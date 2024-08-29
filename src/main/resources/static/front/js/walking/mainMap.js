window.addEventListener("DOMContentLoaded", function() {
    if (typeof mainMapLib.load === 'function') {
        mainMapLib.load("mapId");
    }

    const toggleButton = document.getElementById('toggleButton');
    const infoBox = document.getElementById('infoBox');
    const iframe = document.querySelector('iframe[name="ifrmBoard"]');
    const infoSeq = document.querySelector('.info-seq');
    // 초기 상태 설정
    toggleButton.textContent = '<'; // 닫혀 있을 때

    toggleButton.addEventListener('click', function() {
        if (infoBox.classList.contains('info-box-expanded')) {
            infoBox.classList.remove('info-box-expanded');
            toggleButton.style.right = '0px';
            toggleButton.textContent = '<'; // 닫혔을 때
            if (mainMapLib.seq == "" || mainMapLib.seq == null) {
                iframe.style.display = 'none';
                infoSeq.style.display = 'none';
            } else {
                iframe.style.display = 'block';
                infoSeq.style.display = 'block';
            }
        } else {
            infoBox.classList.add('info-box-expanded');
            toggleButton.style.right = '300px';
            toggleButton.textContent = '>'; // 열렸을 때
            if (mainMapLib.seq == "" || mainMapLib.seq == null) {
                iframe.style.display = 'none';
                infoSeq.style.display = 'none';
            } else {
                iframe.style.display = 'block';
                infoSeq.style.display = 'block';
            }
        }
    });
})
