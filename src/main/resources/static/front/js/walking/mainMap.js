window.addEventListener("DOMContentLoaded", function() {
    if (typeof mainMapLib.load === 'function') {
        mainMapLib.load("mapId");
    }

    const toggleButton = document.getElementById('toggleButton');
    const infoBox = document.getElementById('infoBox');

    // 초기 상태 설정
    toggleButton.textContent = '<'; // 닫혀 있을 때

    toggleButton.addEventListener('click', function() {
        infoBox.classList.toggle('info-box-expanded');
        if (infoBox.classList.contains('info-box-expanded')) {
            toggleButton.style.right = '300px';
            toggleButton.textContent = '>'; // 열렸을 때
        } else {
            toggleButton.style.right = '0px';
            toggleButton.textContent = '<'; // 닫혔을 때
        }
    });
})
