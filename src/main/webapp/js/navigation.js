(() => {
    const header = document.querySelector('.app-header');
    const toggle = header?.querySelector('.nav-toggle');
    const navigation = header?.querySelector('#main-navigation');
    if (!toggle || !navigation) return;

    toggle.hidden = false;
    header.classList.add('navigation-ready');
    const closeMenu = () => {
        toggle.setAttribute('aria-expanded', 'false');
        header.classList.remove('menu-open');
    };
    toggle.addEventListener('click', () => {
        const expanded = toggle.getAttribute('aria-expanded') !== 'true';
        toggle.setAttribute('aria-expanded', String(expanded));
        header.classList.toggle('menu-open', expanded);
    });
    navigation.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeMenu();
            toggle.focus();
        }
    });

    const path = window.location.pathname.replace(/\/$/, '');
    const links = [...navigation.querySelectorAll('a[href]')];
    const current = links.filter((link) => {
        const linkPath = new URL(link.href).pathname;
        return path === linkPath || path.startsWith(linkPath + '/');
    }).sort((a, b) => b.pathname.length - a.pathname.length)[0];
    if (current) current.setAttribute('aria-current', 'page');
})();
