// 1. JWT 헤더 생성
function authHeaders() {
    const raw = localStorage.getItem('accessToken') || '';
    const token = raw.replace(/^Bearer\s+/i, '');
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    };
}

// 2. 별 생성
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

// 저장된 친구 목록 (검색 기능용)
let friendsList = [];

// 3. DOMContentLoaded 에 초기화
window.addEventListener('DOMContentLoaded', () => {
    createStars();
    setupNavbar();
    setupFriendModal();
    setupTodoModal();
    setupSentRequestsModal();
    setupSentRequestsButton();
    setupFriendSearch();
    getFriends();
    getFriendRequests();
});

// 4. 네비게이션 바 셋업 (active 토글 + 친구 목록 갱신)
function setupNavbar() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', e => {
            e.preventDefault();
            document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            getFriends();
            getFriendRequests();
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

// 6. 투두 리스트 모달 셋업
function setupTodoModal() {
    const modal = document.getElementById('todo-modal');
    if (!modal) return;

    document.getElementById('close-todo-modal').addEventListener('click', () => modal.style.display = 'none');
}

// 7. 보낸 요청 모달 셋업
function setupSentRequestsModal() {
    const modal = document.getElementById('sent-requests-modal');
    if (!modal) return;

    document.getElementById('close-sent-modal').addEventListener('click', () => modal.style.display = 'none');
}

// 8. 사용자 검색
async function searchUser() {
    const username = document.getElementById('username-input').value.trim();
    if (!username) {
        return alert('검색할 사용자 이름을 입력해주세요.');
    }

    try {
        const res = await fetch(`/api/users/${encodeURIComponent(username)}`, {
            headers: authHeaders()
        });
        const result = await res.json();
        const container = document.getElementById('search-results');
        container.innerHTML = '';

        if (result.result === 'SUCCESS' && result.data) {
            const user = result.data;
            const div = document.createElement('div');
            div.className = 'search-result-item';

            // 친구 상태에 따른 버튼 처리
            let buttonHtml = '';
            if (user.isMe) {
                buttonHtml = `<button class="request-btn" disabled>본인입니다</button>`;
            } else if (['PENDING', 'ACCEPTED'].includes(user.friendStatus)) {
                buttonHtml = `<button class="request-btn" disabled>요청 불가 (이미 존재)</button>`;
            } else if (['REJECTED', 'AUTO_CANCELLED'].includes(user.friendStatus)) {
                buttonHtml = `<button class="request-btn" disabled>친구 요청 불가</button>`;
            } else {
                buttonHtml = `<button class="request-btn" data-user-id="${user.id}"><i class="fas fa-user-plus"></i> 요청하기</button>`;
            }

            div.innerHTML = `
                <div class="search-result-info">
                    <div class="search-result-avatar">${user.nickName?.charAt(0) || 'U'}</div>
                    <div>
                        <div class="search-result-name">${user.nickName || user.userName}</div>
                        <div class="friend-email">${user.email || ''}</div>
                    </div>
                </div>
                ${buttonHtml}
            `;
            container.appendChild(div);
            setupRequestButtons();
        } else {
            container.innerHTML = '<div class="no-results">검색 결과가 없습니다.</div>';
        }
    } catch (error) {
        console.error('사용자 검색 중 오류:', error);
        alert('사용자 검색 중 오류가 발생했습니다.');
    }
}

// 9. 친구 요청 보내기
function setupRequestButtons() {
    document.querySelectorAll('.request-btn:not([disabled])').forEach(btn => {
        btn.addEventListener('click', async () => {
            const receiverId = btn.getAttribute('data-user-id');
            try {
                const res = await fetch('/api/friend', {
                    method: 'POST',
                    headers: authHeaders(),
                    body: JSON.stringify({ receiverId })
                });
                const result = await res.json();
                alert(result.message || (result.result === 'SUCCESS' ? '요청 성공' : '요청 실패'));
                if (result.result === 'SUCCESS') {
                    document.getElementById('friend-modal').style.display = 'none';
                }
            } catch (error) {
                console.error('친구 요청 중 오류:', error);
                alert('친구 요청 중 오류가 발생했습니다.');
            }
        });
    });
}

