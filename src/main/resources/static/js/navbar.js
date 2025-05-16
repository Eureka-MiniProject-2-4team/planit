fetch('/html/navbar.html')
    .then(res => res.text())
    .then(html => {
        document.getElementById('navbar-container').innerHTML = html;

        // 현재 경로 활성화 표시
        const currentPath = window.location.pathname;
        document.querySelectorAll('.nav-item').forEach(item => {
            const href = item.getAttribute('href');
            if (!href) return;
            const link = new URL(href, window.location.origin).pathname;
            if (link === currentPath) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

        // ✅ 로그아웃 버튼 이벤트 연결 (fetch 이후!)
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                localStorage.removeItem('accessToken');
                window.location.href = '/html/auth/login.html';
            });
        }
    });
