window.addEventListener("DOMContentLoaded", function() {
    const buttonEl = document.querySelector(".contents .more");
    const contentEl = document.querySelector(".contents .inner");

    // 글자 수 계산
    const textLength = contentEl.innerText.length;

    // 글자 수가 100자 미만이면 버튼 숨기기
    if (textLength < 100) {
        buttonEl.style.display = 'none';
    } else {
        buttonEl.addEventListener("click", function() {
            const el = document.querySelector(".contents");
            el.classList.toggle("off");
        });
    }
});
