// /js/friend/friend.js

// 1. JWT 헤더 생성
function authHeaders() {
    const raw = localStorage.getItem('accessToken') || '';
    const token = raw.replace(/^Bearer\s+/i, '');
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    };
}

// 2. 별 생성 (변경 없음)
function createStars() {
    const container = document.getElementById('stars');
    if (!container) return;
    for (let i = 0; i < 150; i++) {
        const star = document.createElement('div');
        star.className = 'star';
        const size = Math.random() * 3 + 1;
        star.style.width = star.style.height = `${size}px`;
        star.style.left = `${Math.random() * 100}%`;
        star.style.top = `${Math.random() * 100}%`;
        star.style.animationDelay = `${Math.random() * 5}s`;
        container.appendChild(star);
    }
}

// 3. DOMContentLoaded 에 초기화
window.addEventListener('DOMContentLoaded', () => {
    createStars();
    setupNavbar();
    setupFriendModal();
    setupRefreshButton();
    getFriends();
});

// 4. 네비게이션 바 셋업 (active 토글 + 친구 목록 갱신)
function setupNavbar() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', e => {
            e.preventDefault();
            document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            getFriends();
        });
    });
}

// 5. 친구 추가 모달 셋업
function setupFriendModal() {
    const modal = document.getElementById('friend-modal');
    if (!modal) return;

    document.getElementById('add-friend-btn').addEventListener('click', () => {
        modal.style.display = 'flex';
        document.getElementById('search-results').innerHTML = '';
        document.getElementById('username-input').value = '';
    });
    document.getElementById('close-modal').addEventListener('click', () => modal.style.display = 'none');
    document.getElementById('cancel-btn').addEventListener('click', () => modal.style.display = 'none');
    document.getElementById('search-btn').addEventListener('click', searchUser);
    document.getElementById('search-friend-form').addEventListener('submit', e => {
        e.preventDefault();
        searchUser();
    });
}

// 6. 사용자 검색
async function searchUser() {
    const username = document.getElementById('username-input').value.trim();
    if (!username) {
        return alert('검색할 사용자 이름을 입력해주세요.');
    }

    // 예시: /api/users/{username} 으로 단일 UserSearchDto 반환
    const res = await fetch(`/api/users/${encodeURIComponent(username)}`, {
        headers: authHeaders()
    });
    const { result, data } = await res.json();
    const container = document.getElementById('search-results');
    container.innerHTML = '';

    if (result === 'SUCCESS' && data) {
        const user = data;
        const div = document.createElement('div');
        div.className = 'search-result-item';
        div.innerHTML = `
            <div class="search-result-info">
                <div class="search-result-avatar">${user.nickName?.charAt(0) || 'U'}</div>
                <div class="search-result-name">${user.nickName || user.userName}</div>
            </div>
            <button class="request-btn" data-user-id="${user.id}">
                <i class="fas fa-user-plus"></i> 요청하기
            </button>
        `;
        container.appendChild(div);
        setupRequestButtons();
    } else {
        container.innerHTML = '<div class="no-results">검색 결과가 없습니다.</div>';
    }
}

// 7. 친구 요청 보내기
function setupRequestButtons() {
    document.querySelectorAll('.request-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const receiverId = btn.getAttribute('data-user-id');
            const res = await fetch('/api/friend', {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify({ receiverId })
            });
            const { result, message } = await res.json();
            alert(message || (result === 'SUCCESS' ? '요청 성공' : '요청 실패'));
            if (result === 'SUCCESS') {
                document.getElementById('friend-modal').style.display = 'none';
                getFriends();
            }
        });
    });
}

// 8. 친구 목록 조회 & 렌더링
async function getFriends() {
    const res = await fetch('/api/friend', {
        method: 'GET',
        headers: authHeaders()
    });
    const { result, data } = await res.json();
    const list = document.getElementById('friends-list');
    list.innerHTML = '';

    if (result === 'SUCCESS' && Array.isArray(data.friends) && data.friends.length) {
        data.friends.forEach(f => {
            const item = document.createElement('div');
            item.className = 'friend-item';
            item.innerHTML = `
                <div class="friend-info">
                    <div class="friend-name">${f.receiverNickName}</div>
                    <small class="text-muted">(${f.receiverEmail})</small>
                    <span class="badge bg-secondary ms-2">${f.status}</span>
                </div>
                <div class="friend-actions">
                    <button class="action-btn delete-btn" data-friend-id="${f.id}">
                        <i class="fas fa-trash-alt"></i> 삭제
                    </button>
                </div>
            `;
            list.appendChild(item);
        });
        setupDeleteButtons();
    } else {
        list.innerHTML = '<div class="friend-item">친구가 없습니다.</div>';
    }
}

// 9. 친구 삭제 버튼 이벤트
function setupDeleteButtons() {
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            if (!confirm('정말 친구를 삭제하시겠습니까?')) return;
            const id = btn.getAttribute('data-friend-id');
            const res = await fetch(`/api/friend/${id}`, {
                method: 'DELETE',
                headers: authHeaders()
            });
            const { message } = await res.json();
            alert(message || '삭제 완료');
            getFriends();
        });
    });
}

// 10. 새로고침 버튼 셋업
function setupRefreshButton() {
    const btn = document.getElementById('refresh-btn');
    if (btn) btn.addEventListener('click', getFriends);
}
