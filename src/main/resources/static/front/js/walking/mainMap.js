window.addEventListener("DOMContentLoaded", function() {
    if (typeof mainMapLib.load === 'function') {
        mainMapLib.load("mapId");
    }

    const toggleButton = document.getElementById('toggleButton');
    const infoBox = document.getElementById('infoBox');
    const iframe = document.querySelector('iframe[name="ifrmBoard"]');
    // 초기 상태 설정
    toggleButton.textContent = '<'; // 닫혀 있을 때

    function toggleInfoBox() {
        if (infoBox.classList.contains('info-box-expanded')) {
            infoBox.classList.remove('info-box-expanded');
            toggleButton.style.right = '0px';
            toggleButton.textContent = '<'; // 닫혔을 때
            if (mainMapLib.seq == "" || mainMapLib.seq == null) {
                iframe.style.display = 'none';
            } else {
                iframe.style.display = 'block';
            }
        } else {
            infoBox.classList.add('info-box-expanded');
            toggleButton.style.right = '400px';
            toggleButton.textContent = '>'; // 열렸을 때
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
            if (infoBox.classList.contains('info-box-expanded')) {
                toggleInfoBox();
            }
        }
    });
});
