/**
 * 파일 업로드 후속 처리
 *
 */
function callbackFileUpload(files) {
    if (!files || files.length == 0) {
        return;
    }

    const imageUrls = [];

    const editorTpl = document.getElementById("editor_tpl").innerHTML;
    const attachTpl = document.getElementById("attach_tpl").innerHTML;

    const editorFiles = document.getElementById("editor_files");
    const attachFiles = document.getElementById("attach_files");

    const domParser = new DOMParser();

    for (const file of files) {
        const location = file.location;

        let html = location == 'editor' ? editorTpl : attachTpl;
        const targetEl = location == 'editor' ? editorFiles : attachFiles;

        if (location == 'editor') {
            imageUrls.push(file.fileUrl);
        }

        html = html.replace(/\[seq\]/g, file.seq)
            .replace(/\[fileName\]/g, file.fileName)
            .replace(/\[imageUrl\]/g, file.fileUrl);

        const dom = domParser.parseFromString(html, "text/html");
        const fileBox = dom.querySelector(".file_tpl_box");

        targetEl.appendChild(fileBox);

        const insertImageEl = fileBox.querySelector(".insert_image");

        if (insertImageEl) insertImageEl.addEventListener("click", () => insertImage(file.fileUrl));
    }

    if (imageUrls.length > 0) insertImage(imageUrls);

}

/**
 * 파일 삭제후 후속 처리
 *
 */
function callbackFileDelete(seq) {
    const fileBox = document.getElementById(`file_${seq}`);
    fileBox.parentElement.removeChild(fileBox);

}