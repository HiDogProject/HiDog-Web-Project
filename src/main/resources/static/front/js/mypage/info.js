function fileUploadCallback(files) {
    if (files.length === 0) return;

    const el = document.querySelector(".profile-image");
    if (!el) {
        return;
    }

    el.style.backgroundImage = "url('" + files[0].fileUrl + "') no-repeat center center;";
    // el.style.backgroundSize = "cover";
}