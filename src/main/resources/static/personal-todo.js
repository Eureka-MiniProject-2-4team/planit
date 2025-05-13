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
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, content, targetDate })
        });
        document.getElementById('todo-form').reset();
        fetchMyTodos();
    });
});

async function fetchMyTodos() {
    const res = await fetch(myApiBase);
    const data = await res.json();
    renderTodos('todo-list', data.data.personalTodosDto, true);
}

async function deleteTodo(id) {
    await fetch(`${myApiBase}/${id}`, { method: 'DELETE' });
    fetchMyTodos();
}

async function toggleComplete(id, isCompleted) {
    await fetch(`${myApiBase}/${id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isCompleted })
    });
    fetchMyTodos();
}

async function getFriendTodos() {
    const friendId = document.getElementById('friendId').value;
    if (!friendId) return alert('친구 ID를 입력하세요');

    const res = await fetch(`/api/todo/${friendId}`);
    const data = await res.json();
    renderTodos('friend-todo-list', data.data.personalTodosDto, false);
}

function renderTodos(listId, todos, isMine) {
    const list = document.getElementById(listId);
    list.innerHTML = '';
    todos.forEach(todo => {
        const li = document.createElement('li');
        li.innerHTML = `
            ${isMine ? `
                <input type="checkbox" ${todo.completed ? 'checked' : ''} onclick="toggleComplete('${todo.id}', ${!todo.completed})">
            ` : ''}
            <strong>${todo.title}</strong> (${todo.targetDate})<br>
            ${todo.content}
            ${isMine ? `<button onclick="deleteTodo('${todo.id}')">삭제</button>` : ''}
        `;
        list.appendChild(li);
    });
}
