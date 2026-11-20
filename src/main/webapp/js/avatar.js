document.querySelectorAll('img[data-avatar-fallback]').forEach((image) => {
    const showDefaultAvatar = () => {
        const fallback = image.dataset.avatarFallback;
        if (!fallback) return;
        delete image.dataset.avatarFallback;
        image.src = fallback;
    };

    image.addEventListener('error', showDefaultAvatar, { once: true });
    if (image.complete && image.naturalWidth === 0) showDefaultAvatar();
});
