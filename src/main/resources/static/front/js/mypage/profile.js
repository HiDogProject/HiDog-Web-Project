document.addEventListener('DOMContentLoaded', function() {
    var modal = document.getElementById('profileImageModal');
    var btn = document.getElementById('editProfileImageBtn');
    var span = document.getElementsByClassName('close')[0];

    // 프로필 이미지 수정 버튼 클릭 시 팝업 열기
    btn.onclick = function() {
        modal.style.display = 'flex';
    }

    // 팝업 닫기 버튼 클릭 시 팝업 닫기
    span.onclick = function() {
        modal.style.display = 'none';
    }

    // 모달 영역 밖 클릭 시 모달 닫기
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }

    // 폼 제출 후 메시지 표시
    document.getElementById('profileImageForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const formData = new FormData(this);
        fetch('/mypage/profile', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('message').textContent = data.message;
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000); // 2초 후 페이지 리로드
                } else {
                    document.getElementById('message').textContent = data.message;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});