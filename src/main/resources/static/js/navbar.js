fetch('/html/navbar.html')
    .then(res => res.text())
    .then(html => {
        document.getElementById('navbar-container').innerHTML = html;

        // 팀 링크 동적 설정
        setupTeamNavLink();

        // 현재 경로 활성화 표시
        highlightCurrentPath();

        // ✅ 로그아웃 버튼 이벤트 연결
        setupLogoutButton();
    });

// 팀 네비게이션 링크 설정
function setupTeamNavLink() {
    // 팀 메뉴 아이템 찾기 - href 속성으로 식별
    const teamNavItem = document.querySelector('.nav-item[href="/html/team/team.html"]');
    if (!teamNavItem) return;

    // 현재 URL이 이미 팀 캘린더인지 확인
    const currentPath = window.location.pathname;
    const isTeamCalendar = currentPath.includes('/html/todo/teamCalendar.html');

    // 현재 URL에서 팀 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const currentTeamId = urlParams.get('teamId');

    // 팀 ID가 URL에 있으면 저장 (나중에 팀 선택 후 다시 사용할 수 있도록)
    if (currentTeamId) {
        localStorage.setItem('lastViewedTeamId', currentTeamId);
    }

    // 팀 메뉴 클릭 이벤트 핸들러 - 항상 팀 목록 페이지로 이동
    teamNavItem.addEventListener('click', function(e) {
        e.preventDefault();
        // 무조건 팀 목록 페이지로 이동
        window.location.href = '/html/team/team.html';
    });
}

// 현재 경로 활성화 표시
function highlightCurrentPath() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(item => {
        const href = item.getAttribute('href');
        if (!href) return;

        // 팀 캘린더 페이지일 경우 팀 메뉴 활성화
        if (currentPath.includes('/html/todo/teamCalendar.html') &&
            href.includes('/html/team/team.html')) {
            item.classList.add('active');
        }
        // 일반적인 경로 일치 확인
        else {
            const link = new URL(href, window.location.origin).pathname;
            if (link === currentPath) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        }
    });
}

// 로그아웃 버튼 이벤트 설정
function setupLogoutButton() {
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.removeItem('accessToken');
            window.location.href = '/html/auth/login.html';
        });
    }
}