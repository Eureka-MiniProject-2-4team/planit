// friend.js

// 1. JWT 헤더 생성
function authHeaders() {
    const raw = localStorage.getItem('accessToken') || '';
    const token = raw.replace(/^Bearer\s+/i, '');
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    };
}

// 2. 모달 인스턴스 선언 (초기화는 DOMContentLoaded 이후)
let todoModal;

// 3. DOMContentLoaded 이벤트에서 모달 초기화 및 초기 함수 호출
window.addEventListener('DOMContentLoaded', () => {
    const todoModalEl = document.getElementById('todoModal');
    if (todoModalEl) {
        todoModal = new bootstrap.Modal(todoModalEl);
        // 모달 숨김 시 백드롭 제거
        todoModalEl.addEventListener('hidden.bs.modal', () => {
            document.body.classList.remove('modal-open');
            document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
        });
    }
    // 초기 데이터 로드
    getFriends();
    getReceivedRequests();
    getSentRequests();
});

// 4. 친구 목록 조회 (투두 보기 & 삭제 버튼 포함)
async function getFriends() {
    const response = await fetch('/api/friend', {
        method: 'GET',
        headers: authHeaders()
    });
    const result = await response.json();
    const list = document.getElementById('friendList');
    list.innerHTML = '';

    if (result.result === 'SUCCESS' && result.data?.friends) {
        result.data.friends.forEach(f => {
            const item = document.createElement('li');
            item.className = 'list-group-item d-flex justify-content-between align-items-center';

            const infoDiv = document.createElement('div');
            infoDiv.innerHTML = `<strong>${f.receiverNickName}</strong> (${f.receiverEmail}) [${f.status}]`;

            const btnGroup = document.createElement('div');

            const todoBtn = document.createElement('button');
            todoBtn.className = 'btn btn-info btn-sm me-2';
            todoBtn.textContent = '투두 보기';
            todoBtn.onclick = () => getFriendTodos(f.receiverId);

            const delBtn = document.createElement('button');
            delBtn.className = 'btn btn-danger btn-sm';
            delBtn.textContent = '친구 삭제';
            delBtn.onclick = () => deleteFriend(f.id);

            btnGroup.append(todoBtn, delBtn);
            item.append(infoDiv, btnGroup);
            list.appendChild(item);
        });
    } else {
        list.innerHTML = '<li class="list-group-item">친구가 없습니다.</li>';
    }
}

// 5. 친구 투두 전체 조회 (모달 띄우기)
async function getFriendTodos(friendId) {
    const response = await fetch(`/api/todo/${friendId}`, {
        method: 'GET',
        headers: authHeaders()
    });
    const result = await response.json();
    const container = document.getElementById('todoListContainer');
    container.innerHTML = '';

    if (result.result === 'SUCCESS' && result.data?.personalTodosDto?.length) {
        result.data.personalTodosDto.forEach(todo => {
            const todoItem = document.createElement('div');
            todoItem.className = 'list-group-item';
            todoItem.innerHTML = `
                <strong>${todo.title}</strong> - ${todo.content}
                <br>목표일: ${todo.targetDate}
                <br>완료 여부: ${todo.isCompleted ? '완료' : '미완료'}
                <br>
                <button class="btn btn-link btn-sm p-0 mt-1" onclick="getFriendTodoById('${friendId}', '${todo.id}')">
                    자세히 보기
                </button>
            `;
            container.appendChild(todoItem);
        });
    } else {
        container.innerHTML = '<p>이 친구의 투두가 없습니다.</p>';
    }

    todoModal?.show();
}

// 6. 친구 개별 투두 조회
async function getFriendTodoById(friendId, todoId) {
    const response = await fetch(`/api/todo/${friendId}/${todoId}`, {
        method: 'GET',
        headers: authHeaders()
    });
    const result = await response.json();
    const container = document.getElementById('todoListContainer');
    container.innerHTML = '';

    if (result.result === 'SUCCESS' && result.data) {
        const todo = result.data;
        container.innerHTML = `
            <h5>${todo.title}</h5>
            <p>${todo.content}</p>
            <p>목표일: ${todo.targetDate}</p>
            <p>완료 여부: ${todo.isCompleted ? '완료' : '미완료'}</p>
        `;
    } else {
        container.innerHTML = `<p>${result.message || '해당 투두를 찾을 수 없습니다.'}</p>`;
    }

    todoModal?.show();
}

