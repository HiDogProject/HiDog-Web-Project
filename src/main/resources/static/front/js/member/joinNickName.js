document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("checkUserName").addEventListener("click", function() {
        var userName = document.getElementById("userName").value;
        var resultSpan = document.getElementById("userNameError");

        if (!userName.trim()) {
            resultSpan.textContent = "닉네임을 입력해주세요.";
            resultSpan.style.color = "red";
            return;
        }

        // 요청 URL 설정
        var url = `/member/join/check-username?userName=${encodeURIComponent(userName)}`;

        // ajaxLoad 함수 호출
        commonLib.ajaxLoad(url, "GET", null, null, "json")
            .then(isTaken => {
                if (isTaken) {
                    resultSpan.textContent = "이미 사용 중인 닉네임입니다.";
                    resultSpan.style.color = "red";
                } else {
                    resultSpan.textContent = "사용 가능한 닉네임입니다.";
                    resultSpan.style.color = "green";
                }
            })
            .catch(error => {
                console.error("Error:", error);
                resultSpan.textContent = "검사 중 오류";
                resultSpan.style.color = "red";
            });
    });
});