// 10. 친구 목록 조회 & 렌더링
async function getFriends() {
    try {
        const res = await fetch('/api/friend', {
            method: 'GET',
            headers: authHeaders()
        });
        const result = await res.json();

        // 전역 변수에 친구 목록 저장 (검색용)
        friendsList = result.result === 'SUCCESS' && result.data?.friends ? [...result.data.friends] : [];

        renderFriendsList(friendsList);
    } catch (error) {
        console.error('친구 목록 조회 중 오류:', error);
        document.getElementById('friends-list').innerHTML = '<div class="friend-item">친구 목록을 불러오는 중 오류가 발생했습니다.</div>';
    }
}

// 11. 친구 목록 렌더링 함수
function renderFriendsList(friends) {
    const list = document.getElementById('friends-list');
    list.innerHTML = '';

    if (friends && friends.length) {
        friends.forEach(f => {
            const item = document.createElement('div');
            item.className = 'friend-item';
            item.innerHTML = `
                <div class="friend-avatar">${f.receiverNickName?.charAt(0) || 'U'}</div>
                <div class="friend-info">
                    <div class="friend-name">${f.receiverNickName}</div>
                    <div class="friend-email">${f.receiverEmail || ''}</div>
                </div>
                <div class="friend-actions">
                    <button class="action-btn view-btn" data-friend-id="${f.receiverId}" data-friend-name="${f.receiverNickName}">
                        <i class="fas fa-list-check"></i>
                    </button>
                    <button class="action-btn delete-btn" data-friend-id="${f.id}">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </div>
            `;
            list.appendChild(item);
        });
        setupDeleteButtons();
        setupViewTodoButtons();
    } else {
        list.innerHTML = '<div class="friend-item">친구가 없습니다.</div>';
    }
}

// 12. 친구 검색 기능 셋업
function setupFriendSearch() {
    const searchInput = document.getElementById('friend-search-input');
    if (!searchInput) return;

    searchInput.addEventListener('input', () => {
        const searchText = searchInput.value.trim().toLowerCase();

        if (!searchText) {
            renderFriendsList(friendsList);
            return;
        }

        // 검색어로 필터링
        const filteredFriends = friendsList.filter(friend =>
            friend.receiverNickName.toLowerCase().includes(searchText) ||
            (friend.receiverEmail && friend.receiverEmail.toLowerCase().includes(searchText))
        );

        renderFriendsList(filteredFriends);
    });
}

// 13. 보낸 친구 요청 목록 조회 & 렌더링 (상태별 아이콘/텍스트 분기)
async function getSentRequests() {
    try {
        const res = await fetch('/api/friend/sent', {
            method: 'GET',
            headers: authHeaders()
        });
        const result = await res.json();
        const list = document.getElementById('sent-requests-list');
        list.innerHTML = '';

        if (result.result === 'SUCCESS' &&
            Array.isArray(result.data?.friends) &&
            result.data.friends.length
        ) {
            result.data.friends.forEach(request => {
                // 상태별로 아이콘과 텍스트 결정
                let statusIcon, statusText;
                if (request.status === 'PENDING') {
                    statusIcon = 'fa-paper-plane';
                    statusText = '요청 중';
                } else if (request.status === 'REJECTED') {
                    statusIcon = 'fa-times-circle';
                    statusText = '거절됨';
                } else {
                    // 혹시 ACCEPTED가 내려오는 경우 대비 (대상에서 제외 처리할 수도 있음)
                    return;
                }

                const item = document.createElement('div');
                item.className = 'friend-item';
                item.innerHTML = `
                    <div class="friend-avatar">
                        ${request.receiverNickName?.charAt(0) || 'U'}
                    </div>
                    <div class="friend-info">
                        <div class="friend-name">
                            ${request.receiverNickName}
                        </div>
                        <div class="friend-email">
                            ${request.receiverEmail || ''}
                        </div>
                        <div class="friend-status">
                            <i class="fas ${statusIcon} status-icon"></i>
                            ${statusText}
                        </div>
                    </div>
                `;
                list.appendChild(item);
            });
        } else {
            list.innerHTML = '<div class="no-results">보낸 요청이 없습니다.</div>';
        }

        // 모달 띄우기
        document.getElementById('sent-requests-modal').style.display = 'flex';
    } catch (error) {
        console.error('보낸 친구 요청 목록 조회 중 오류:', error);
        document.getElementById('sent-requests-list').innerHTML =
            '<div class="no-results">보낸 요청을 불러오는 중 오류가 발생했습니다.</div>';
        document.getElementById('sent-requests-modal').style.display = 'flex';
    }
}


