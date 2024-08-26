const commonLib = {
    /**
     * ajax 요청 공통 기능 (ajax 요청,응답 편의 함수)
     *
     * @param url : 요청 URL
     * @param method : 요청 방식 - GET, POST, PUT, PATCH, DELETE ...
     * @param data
     * @param headers
     * @param responseType : 응답 데이터 타입(text - text로, 그외는 json)
     * @returns {Promise<unknown>}
     */
    ajaxLoad(url, method = "GET", data, headers, responseType) {
        if (!url) {
            return;
        }

        const csrfToken = document.querySelector("meta[name='csrf_token']")?.content?.trim();
        const csrfHeader = document.querySelector("meta[name='csrf_header']")?.content?.trim();

        if (!/^http[s]?/i.test(url)) {
            let rootUrl = document.querySelector("meta[name='rootUrl']")?.content?.trim() ?? '';
            rootUrl = rootUrl === '/' ? '' : rootUrl;

            url = rootUrl.includes("http") ? rootUrl + url.replace("/", "") : location.protocol + "//" + location.host + url;

            // url = location.protocol + "//" + location.host + rootUrl + url;
        }

        method = method.toUpperCase();
        if (method === 'GET') {
            data = null;
        }

        if (data && !(data instanceof FormData) && typeof data !== 'string' && data instanceof Object) {
            data = JSON.stringify(data);
        }

        if (csrfHeader && csrfToken) {
            headers = headers ?? {};
            headers[csrfHeader] = csrfToken;
        }

        const options = {
            method
        };

        if (data) options.body = data;
        if (headers) options.headers = headers;

        return new Promise((resolve, reject) => {
            fetch(url, options)
                .then(res => responseType === 'text' ? res.text() : res.json()) // res.json() - JSON / res.text() - 텍스트
                .then(data => resolve(data))
                .catch(err => reject(err));
        });
    },
    /**
     * 에디터 로드
     *
     */
    editorLoad(id) {
        if(!ClassicEditor || !id?.trim()) return;

        return ClassicEditor.create(document.getElementById(id.trim()), {});
    }
};

/**
* 이메일 인증 메일 보내기
*
* @param email : 인증할 이메일
*/
commonLib.sendEmailVerify = function(email) {
    const { ajaxLoad } = commonLib;

    const url = `email//verify?email=${email}`;

    ajaxLoad(url, "GET", null, null, "json")
        .then(data => {
            if (typeof callbackEmailVerify == 'function') { // 이메일 승인 코드 메일 전송 완료 후 처리 콜백
                callbackEmailVerify(data);
            }
        })
        .catch(err => console.error(err));
};

/**
* 인증 메일 코드 검증 처리
*
*/
commonLib.sendEmailVerifyCheck = function(authNum) {
    const { ajaxLoad } = commonLib;
    const url = `email//auth_check?authNum=${authNum}`;

    ajaxLoad(url, "GET", null, null, "json")
        .then(data => {
            if (typeof callbackEmailVerifyCheck == 'function') { // 인증 메일 코드 검증 요청 완료 후 처리 콜백
                callbackEmailVerifyCheck(data);
            }
        })
        .catch(err => console.error(err));
};

function user_execDaumPostcode() {
            new daum.Postcode({
                oncomplete: function(data) {
                    var addr = ''; // 주소 변수
                    var extraAddr = ''; // 참고항목 변수

                    if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                        addr = data.roadAddress;
                    } else { // 사용자가 지번 주소를 선택했을 경우(J)
                        addr = data.jibunAddress;
                    }

                    if (data.userSelectedType === 'R') {

                        if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                            extraAddr += data.bname;
                        }

                        if (data.buildingName !== '' && data.apartment === 'Y') {
                            extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                        }

                        if (extraAddr !== '') {
                            extraAddr = ' (' + extraAddr + ')';
                        }

                        document.getElementById("user_extraAddress").value = extraAddr;

                    } else {
                        document.getElementById("user_extraAddress").value = '';
                    }

                    // 우편번호와 주소 정보를 해당 필드에 넣는다.
                    document.getElementById('user_postcode').value = data.zonecode;
                    document.getElementById("user_address").value = addr;
                    // 커서를 상세주소 필드로 이동한다.
                    document.getElementById("user_detailAddress").focus();
                }
            }).open();
        }