/**
 * 프로필 이미지 업로드 후속 처리
 *
 */
function fileUploadCallback(files) {
    if (files.length === 0) {
        return;
    }

    const targetEl = document.querySelector(".profile-image");

    // 현재 이미지 박스에 추가된 모든 이미지와 삭제 버튼들을 지우지 않고 유지
    // 각 파일을 순회하며 새로운 HTML을 생성하여 추가합니다
    files.forEach(file => {
        let html = document.getElementById("image-file-tpl").innerHTML;
        html = html.replace(/\[seq\]/g, file.seq)
            .replace(/\[fileUrl\]/g, file.fileUrl);

        const domParser = new DOMParser();
        const dom = domParser.parseFromString(html, 'text/html');
        const box = dom.querySelector(".image-file-box");

        // 새로운 이미지 박스를 추가합니다
        targetEl.append(box);

        const removeEl = box.querySelector(".remove");
        removeEl.addEventListener("click", function() {
            if (!confirm('정말 삭제 하겠습니까?')) {
                return;
            }

            const seq = this.dataset.seq;
            fileManager.delete(seq);
        });
    });
}

/**
 * 파일 삭제 후 후속 처리
 *
 */
function fileDeleteCallback(file) {
    const targetEl = document.querySelector(".profile-image");
    if (targetEl) {
        // 파일이 삭제되었을 때 그에 해당하는 이미지를 찾아서 제거합니다
        const boxToRemove = targetEl.querySelector(`.image-file-box[data-seq="${file.seq}"]`);
        if (boxToRemove) {
            targetEl.removeChild(boxToRemove);
        }
    }
}
