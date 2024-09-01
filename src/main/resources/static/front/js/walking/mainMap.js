window.addEventListener("DOMContentLoaded", function() {
    if (typeof mainMapLib.load === 'function') {
        mainMapLib.load("mapId");
    }

    const toggleButton = document.getElementById('toggleButton');
    const infoBox = document.getElementById('infoBox');
    const iframe = document.querySelector('iframe[name="ifrmBoard"]');
    const map = document.getElementById('mapId');

    // 초기 상태 설정
    toggleButton.textContent = '<'; // 닫혀 있을 때
    infoBox.style.transform = 'translateX(100%)'; // 초기에는 화면 밖으로 이동

    function toggleInfoBox() {
        if (infoBox.style.transform === 'translateX(100%)') {
            infoBox.style.transform = 'translateX(0)';
            toggleButton.style.right = '375px';
            toggleButton.textContent = '>'; // 열렸을 때
            if (mainMapLib.seq == "" || mainMapLib.seq == null) {
                iframe.style.display = 'none';
            } else {
                iframe.style.display = 'block';
            }
        } else {
            infoBox.style.transform = 'translateX(100%)';
            toggleButton.style.right = '0px';
            toggleButton.textContent = '<'; // 닫혔을 때
            if (mainMapLib.seq == "" || mainMapLib.seq == null) {
                iframe.style.display = 'none';
            } else {
                iframe.style.display = 'block';
            }
        }
    }

    toggleButton.addEventListener('click', toggleInfoBox);

    window.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            if (toggleButton.style.right === '375px') {
                toggleInfoBox();
            }
        }
    });
});
