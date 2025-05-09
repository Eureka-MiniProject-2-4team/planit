const BASE_URL = "http://localhost:8080/api/todo/personal";

async function createOrUpdateTodo() {
    const id = document.getElementById("todoId").value;
    const userId = document.getElementById("userId").value;
    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;
    const targetDate = document.getElementById("targetDate").value;

    const todo = { userId, title, content, targetDate, isCompleted: false };
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
    const res = await fetch(`${BASE_URL}?userId=${userId}`);
    const json = await res.json();

    const list = document.getElementById("todoList");
    list.innerHTML = "";

    json.data.forEach(todo => {
        const li = document.createElement("li");
        li.className = "list-group-item d-flex justify-content-between align-items-center";
        li.innerHTML = `
            <div>
                <strong>${todo.title}</strong><br/>
                ${todo.content}<br/>
                <small>${new Date(todo.targetDate).toLocaleString()}</small>
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
    document.getElementById("targetDate").value = todo.targetDate;
}

async function deleteTodo(id) {
    const res = await fetch(`${BASE_URL}/${id}`, { method: "DELETE" });
    const json = await res.json();
    alert(json.message || "삭제 완료");
    getTodos();
}
