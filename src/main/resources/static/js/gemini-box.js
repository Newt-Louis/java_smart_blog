document.addEventListener('DOMContentLoaded', function () {
    const popoverTriggerBtn = document.getElementById('openGeminiPopoverBtn');
    const popoverHtmlContent = document.getElementById('geminiPopoverContent');

    // 1. Khởi tạo Popover
    const popover = new bootstrap.Popover(popoverTriggerBtn, {
        container: 'body',      // Quan trọng: giúp popover không bị cắt xén
        html: true,             // Quan trọng: cho phép nội dung là HTML
        placement: 'top',       // Vị trí của popover (top, bottom, left, right)
        title: '<i class="fa-solid fa-wand-magic-sparkles"></i> Generate Content',
        customClass: 'gemini-popover', // Class CSS tùy chỉnh
        // Lấy nội dung từ div ẩn
        content: function () {
            return popoverHtmlContent.content.cloneNode(true);
        }
    });

    // 2. Lắng nghe sự kiện click trên toàn bộ trang (Event Delegation)
    document.addEventListener('click', function (event) {
        const target = event.target;

        if (target.closest('#generateContentBtn')) {
            handleGenerateContentClick(target);
            return;
        }

        if (target.closest('#insertToEditorBtn')){
            handleInsertToEditorClick(target);
            return;
        }

        hidePopoverWhenClickOutsideContent(target);
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
        const popoverBody = target.closest('.popover-body');
        const resultDiv = popoverBody.querySelector('#geminiResult');
        const generatedHtml = resultDiv.innerHTML;

        if (generatedHtml) {
            const currentContent = quill.root.innerHTML;
            quill.root.innerHTML = currentContent + generatedHtml;
            popover.hide(); // Ẩn popover sau khi chèn
        }
    }

    function hidePopoverWhenClickOutsideContent(target){
        if (!popoverTriggerBtn.contains(target) && !document.querySelector('.popover')?.contains(target)) {
            popover.hide();
        }
    }
});