// 14. 보낸 요청 버튼 셋업
function setupSentRequestsButton() {
    const btn = document.getElementById('sent-requests-btn');
    if (btn) {
        btn.addEventListener('click', () => {
            getSentRequests();
        });
    }
}

// 15. 받은 친구 요청 목록 조회 & 렌더링
async function getFriendRequests() {
    try {
        const res = await fetch('/api/friend/pending', {
            method: 'GET',
            headers: authHeaders()
        });
        const result = await res.json();
        const panel = document.getElementById('friend-requests-panel');
        const list = document.getElementById('friend-requests-list');
        list.innerHTML = '';

        if (result.result === 'SUCCESS' && result.data?.friends && result.data.friends.length) {
            panel.style.display = 'block';
            result.data.friends.forEach(request => {
                const item = document.createElement('div');
                item.className = 'friend-item';
                item.innerHTML = `
                    <div class="friend-avatar">${request.requesterNickName?.charAt(0) || 'U'}</div>
                    <div class="friend-info">
                        <div class="friend-name">${request.requesterNickName}</div>
                        <div class="friend-email">${request.requesterEmail || ''}</div>
                        <div class="friend-status">
                            <i class="fas fa-clock status-icon"></i> 대기중
                        </div>
                    </div>
                    <div class="friend-actions">
                        <button class="action-btn accept-btn" data-request-id="${request.id}">
                            <i class="fas fa-check"></i>
                        </button>
                        <button class="action-btn reject-btn" data-request-id="${request.id}">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                `;
                list.appendChild(item);
            });
            setupAcceptButtons();
            setupRejectButtons();
        } else {
            panel.style.display = 'none';
        }
    } catch (error) {
        console.error('친구 요청 목록 조회 중 오류:', error);
        document.getElementById('friend-requests-panel').style.display = 'none';
    }
}

// 16. 친구 요청 수락 버튼 이벤트
function setupAcceptButtons() {
    document.querySelectorAll('.accept-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            try {
                const friendId = btn.getAttribute('data-request-id');
                const res = await fetch(`/api/friend/${friendId}`, {
                    method: 'PATCH',
                    headers: authHeaders(),
                    body: JSON.stringify({ status: 'ACCEPTED' })
                });
                const result = await res.json();
                alert(result.message || (result.result === 'SUCCESS' ? '친구 요청 수락 완료' : '요청 실패'));
                if (result.result === 'SUCCESS') {
                    getFriends();
                    getFriendRequests();
                }
            } catch (error) {
                console.error('친구 요청 수락 중 오류:', error);
                alert('친구 요청 수락 중 오류가 발생했습니다.');
            }
        });
    });
}

// 17. 친구 요청 거절 버튼 이벤트
function setupRejectButtons() {
    document.querySelectorAll('.reject-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            try {
                const friendId = btn.getAttribute('data-request-id');
                const res = await fetch(`/api/friend/${friendId}`, {
                    method: 'PATCH',
                    headers: authHeaders(),
                    body: JSON.stringify({ status: 'REJECTED' })
                });
                const result = await res.json();
                alert(result.message || (result.result === 'SUCCESS' ? '친구 요청 거절 완료' : '요청 실패'));
                if (result.result === 'SUCCESS') {
                    getFriendRequests();
                }
            } catch (error) {
                console.error('친구 요청 거절 중 오류:', error);
                alert('친구 요청 거절 중 오류가 발생했습니다.');
            }
        });
    });
}

// 18. 친구 삭제 버튼 이벤트
function setupDeleteButtons() {
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            if (!confirm('정말 친구를 삭제하시겠습니까?')) return;
            try {
                const id = btn.getAttribute('data-friend-id');
                const res = await fetch(`/api/friend/${id}`, {
                    method: 'DELETE',
                    headers: authHeaders()
                });
                const result = await res.json();
                alert(result.message || (result.result === 'SUCCESS' ? '삭제 완료' : '삭제 실패'));
                getFriends();
            } catch (error) {
                console.error('친구 삭제 중 오류:', error);
                alert('친구 삭제 중 오류가 발생했습니다.');
            }
        });
    });
}

