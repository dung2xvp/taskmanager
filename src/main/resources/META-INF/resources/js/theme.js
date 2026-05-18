(function () {
    const STORAGE_KEY = 'taskflow-theme';
    const root = document.documentElement;

    function getPreferredTheme() {
        const saved = localStorage.getItem(STORAGE_KEY);
        if (saved === 'light' || saved === 'dark') return saved;
        return 'dark';
    }

    function applyTheme(theme) {
        root.dataset.theme = theme;
        localStorage.setItem(STORAGE_KEY, theme);
        document.querySelectorAll('[data-theme-toggle]').forEach(button => {
            const isLight = theme === 'light';
            button.setAttribute('aria-label', isLight ? 'Chuyen sang giao dien toi' : 'Chuyen sang giao dien sang');
            button.setAttribute('title', isLight ? 'Chuyen sang giao dien toi' : 'Chuyen sang giao dien sang');
            button.setAttribute('aria-pressed', String(isLight));
            button.innerHTML = `
                <span class="theme-toggle-track" aria-hidden="true">
                    <span class="theme-toggle-symbol theme-toggle-sun">☀</span>
                    <span class="theme-toggle-symbol theme-toggle-moon">☾</span>
                    <span class="theme-toggle-thumb"></span>
                </span>
            `;
        });
    }

    function createToggle() {
        if (document.querySelector('[data-theme-toggle]')) return;

        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'btn btn-ghost btn-sm theme-toggle';
        button.dataset.themeToggle = 'true';
        button.addEventListener('click', () => {
            applyTheme(root.dataset.theme === 'light' ? 'dark' : 'light');
        });

        const isAuthPage = Boolean(document.querySelector('.login-page'));
        const target = isAuthPage
            ? null
            : document.querySelector('.board-navbar-right, .navbar-right, .navbar-actions, .nav-actions');
        if (target) {
            target.insertBefore(button, target.firstChild);
        } else {
            button.classList.add('theme-toggle-floating');
            document.body.appendChild(button);
        }

        applyTheme(root.dataset.theme || getPreferredTheme());
    }

    applyTheme(getPreferredTheme());

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', createToggle);
    } else {
        createToggle();
    }
})();
