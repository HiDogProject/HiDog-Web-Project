window.addEventListener("DOMContentLoaded", function() {
    const buttonEl = document.querySelector(".contents .more");
    buttonEl.addEventListener("click", function() {
        const el = document.querySelector(".contents");
        el.classList.toggle("off");
    });
});