// 19. 투두 리스트 보기 버튼 이벤트
function setupViewTodoButtons() {
    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            try {
                const userId = btn.getAttribute('data-friend-id');
                const friendName = btn.getAttribute('data-friend-name');

                // 친구 이름 표시
                document.getElementById('todo-friend-name').textContent = friendName + '님의 할 일';

                // 투두 리스트 가져오기
                const res = await fetch(`/api/todo/${userId}`, {
                    method: 'GET',
                    headers: authHeaders()
                });

                const result = await res.json();
                const todoContainer = document.getElementById('todo-list');
                todoContainer.innerHTML = '';

                if (result.result === 'SUCCESS' && result.data?.personalTodosDto && result.data.personalTodosDto.length) {
                    result.data.personalTodosDto.forEach(todo => {
                        const todoItem = document.createElement('div');
                        todoItem.className = 'todo-item';
                        todoItem.innerHTML = `
                            <div class="todo-content">
                                <h3 class="todo-title">${todo.title}</h3>
                                <div class="todo-date">
                                    <i class="fas fa-calendar-alt"></i> ${new Date(todo.targetDate).toLocaleDateString()}
                                </div>
                                <div class="todo-status ${todo.isCompleted ? 'completed' : 'pending'}">
                                    <i class="fas ${todo.isCompleted ? 'fa-check-circle' : 'fa-clock'}"></i>
                                    ${todo.isCompleted ? '완료' : '진행중'}
                                </div>
                                <button class="todo-detail-btn" data-friend-id="${userId}" data-todo-id="${todo.id}">
                                    자세히 보기
                                </button>
                            </div>
                        `;
                        todoContainer.appendChild(todoItem);
                    });
                    setupTodoDetailButtons();
                } else {
                    todoContainer.innerHTML = '<div class="no-todos">할 일이 없습니다.</div>';
                }

                // 모달 표시
                document.getElementById('todo-modal').style.display = 'flex';
            } catch (error) {
                console.error('친구 투두 목록 조회 중 오류:', error);
                alert('친구의 할 일 목록을 불러오는 중 오류가 발생했습니다.');
            }
        });
    });
}

// 20. 투두 상세 보기 버튼 이벤트
function setupTodoDetailButtons() {
    document.querySelectorAll('.todo-detail-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            try {
                const friendId = btn.getAttribute('data-friend-id');
                const todoId = btn.getAttribute('data-todo-id');

                const res = await fetch(`/api/todo/${friendId}/${todoId}`, {
                    method: 'GET',
                    headers: authHeaders()
                });

                const result = await res.json();
                const todoContainer = document.getElementById('todo-list');

                if (result.result === 'SUCCESS' && result.data) {
                    const todo = result.data;
                    todoContainer.innerHTML = `
                        <div class="todo-item todo-detail">
                            <h3 class="todo-title">${todo.title}</h3>
                            <p class="todo-description">${todo.content || '설명 없음'}</p>
                            <div class="todo-date">
                                <i class="fas fa-calendar-alt"></i> ${new Date(todo.targetDate).toLocaleDateString()}
                            </div>
                            <div class="todo-status ${todo.isCompleted ? 'completed' : 'pending'}">
                                <i class="fas ${todo.isCompleted ? 'fa-check-circle' : 'fa-clock'}"></i>
                                ${todo.isCompleted ? '완료' : '진행중'}
                            </div>
                            <button class="todo-back-btn" data-friend-id="${friendId}">
                                <i class="fas fa-arrow-left"></i> 목록으로 돌아가기
                            </button>
                        </div>
                    `;

                    // 뒤로가기 버튼 이벤트 설정
                    document.querySelector('.todo-back-btn').addEventListener('click', () => {
                        const userId = friendId;
                        const friendName = document.getElementById('todo-friend-name').textContent.replace('님의 할 일', '');
                        document.querySelector('.view-btn[data-friend-id="' + userId + '"]').click();
                    });
                } else {
                    todoContainer.innerHTML = `<div class="no-todos">할 일 상세 정보를 찾을 수 없습니다.</div>`;
                }
            } catch (error) {
                console.error('투두 상세 조회 중 오류:', error);
                alert('할 일 상세 정보를 불러오는 중 오류가 발생했습니다.');
            }
        });
    });
}