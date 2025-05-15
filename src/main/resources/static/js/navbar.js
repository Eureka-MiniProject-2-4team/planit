fetch('/html/navbar.html')
    .then(res => res.text())
    .then(html => {
        document.getElementById('navbar-container').innerHTML = html;

        const currentPath = window.location.pathname;
        document.querySelectorAll('.nav-item').forEach(item => {
            const href = item.getAttribute('href');
            const link = new URL(href, window.location.origin).pathname;
            if (link === currentPath) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });
    });