// 7. 친구 검색
async function searchUser() {
    const value = document.getElementById('searchValue').value.trim();
    const response = await fetch(`/api/users/${value}`, {
        method: 'GET',
        headers: authHeaders()
    });
    const result = await response.json();
    const container = document.getElementById('searchResult');
    container.innerHTML = '';

    if (!result.data) {
        container.textContent = '사용자를 찾을 수 없습니다.';
        return;
    }

    const { id, nickName, email, isMe, friendStatus } = result.data;
    const info = document.createElement('div');
    info.innerHTML = `
        <p>닉네임: <strong>${nickName}</strong></p>
        <p>이메일: ${email}</p>
        <p>친구 상태: ${friendStatus ?? '없음'}</p>
    `;

    const btn = document.createElement('button');
    btn.className = 'btn btn-primary';

    if (isMe) {
        btn.textContent = '본인입니다.';
        btn.disabled = true;
    } else if (['PENDING', 'ACCEPTED'].includes(friendStatus)) {
        btn.textContent = '요청 불가 (이미 존재)';
        btn.disabled = true;
    } else if (['REJECTED', 'AUTO_CANCELLED'].includes(friendStatus)) {
        btn.textContent = '친구 요청 불가';
        btn.disabled = true;
    } else {
        btn.textContent = '친구 요청 보내기';
        btn.onclick = () => sendFriendRequestById(id);
    }

    container.append(info, btn);
}

// 8. 친구 요청 보내기
async function sendFriendRequestById(receiverId) {
    const response = await fetch('/api/friend', {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ receiverId })
    });
    const result = await response.json();
    alert(result.message);
    getSentRequests();
}

// 9. 보낸 친구 요청 목록 조회
async function getSentRequests() {
    const response = await fetch('/api/friend/sent', {
        method: 'GET',
        headers: authHeaders()
    });
    const result = await response.json();
    const list = document.getElementById('sentList');
    list.innerHTML = '';

    if (result.result === 'SUCCESS' && result.data?.friends) {
        result.data.friends.forEach(f => {
            const item = document.createElement('li');
            item.className = 'list-group-item';
            item.innerHTML = `나 → <strong>${f.receiverNickName}</strong> (${f.receiverEmail}) [${f.status}]`;
            list.appendChild(item);
        });
    } else {
        list.innerHTML = '<li class="list-group-item">보낸 요청이 없습니다.</li>';
    }
}

// 10. 받은 친구 요청 목록 조회
async function getReceivedRequests() {
    const response = await fetch('/api/friend/pending', {
        method: 'GET',
        headers: authHeaders()
    });
    const result = await response.json();
    const list = document.getElementById('pendingList');
    list.innerHTML = '';

    if (result.result === 'SUCCESS' && result.data?.friends) {
        result.data.friends.forEach(f => {
            const item = document.createElement('li');
            item.className = 'list-group-item d-flex justify-content-between align-items-center';
            const textDiv = document.createElement('div');
            textDiv.innerHTML = `<strong>${f.requesterNickName}</strong> (${f.requesterEmail}) → 나`;
            const btnGroup = document.createElement('div');
            const acceptBtn = document.createElement('button');
            acceptBtn.className = 'btn btn-success btn-sm me-2';
            acceptBtn.textContent = '수락';
            acceptBtn.onclick = () => updateStatus(f.id, 'ACCEPTED');
            const rejectBtn = document.createElement('button');
            rejectBtn.className = 'btn btn-secondary btn-sm';
            rejectBtn.textContent = '거절';
            rejectBtn.onclick = () => updateStatus(f.id, 'REJECTED');
            btnGroup.append(acceptBtn, rejectBtn);
            item.append(textDiv, btnGroup);
            list.appendChild(item);
        });
    } else {
        list.innerHTML = '<li class="list-group-item">받은 요청이 없습니다.</li>';
    }
}

// 11. 친구 상태 변경
async function updateStatus(friendId, status) {
    const response = await fetch(`/api/friend/${friendId}`, {
        method: 'PATCH',
        headers: authHeaders(),
        body: JSON.stringify({ status })
    });
    const result = await response.json();
    alert(result.message);
    getReceivedRequests();
    getFriends();
}

// 12. 친구 삭제
async function deleteFriend(friendId) {
    const response = await fetch(`/api/friend/${friendId}`, {method: 'DELETE', headers: authHeaders()});
    const result = await response.json();
    alert(result.message);
    getFriends();
}
