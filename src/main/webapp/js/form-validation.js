document.querySelectorAll("[data-nonblank]").forEach((field) => {
    const validate = () => {
        const onlyWhitespace = field.value.length > 0 && !field.value.trim();
        field.setCustomValidity(onlyWhitespace ? "Vui lòng nhập nội dung." : "");
    };
    field.addEventListener("input", validate);
    validate();
});

document.querySelectorAll("[data-match]").forEach((field) => {
    const original = field.form.elements.namedItem(field.dataset.match);
    const validate = () => {
        const mismatch = field.value && field.value !== original.value;
        field.setCustomValidity(mismatch ? "Mật khẩu xác nhận không khớp." : "");
    };
    field.addEventListener("input", validate);
    original.addEventListener("input", validate);
    validate();
});

document.querySelectorAll("input[type=file][data-max-mb]").forEach((field) => {
    field.addEventListener("change", () => {
        const maxMb = Number(field.dataset.maxMb);
        const tooLarge = field.files[0]?.size > maxMb * 1024 * 1024;
        field.setCustomValidity(tooLarge ? `Tệp vượt quá dung lượng tối đa ${maxMb} MB.` : "");
    });
});
