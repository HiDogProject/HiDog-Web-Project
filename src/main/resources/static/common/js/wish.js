const wishList = {
    /**
     * 추가
     * type - BOARD : 게시판
     * @param seq
     * @param type
     * @param callback 추가 이후 후속 처리
     */
    add(seq, type, callback) {
        this.process(seq, type, 'GET', callback);
    },
    /**
     * 삭제
     * @param seq
     * @param type
     * @param callback
     */
    remove(seq, type, callback) {
        this.process(seq, type, 'DELETE', callback);
    },
    process(seq, type, method, callback) {
        const { ajaxLoad } = commonLib;
        (async() => {
            try {
                await ajaxLoad(`/wish/${type}/${seq}`, method, null, null, 'text');
                // 후속 처리
                if (typeof callback === 'function') {
                    callback(seq, type);
                }
            } catch (err) {
                console.error(err);
            }
        })();
    }
};

window.addEventListener("DOMContentLoaded", function() {
    // 클래스명이 wishlist-toggel이면 동작하도록 구성
    //  on 유 - 추가 상태 , on 무 - 제거 상태
    const els = document.getElementsByClassName("wishlist-toggle");
    for (const el of els) {
        el.addEventListener("click", function() {
            const classList = this.classList; // 클래스 추가, 제거, 토글기능
            const { seq, type } = this.dataset; // seq, type 가져오기 // 비구조화 할당
            if (!seq || !type) { //seq, type 는 필수
                return;
            }

            if (classList.contains("on")) { // 이미 추가 상태이므로 제거 처리
                wishList.remove(seq, type, () => classList.remove("on"));
            } else { // 추가 처리
                wishList.add(seq, type, () => classList.add("on")); // 콜백방식??
            }
        });
    }
});