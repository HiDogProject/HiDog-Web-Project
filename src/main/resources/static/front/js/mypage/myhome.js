document.addEventListener('DOMContentLoaded', function() {
    const editProfileBtn = document.getElementById('editProfileBtn');
    const profilePopup = document.getElementById('profilePopup');
    const cancelBtn = document.getElementById('cancelBtn');
    const uploadForm = document.getElementById('uploadForm');

    // 프로필 수정 버튼 클릭 시 팝업 열기
    editProfileBtn.addEventListener('click', function() {
        profilePopup.style.display = 'block';
    });

    // 취소 버튼 클릭 시 팝업 닫기
    cancelBtn.addEventListener('click', function() {
        profilePopup.style.display = 'none';
    });

    // 폼 제출 시 팝업 닫기 및 프로필 이미지 업데이트
    uploadForm.addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        const formData = new FormData(uploadForm);

        fetch(uploadForm.action, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('profileImage').src = data.imageUrl; // 이미지 URL 업데이트
                    alert(data.successMessage);
                    profilePopup.style.display = 'none'; // 팝업 닫기
                } else {
                    alert(data.errorMessage);
                }
            })
            .catch(error => console.error('Error:', error));
    });
});