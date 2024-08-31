window.addEventListener("DOMContentLoaded", function() {
    const buttonEl = document.querySelector(".contents .more");
    const contentEl = document.querySelector(".contents .inner");

    const textLength = contentEl.innerText.length;

    if (textLength < 100) {
        buttonEl.style.display = 'none';
    } else {
        buttonEl.addEventListener("click", function() {
            const el = document.querySelector(".contents");
            el.classList.toggle("off");
        });
    }
});
