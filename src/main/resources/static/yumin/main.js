const BASE_URL = "http://localhost:8080/api/todo/personal";

async function createOrUpdateTodo() {
    const id = document.getElementById("todoId").value;
    const userId = document.getElementById("userId").value;
    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;
    const targetDateInput = document.getElementById("targetDate").value;

    if (!userId || !title || !targetDateInput) {
        alert("사용자 ID, 제목, 날짜는 필수입니다.");
        return;
    }

    const targetDate = targetDateInput + ":00";
    const isCompleted = false;

    const todo = { userId, title, content, targetDate, isCompleted };

    const method = id ? "PATCH" : "POST";
    const url = id ? `${BASE_URL}/${id}` : BASE_URL;

    const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(todo)
    });

    const json = await res.json();
    alert(json.message || "처리 완료");
    resetForm();
    getTodos();
}

function resetForm() {
    document.getElementById("todoId").value = "";
    document.getElementById("userId").value = "";
    document.getElementById("title").value = "";
    document.getElementById("content").value = "";
    document.getElementById("targetDate").value = "";
}

async function getTodos() {
    const userId = document.getElementById("searchUserId").value;
    if (!userId) {
        alert("조회할 사용자 ID를 입력하세요.");
        return;
    }

    const res = await fetch(`${BASE_URL}?userId=${userId}`);
    const json = await res.json();
    const todos = json.data.personalTodosDto;

    const list = document.getElementById("todoList");
    list.innerHTML = "";

    todos.forEach(todo => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";
        li.innerHTML = `
            <div>
                <strong>${todo.title}</strong><br/>
                ${todo.content || ""}<br/>
                <small>📅 ${new Date(todo.targetDate).toLocaleString()}</small><br/>
                <span class="badge ${todo.isCompleted ? 'bg-success' : 'bg-warning text-dark'}">
                    ${todo.isCompleted ? '완료됨' : '진행 중'}
                </span>
            </div>
            <div>
                <button class="btn btn-sm btn-outline-primary me-2" onclick='editTodo(${JSON.stringify(todo)})'>수정</button>
                <button class="btn btn-sm btn-outline-danger" onclick='deleteTodo("${todo.id}")'>삭제</button>
            </div>
        `;
        list.appendChild(li);
    });
}

function editTodo(todo) {
    document.getElementById("todoId").value = todo.id;
    document.getElementById("userId").value = todo.userId;
    document.getElementById("title").value = todo.title;
    document.getElementById("content").value = todo.content;
    document.getElementById("targetDate").value = todo.targetDate.slice(0, 16);
}

async function deleteTodo(id) {
    const res = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
    const json = await res.json();
    alert(json.message || "삭제 완료");
    getTodos();
}
