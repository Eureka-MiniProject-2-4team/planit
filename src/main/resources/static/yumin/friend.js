const baseUrl = '/api/friend';

function authHeaders() {
    const raw = localStorage.getItem('accessToken') || '';
    const token = raw.replace(/^Bearer\s+/i, '');
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    };
}

// ✅ 친구 요청 (검색 기반)
async function sendFriendRequestById(receiverId) {
    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ receiverId })  // requesterId는 로그인 사용자 기준
    });

    const result = await response.json();
    alert(result.message);
}

// ✅ 내 친구 목록
async function getFriends() {
    const response = await fetch(baseUrl, {
        method: 'GET',
        headers: authHeaders()
    });

    const result = await response.json();
    const list = document.getElementById('friendList');
    list.innerHTML = '';

    result.data.friends.forEach(f => {
        const item = document.createElement('li');
        item.textContent = `${f.requesterId} ↔ ${f.receiverId} (${f.status})`;
        const delBtn = document.createElement('button');
        delBtn.textContent = '삭제';
        delBtn.onclick = () => deleteFriend(f.id);
        item.appendChild(delBtn);
        list.appendChild(item);
    });
}

// ✅ 받은 친구 요청
async function getReceivedRequests() {
    const response = await fetch(`${baseUrl}/pending`, {
        method: 'GET',
        headers: authHeaders()
    });

    const result = await response.json();
    const list = document.getElementById('pendingList');
    list.innerHTML = '';

    result.data.friends.forEach(f => {
        const item = document.createElement('li');
        item.textContent = `${f.requesterId} → 나`;

        const acceptBtn = document.createElement('button');
        acceptBtn.textContent = '수락';
        acceptBtn.onclick = () => updateStatus(f.id, 'ACCEPTED');

        const rejectBtn = document.createElement('button');
        rejectBtn.textContent = '거절';
        rejectBtn.onclick = () => updateStatus(f.id, 'REJECTED');

        item.appendChild(acceptBtn);
        item.appendChild(rejectBtn);
        list.appendChild(item);
    });
}

// ✅ 보낸 친구 요청
async function getSentRequests() {
    const response = await fetch(`${baseUrl}/sent`, {
        method: 'GET',
        headers: authHeaders()
    });

    const result = await response.json();
    const list = document.getElementById('sentList');
    list.innerHTML = '';

    result.data.friends.forEach(f => {
        const item = document.createElement('li');
        item.textContent = `나 → ${f.receiverId} (${f.status})`;
        list.appendChild(item);
    });
}

// ✅ 상태 변경
async function updateStatus(friendId, status) {
    const response = await fetch(`${baseUrl}/${friendId}`, {
        method: 'PATCH',
        headers: authHeaders(),
        body: JSON.stringify({ status })
    });

    const result = await response.json();
    alert(result.message);
    getReceivedRequests();
}

// ✅ 삭제
async function deleteFriend(friendId) {
    const response = await fetch(`${baseUrl}/${friendId}`, {
        method: 'DELETE',
        headers: authHeaders()
    });

    const result = await response.json();
    alert(result.message);
    getFriends();
}

// ✅ 유저 검색 + 요청 버튼 노출
async function searchUser() {
    const value = document.getElementById('searchValue').value;

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
        <p>닉네임: ${nickName} (${email})</p>
        <p>친구 상태: ${friendStatus ?? '없음'}</p>
    `;

    const btn = document.createElement('button');

    if (isMe) {
        btn.textContent = '본인입니다.';
        btn.disabled = true;
    } else if (friendStatus === 'PENDING' || friendStatus === 'ACCEPTED') {
        btn.textContent = '이미 친구 요청이 존재합니다.';
        btn.disabled = true;
    } else if (friendStatus === 'REJECTED' || friendStatus === 'AUTO_CANCELLED') {
        btn.textContent = '친구 요청 불가';
        btn.disabled = true;
    } else {
        btn.textContent = '친구 요청 보내기';
        btn.onclick = () => sendFriendRequestById(id);
    }

    container.appendChild(info);
    container.appendChild(btn);
}
