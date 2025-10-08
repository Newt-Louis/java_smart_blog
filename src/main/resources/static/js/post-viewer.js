/** @type {import('quill')} */
const Quill = window.Quill;
/** @type {import('dompurify')} */
const DOMPurify = window.DOMPurify;

let quillUpdatePost;
const titleInput = document.getElementById("quill_title_update_post");
const idInput = document.getElementById("hiddenId_updatePost");
document.addEventListener("DOMContentLoaded", () => {
    fetchPosts().catch(e => console.error(e));
    EventBus.on("post:saved",refreshPosts);
    EventBus.on("post:deleted",refreshPosts);
    EventBus.on("post:updated",refreshPosts);
    quillUpdatePost = new Quill('#quill_editor_update_post', {
        modules: {
            toolbar: '#toolbar_update_post',
            imageResize: {
                displaySize: true,
                modules: ['Resize', 'DisplaySize', 'Toolbar']
            }
        },
        theme: 'snow'
    });
    document.getElementById("clearContentBtnUpdatePost").addEventListener("click", () => {
        quillUpdatePost.setContents([]);
    });
});

async function fetchPosts(){
    let data = null;
    try {
        const res = await fetch("/api/posts", {
            method: "get",
            headers: {
                "Content-Type": "application/json"
            },
        });
        if (res.status === 200) {
            data = await res.json();
        } else {
            return;
        }
    } catch (e) {
        console.error(e);
        return;
    }

    if (!(data?.content instanceof Array)) {
        console.log("Data phải là Pageable",data);
        return;
    }

    const tpl = document.getElementById("post-card");
    const postContainer = document.getElementById("post-container");

    data.content.forEach(post => {
        const node = tpl.content.cloneNode(true);
        node.querySelector(".card-title").textContent = post.title == null ? "Not have Title yet" : post.title;
        node.querySelector(".post-content").innerHTML = DOMPurify.sanitize(post.content);
        node.querySelector(".btn-edit-post").dataset.id = post.id;
        node.querySelector(".btn-delete-post").dataset.id = post.id;
        node.querySelector(".btn-edit-post").addEventListener("click", (e) => {
            openModalEdit(post.id,e.target);
        });
        node.querySelector(".btn-delete-post").addEventListener("click", () => {
            deletePost(post.id);
        });
        postContainer.appendChild(node);
    });
}

async function openModalEdit(postId,relatedTarget){
    if (!postId){
        console.error("Must have post id !!!");
        return;
    }
    const modalElement = document.getElementById("modal-edit-post");
    const modalBody = modalElement.querySelector(".modal-body");
    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);

    modal.show(relatedTarget);

    try {
        const postData = await fetchPost(postId);
        if (postData == null) {
            modalBody.innerHTML = '<p class="text-danger text-center">Error: Post not found!</p>';
            return;
        }

        titleInput.value = postData.title ?? "Not have Title yet";
        idInput.value = postData.id ?? postId;
        quillUpdatePost.setContents([]);
        quillUpdatePost.clipboard.dangerouslyPasteHTML(0,postData.content,Quill.sources.API);

        const btnUpdatePost = modalElement.querySelector("#btn_update_post");
        btnUpdatePost.replaceWith(btnUpdatePost.cloneNode(true));

        const newBtn = modalElement.querySelector("#btn_update_post");
        newBtn.addEventListener("click", async () => {
            const originalHTML = newBtn.innerHTML;
            newBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Updating...`;
            newBtn.disabled = true;
            try {
                const status = await updatePost();
                if (status === 200) {
                    modal.hide();
                }
            } catch (e) {
                console.error("Update failed", e);
            } finally {
                newBtn.innerHTML = originalHTML;
                newBtn.disabled = false;
            }
        });
    } catch (e) {
        console.error("Failed to load post editing",e);
        modalBody.innerHTML = '<p class="text-danger text-center">Failed to load post data. Please try again.</p>';
    }
}

async function fetchPost(postId){
    try {
        const res = await fetch("/api/post/" + postId,{
            method: "get",
            headers: {}
        });
        if (res.status === 200) {
            return await res.json();
        }
    } catch (e) {
        console.error(e);
    }
    return null;
}

async function updatePost(){
    const formUpdatePost = document.getElementById("savePostContentFormUpdate");
    const hiddenInputTitle = formUpdatePost.querySelector("#hiddenTitle_updatePost");
    const hiddenInputContent = formUpdatePost.querySelector("#hiddenContent_updatePost");
    hiddenInputTitle.value = titleInput.value;
    hiddenInputContent.value = quillUpdatePost.root.innerHTML;
    const formData = new FormData(formUpdatePost);
    try {
        const res = await fetch("/api/update-post", {method: "put", headers: {}, body: formData});
        if (res.status === 200) {
            EventBus.emit("post:updated");
        }
        return res.status;
    } catch (e) {
        console.error(e);
        throw e;
    }
}

async function deletePost(id){
    try {
        const res = await fetch(`/api/delete-post/${id}`, {
            method: "delete",
            headers: {}
        });
        if (res.status === 200) {
            EventBus.emit("post:deleted");
        }
    } catch (e) {
        console.error(e);
    }
}

function refreshPosts() {
    console.log("Reloading posts...");
    const postContainer = document.getElementById("post-container");
    postContainer.innerHTML = "";
    fetchPosts().catch(e => console.error(e));
}