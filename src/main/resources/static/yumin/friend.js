const baseUrl = '/api/friend';

// JWT 헤더 생성 (로그인 여부 관계없이 헤더만 포함)
function authHeaders() {
    const raw = localStorage.getItem('accessToken') || '';
    const token = raw.replace(/^Bearer\s+/i, '');
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
    };
}

// 메시지 알림 함수
function showAlert(message) {
    alert(message);
}

// 친구 요청 보내기
async function sendFriendRequestById(receiverId) {
    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify({ receiverId })
    });

    const result = await response.json();
    showAlert(result.message);
    getSentRequests();
}

// 친구 목록 조회
async function getFriends() {
    try {
        const response = await fetch(baseUrl, {
            method: 'GET',
            headers: authHeaders()
        });

        const result = await response.json();

        // 예기치 않은 오류가 발생한 경우 처리
        if (result.result === 'FAIL') {
            showAlert(result.message || '친구 목록을 불러오는 데 실패했습니다.');
            return;
        }

        const list = document.getElementById('friendList');
        list.innerHTML = '';

        // 응답 데이터 확인
        if (!result || !result.data || !result.data.friends || result.data.friends.length === 0) {
            showAlert('친구 목록이 비어있거나 잘못된 응답이 왔습니다.');
            return;
        }

        result.data.friends.forEach(f => {
            const friendNick = f.receiverNickName;  // receiverNickName으로 수정
            const friendEmail = f.receiverEmail;   // receiverEmail으로 수정
            const friendStatus = f.status;         // status 가져오기

            const item = document.createElement('li');
            item.className = 'list-group-item d-flex justify-content-between align-items-center';
            item.innerHTML = `
                <div>
                    <strong>${friendNick}</strong> (${friendEmail}) [${friendStatus}]
                </div>
            `;

            const delBtn = document.createElement('button');
            delBtn.className = 'btn btn-sm btn-danger';
            delBtn.textContent = '삭제';
            delBtn.onclick = () => deleteFriend(f.id);

            item.appendChild(delBtn);
            list.appendChild(item);
        });
    } catch (error) {
        // 네트워크 오류 처리
        showAlert('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
}

// 받은 친구 요청 조회
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
        item.className = 'list-group-item d-flex justify-content-between align-items-center';

        const text = `<strong>${f.requesterNickName}</strong> (${f.requesterEmail}) → 나`;
        item.innerHTML = `<div>${text}</div>`;

        const btnGroup = document.createElement('div');

        const acceptBtn = document.createElement('button');
        acceptBtn.className = 'btn btn-success btn-sm me-2';
        acceptBtn.textContent = '수락';
        acceptBtn.onclick = () => updateStatus(f.id, 'ACCEPTED');

        const rejectBtn = document.createElement('button');
        rejectBtn.className = 'btn btn-secondary btn-sm';
        rejectBtn.textContent = '거절';
        rejectBtn.onclick = () => updateStatus(f.id, 'REJECTED');

        btnGroup.appendChild(acceptBtn);
        btnGroup.appendChild(rejectBtn);
        item.appendChild(btnGroup);

        list.appendChild(item);
    });
}

// 보낸 친구 요청 조회
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
        item.className = 'list-group-item';
        item.innerHTML = `나 → <strong>${f.receiverNickName}</strong> (${f.receiverEmail}) [${f.status}]`;
        list.appendChild(item);
    });
}

// 상태 변경 (수락/거절)
async function updateStatus(friendId, status) {
    const response = await fetch(`${baseUrl}/${friendId}`, {
        method: 'PATCH',
        headers: authHeaders(),
        body: JSON.stringify({ status })
    });

    const result = await response.json();
    showAlert(result.message);
    getReceivedRequests();
    getFriends();
}

// 친구 삭제
async function deleteFriend(friendId) {
    const response = await fetch(`${baseUrl}/${friendId}`, {
        method: 'DELETE',
        headers: authHeaders()
    });

    const result = await response.json();
    showAlert(result.message);
    getFriends();
}

// 유저 검색 + 요청 버튼 노출
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

    container.appendChild(info);
    container.appendChild(btn);
}

// 초기 로딩 시 자동 호출
getFriends();
getReceivedRequests();
getSentRequests();
