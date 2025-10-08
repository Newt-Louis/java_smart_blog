document.addEventListener('DOMContentLoaded', function () {
    let activeQuillInstance = null;
    let activePopoverTrigger = null;
    const popoverTemplate = document.getElementById('geminiPopoverTemplate');
    if (!popoverTemplate) {
        console.error('Gemini Popover template not found!');
        return;
    }

    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');

    // 1. Khởi tạo Popover
    const popovers = [...popoverTriggerList].map(popoverTriggerEl => {
        const popover = new bootstrap.Popover(popoverTriggerEl, {
            container: 'body',
            html: true,
            placement: 'top',
            title: '<i class="fa-solid fa-wand-magic-sparkles"></i> Generate Content',
            customClass: 'gemini-popover',
            content: function () {
                // Luôn tạo bản sao mới từ template
                return popoverTemplate.content.cloneNode(true);
            }
        });
        popoverTriggerEl.addEventListener('show.bs.popover', function (event) {
            bootstrap.Popover.getInstance(activePopoverTrigger)?.hide();
            activePopoverTrigger = event.target;
            const targetEditorSelector = event.target.dataset.targetEditor;
            const editorElement = document.querySelector(targetEditorSelector);

            if (editorElement) {
                // Dùng API của Quill để tìm instance đang gắn với DOM element đó
                activeQuillInstance = Quill.find(editorElement);
            } else {
                console.error('Target Quill editor not found:', targetEditorSelector);
                activeQuillInstance = null;
            }
        });

        // Khi popover ĐÃ ĐƯỢC ẨN
        popoverTriggerEl.addEventListener('hidden.bs.popover', function () {
            // Dọn dẹp biến trạng thái
            activePopoverTrigger = null;
            activeQuillInstance = null;
        });

        return popover;
    });

    // 2. Lắng nghe sự kiện click trên toàn bộ trang (Event Delegation)
    document.addEventListener('click', function (event) {
        const target = event.target;

        // Nếu click bên trong popover
        const popoverBody = target.closest('.popover-body');
        if (popoverBody) {
            if (target.closest('#generateContentBtn')) {
                handleGenerateContentClick(target);
            } else if (target.closest('#insertToEditorBtn')) {
                handleInsertToEditorClick(target);
            }
            return;
        }

        // Nếu click bên ngoài popover và không phải là nút trigger đang active
        if (activePopoverTrigger && !activePopoverTrigger.contains(target)) {
            bootstrap.Popover.getInstance(activePopoverTrigger).hide();
        }
    });

    function handleGenerateContentClick(target) {
        const popoverBody = target.closest('.popover-body');
        const promptInput = popoverBody.querySelector('#geminiPrompt');
        const resultDiv = popoverBody.querySelector('#geminiResult');
        const btnText = popoverBody.querySelector('#generateBtnText');
        const spinner = popoverBody.querySelector('#loadingSpinner');

        const promptText = promptInput.value.trim();
        if (!promptText) {
            alert('Please enter a prompt!');
            return;
        }

        target.disabled = true;
        btnText.textContent = 'Generating...';
        spinner.classList.remove('d-none');

        // Gọi AJAX (giữ nguyên logic fetch)
        fetch('/api/gemini', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({prompt: promptText})
        })
            .then(response => response.json())
            .then(data => {
                resultDiv.innerHTML = data.generatedContent;
            })
            .catch(error => {
                console.error('Error:', error);
                resultDiv.textContent = 'An error occurred.';
            })
            .finally(() => {
                target.disabled = false;
                btnText.textContent = 'Generate';
                spinner.classList.add('d-none');
            });

        console.log('Generate button clicked!');
    }

    function handleInsertToEditorClick(target) {
        if (!activeQuillInstance) {
            console.error('Cannot insert content: No active Quill editor instance found.');
            alert('Error: Could not find the editor to insert content into.');
            return;
        }
        const popoverBody = target.closest('.popover-body');
        const resultDiv = popoverBody.querySelector('#geminiResult');
        const generatedHtml = resultDiv.innerHTML;

        if (generatedHtml) {
            const currentContent = activeQuillInstance.root.innerHTML;
            activeQuillInstance.root.innerHTML = currentContent + generatedHtml;

            // Tìm và ẩn popover đang mở
            const popoverInstance = bootstrap.Popover.getInstance(activePopoverTrigger);
            if (popoverInstance) {
                popoverInstance.hide();
            }
        }
    }
});