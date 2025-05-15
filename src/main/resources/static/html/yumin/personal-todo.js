const myApiBase = '/api/todo/me';

document.addEventListener('DOMContentLoaded', () => {
    fetchMyTodos();

    document.getElementById('todo-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;
        const targetDate = document.getElementById('targetDate').value;

        await fetch(myApiBase, {
            method: 'POST',
            headers: buildHeaders(),
            body: JSON.stringify({ title, content, targetDate })
        });

        document.getElementById('todo-form').reset();
        fetchMyTodos();
    });

    document.getElementById('friend-btn').addEventListener('click', getFriendTodos);
});

async function fetchMyTodos() {
    const res = await fetch(myApiBase, { headers: buildHeaders() });
    const data = await res.json();
    renderTodos('todo-list', data.data.personalTodosDto || [], true);
}

async function deleteTodo(id) {
    await fetch(`${myApiBase}/${id}`, {
        method: 'DELETE',
        headers: buildHeaders()
    });
    fetchMyTodos();
}

async function toggleComplete(id, isCompleted) {
    await fetch(`${myApiBase}/${id}`, {
        method: 'PATCH',
        headers: buildHeaders(),
        body: JSON.stringify({ isCompleted })
    });
    fetchMyTodos();
}

async function getFriendTodos() {
    const friendId = document.getElementById('friendId').value.trim();
    if (!friendId) return alert('친구 ID를 입력하세요');

    const res = await fetch(`/api/todo/${friendId}`, {
        headers: buildHeaders()
    });
    const data = await res.json();
    renderTodos('friend-todo-list', data.data.personalTodosDto || [], false);
}

function renderTodos(listId, todos, isMine) {
    const list = document.getElementById(listId);
    list.innerHTML = '';
    todos.forEach(todo => {
        const li = document.createElement('li');
        li.innerHTML = `
            ${isMine ? `<input type="checkbox" ${todo.completed ? 'checked' : ''} onclick="toggleComplete('${todo.id}', ${!todo.completed})">` : ''}
            <strong>${todo.title}</strong> (${todo.targetDate})<br>
            ${todo.content}
            ${isMine ? `<button onclick="deleteTodo('${todo.id}')">삭제</button>` : ''}
        `;
        list.appendChild(li);
    });
}

function buildHeaders() {
    const token = localStorage.getItem('accessToken');
    return {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': token })
    };
}
