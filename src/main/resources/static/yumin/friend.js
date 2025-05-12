const baseUrl = '/api/friend';

async function sendFriendRequest() {
    const requesterId = document.getElementById('requesterId').value;
    const receiverId = document.getElementById('receiverId').value;

    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ requesterId, receiverId })
    });

    const result = await response.json();
    alert(result.message);
}

async function getFriends() {
    const userId = document.getElementById('userIdForFriends').value;

    const response = await fetch(`${baseUrl}?userId=${userId}`);
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

async function getReceivedRequests() {
    const userId = document.getElementById('userIdForPending').value;

    const response = await fetch(`${baseUrl}/pending?userId=${userId}`);
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

async function getSentRequests() {
    const userId = document.getElementById('userIdForSent').value;

    const response = await fetch(`${baseUrl}/sent?userId=${userId}`);
    const result = await response.json();

    const list = document.getElementById('sentList');
    list.innerHTML = '';
    result.data.friends.forEach(f => {
        const item = document.createElement('li');
        item.textContent = `나 → ${f.receiverId} (${f.status})`;
        list.appendChild(item);
    });
}

async function updateStatus(friendId, status) {
    const response = await fetch(`${baseUrl}/${friendId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
    });

    const result = await response.json();
    alert(result.message);
    getReceivedRequests(); // 자동 새로고침
}

async function deleteFriend(friendId) {
    const response = await fetch(`${baseUrl}/${friendId}`, { method: 'DELETE' });
    const result = await response.json();
    alert(result.message);
    getFriends(); // 자동 새로고침
}
