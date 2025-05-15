let allTodos = [];
let selectedDateKey = null;

function markDatesWithTodos() {
    if (!allTodos || allTodos.length === 0) return;

    // 날짜별로 할 일을 그룹화
    const todosByDate = {};
    allTodos.forEach(todo => {
        const todoDate = new Date(todo.targetDate);
        todoDate.setHours(0, 0, 0, 0);
        const dateKey = todoDate.toISOString().slice(0, 10);
        if (!todosByDate[dateKey]) todosByDate[dateKey] = [];
        todosByDate[dateKey].push(todo);
    });

    // 현재 달력의 모든 날짜 셀을 확인
    const cells = document.querySelectorAll('.date-cell');
    const currentMonth = new Date(selectedDateKey).getMonth();
    const currentYear = new Date(selectedDateKey).getFullYear();

    cells.forEach(cell => {
        const dayNum = parseInt(cell.querySelector('.date-num').textContent);
        if (!isNaN(dayNum) && !cell.classList.contains('other-month')) {
            const date = new Date(currentYear, currentMonth, dayNum);
            date.setHours(0, 0, 0, 0);
            const dateKey = date.toISOString().slice(0, 10);

            // 해당 날짜에 할 일이 있는지 확인
            if (todosByDate[dateKey] && todosByDate[dateKey].length > 0) {
                // 날짜 아래에 할 일 표시 추가
                const todoIndicator = document.createElement('div');
                todoIndicator.className = 'todo-indicator';
                todoIndicator.title = `${todosByDate[dateKey].length}개의 할 일`;
                cell.appendChild(todoIndicator);
            }
        }
    });
}

// Create stars background
function createStars() {
    const starsContainer = document.getElementById('stars');
    const numberOfStars = 150;

    for (let i = 0; i < numberOfStars; i++) {
        const star = document.createElement('div');
        star.classList.add('star');

        const size = Math.random() * 3 + 1;
        star.style.width = `${size}px`;
        star.style.height = `${size}px`;

        const posX = Math.random() * 100;
        const posY = Math.random() * 100;
        star.style.left = `${posX}%`;
        star.style.top = `${posY}%`;

        const delay = Math.random() * 5;
        star.style.animationDelay = `${delay}s`;

        starsContainer.appendChild(star);
    }
}

function generateCalendar() {
    const referenceDate = new Date(selectedDateKey);
    referenceDate.setHours(0, 0, 0, 0);

    const calendarGrid = document.getElementById('calendar-grid');
    calendarGrid.innerHTML = '';

    const currentMonth = referenceDate.getMonth();
    const currentYear = referenceDate.getFullYear();

    // 할 일 날짜 정리
    const taskDates = new Set();
    allTodos.forEach(todo => {
        const d = new Date(todo.targetDate);
        d.setHours(0, 0, 0, 0);
        taskDates.add(d.toISOString().slice(0, 10));
    });

    const firstDay = new Date(currentYear, currentMonth, 1);
    const startingDay = firstDay.getDay();

    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
    const prevMonthDays = startingDay;
    const totalCells = Math.ceil((daysInMonth + prevMonthDays) / 7) * 7;
    const nextMonthDays = totalCells - (daysInMonth + prevMonthDays);

    const prevMonth = new Date(currentYear, currentMonth, 0);
    const prevMonthTotalDays = prevMonth.getDate();

    // 이전 달
    for (let i = 0; i < prevMonthDays; i++) {
        const dayNum = prevMonthTotalDays - prevMonthDays + i + 1;
        const cellDate = new Date(currentYear, currentMonth - 1, dayNum);
        const dateKey = cellDate.toISOString().slice(0, 10);
        const classList = ['other-month'];
        if (taskDates.has(dateKey)) classList.push('has-tasks');
        const dateCell = createDateCell(dayNum, classList.join(' '), cellDate);
        calendarGrid.appendChild(dateCell);
    }

    // 현재 달
    for (let i = 1; i <= daysInMonth; i++) {
        const cellDate = new Date(currentYear, currentMonth, i);
        const dateKey = cellDate.toISOString().slice(0, 10);
        const classList = [];
        if (taskDates.has(dateKey)) classList.push('has-tasks');
        if (
            cellDate.getFullYear() === new Date().getFullYear() &&
            cellDate.getMonth() === new Date().getMonth() &&
            cellDate.getDate() === new Date().getDate()
        ) {
            classList.push('today');
        }
        const dateCell = createDateCell(i, classList.join(' '), cellDate);
        calendarGrid.appendChild(dateCell);
    }

    // 다음 달
    for (let i = 1; i <= nextMonthDays; i++) {
        const cellDate = new Date(currentYear, currentMonth + 1, i);
        const dateKey = cellDate.toISOString().slice(0, 10);
        const classList = ['other-month'];
        if (taskDates.has(dateKey)) classList.push('has-tasks');
        const dateCell = createDateCell(i, classList.join(' '), cellDate);
        calendarGrid.appendChild(dateCell);
    }

    updateMonthDisplay();
}


function createDateCell(dayNum, classNames = '', referenceDate = new Date()) {
    const dateCell = document.createElement('div');
    dateCell.className = `date-cell ${classNames}`;

    const dateNumSpan = document.createElement('span');
    dateNumSpan.className = 'date-num';
    dateNumSpan.textContent = dayNum;

    dateCell.appendChild(dateNumSpan);

    dateCell.addEventListener('click', function () {
        document.querySelectorAll('.date-cell').forEach(cell => cell.classList.remove('selected'));
        dateCell.classList.add('selected');

        const selected = new Date(referenceDate.getFullYear(), referenceDate.getMonth(), dayNum);
        selected.setHours(0, 0, 0, 0);
        selectedDateKey = selected.toISOString().slice(0, 10);
        updateSelectedDate(dayNum, referenceDate);
        renderTodos(allTodos);
    });

    return dateCell;
}


function updateSelectedDate(day, referenceDate = new Date()) {
    const dateDisplay = document.querySelector('.date-display');
    const date = new Date(referenceDate.getFullYear(), referenceDate.getMonth(), day);
    const options = {year: 'numeric', month: 'long', day: 'numeric'};
    dateDisplay.innerHTML = `<i class="fas fa-star-half-alt"></i> ${date.toLocaleDateString('ko-KR', options)}`;
}

function fetchMyTodos() {
    const token = localStorage.getItem('accessToken');
    if (!token) return console.error('토큰 없음');

    fetch('/api/todo/me', {
        method: 'GET',
        headers: {
            'Authorization': token,
            'Content-Type': 'application/json'
        }
    })
        .then(res => res.json())
        .then(response => {
            console.log('내 투두:', response);
            if (Array.isArray(response.data?.personalTodosDto)) {
                allTodos = response.data.personalTodosDto;
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                generateCalendar();          // ✅ 여기서 캘린더 다시 그림 (할 일 기준으로 점 찍힘)
                renderTodos(allTodos);       // 할 일 렌더링
            } else {
                console.error('투두 데이터가 배열이 아님:', response.data);
            }
        })
        .catch(err => console.error('투두 조회 실패', err));
}

function renderTodos(todos) {
    if (!selectedDateKey) return;
    const taskList = document.querySelector('.task-list');
    taskList.innerHTML = '';

    const filtered = todos.filter(todo => {
        const todoDate = new Date(todo.targetDate);
        todoDate.setHours(0, 0, 0, 0);
        return todoDate.toISOString().slice(0, 10) === selectedDateKey;
    });

    filtered.forEach(todo => {
        const taskItem = document.createElement('div');
        taskItem.className = 'task-item';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'task-checkbox';
        checkbox.checked = todo.isCompleted;
        checkbox.addEventListener('change', function (e) {
            e.stopPropagation(); // ✅ 클릭 이벤트 버블링 방지 (상세보기 안 뜨게)

            const newStatus = this.checked;

            taskItem.style.opacity = newStatus ? '0.6' : '1';

            const token = localStorage.getItem('accessToken');
            fetch(`/api/todo/me/${todo.id}`, {
                method: 'PATCH',
                headers: {
                    'Authorization': token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: todo.title,
                    content: todo.content,
                    targetDate: todo.targetDate,
                    isCompleted: newStatus
                })
            })

                .then(res => res.json())
                .then(data => {
                    console.log('상태 업데이트 성공:', data);
                    fetchMyTodos(); // UI 반영
                })
                .catch(err => console.error('상태 업데이트 실패:', err));
        });

        const content = document.createElement('div');
        content.className = 'task-content';

        const title = document.createElement('div');
        title.className = 'task-title';
        title.textContent = todo.title;

        const time = document.createElement('div');
        time.className = 'task-time';
        time.innerHTML = `<i class="fas fa-clock time-icon"></i> ${formatDateTime(todo.targetDate)}`;

        content.appendChild(title);
        content.appendChild(time);

        taskItem.appendChild(checkbox);
        taskItem.appendChild(content);

        content.addEventListener('click', function () {
            document.getElementById('detail-title').value = todo.title;
            document.getElementById('detail-content').value = todo.content || '';
            document.getElementById('detail-time').value = formatTimeInput(todo.targetDate);
            document.getElementById('detail-date').value = formatDateInput(todo.targetDate);
            document.getElementById('detail-status').value = todo.isCompleted;
            document.getElementById('save-detail').setAttribute('data-id', todo.id);
            document.getElementById('todo-detail-modal').style.display = 'flex';
        });

        taskList.appendChild(taskItem);
    });
}


function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
}

function createTodo() {
    const token = localStorage.getItem('accessToken');
    if (!token) return alert('로그인이 필요합니다');

    const modal = document.getElementById('todo-modal');
    modal.style.display = 'flex';

    document.getElementById('save-todo').onclick = function () {
        const title = document.getElementById('todo-title').value;
        const content = document.getElementById('todo-content').value;
        const date = document.getElementById('todo-date').value;
        const time = document.getElementById('todo-time').value;

        if (!title || !date || !time) {
            alert('제목, 날짜, 시간 모두 입력해주세요');
            return;
        }

        const [year, month, day] = date.split('-').map(Number);
        const [hour, minute] = time.split(':').map(Number);

        const targetDate = new Date(year, month - 1, day, hour, minute);

        // KST 기준 문자열 생성
        function toKSTISOString(date) {
            const yyyy = date.getFullYear();
            const MM = String(date.getMonth() + 1).padStart(2, '0');
            const dd = String(date.getDate()).padStart(2, '0');
            const hh = String(date.getHours()).padStart(2, '0');
            const mi = String(date.getMinutes()).padStart(2, '0');
            return `${yyyy}-${MM}-${dd}T${hh}:${mi}:00`;
        }

        const newTodo = {
            title,
            content,
            targetDate: toKSTISOString(targetDate),
            isCompleted: false
        };

        fetch('/api/todo/me', {
            method: 'POST',
            headers: {
                'Authorization': token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newTodo)
        })
            .then(res => res.json())
            .then(data => {
                console.log('할 일 생성 완료:', data);
                modal.style.display = 'none';
                fetchMyTodos();
            })
            .catch(err => console.error('할 일 생성 실패:', err));
    };

    document.getElementById('cancel-todo').onclick = function () {
        modal.style.display = 'none';
    };
}


window.onload = function () {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/html/auth/login.html';
        return;
    }
    if (selectedDateKey == null) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        selectedDateKey = today.toISOString().slice(0, 10);

    }

    createStars();
    fetchMyTodos();

    document.getElementById('close-detail').addEventListener('click', function () {
        document.getElementById('todo-detail-modal').style.display = 'none';
    });


    document.getElementById('prev-month').addEventListener('click', function () {
        console.log(selectedDateKey);
        const date = new Date(selectedDateKey);
        console.log(date);
        const prevMonth = new Date(date.getFullYear(), date.getMonth() - 1, 2);
        console.log(prevMonth);
        selectedDateKey = prevMonth.toISOString().slice(0, 10);
        console.log('selected day', selectedDateKey);

        generateCalendar();
        fetchMyTodos();
    });

    document.getElementById('next-month').addEventListener('click', function () {
        const date = new Date(selectedDateKey);
        console.log(date);
        const nextMonth = new Date(date.getFullYear(), date.getMonth() + 1, 2);
        console.log(nextMonth);
        selectedDateKey = nextMonth.toISOString().slice(0, 10);
        console.log(selectedDateKey);

        generateCalendar();
        fetchMyTodos();
    });
    document.querySelector('.add-task-btn').addEventListener('click', function () {
        createTodo();
    });

    document.getElementById('save-detail').addEventListener('click', function () {
        const id = this.getAttribute('data-id');
        const title = document.getElementById('detail-title').value;
        const content = document.getElementById('detail-content').value;
        const time = document.getElementById('detail-time').value;
        const dateInput = document.getElementById('detail-date').value;
        const isCompleted = document.getElementById('detail-status').value === 'true';

        if (!title || !time || !dateInput) {
            alert('제목, 날짜, 시간을 모두 입력해주세요.');
            return;
        }

        const [year, month, day] = dateInput.split('-').map(Number);
        const [hour, minute] = time.split(':').map(Number);
        const targetDate = new Date(year, month - 1, day, hour, minute);
        const kstISOString = toKSTISOString(targetDate);
        const token = localStorage.getItem('accessToken');
        fetch(`/api/todo/me/${id}`, {
            method: 'PATCH',
            headers: {
                'Authorization': token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                title,
                content,
                targetDate: kstISOString,
                isCompleted
            })
        })
            .then(res => res.json())
            .then(data => {
                alert('수정이 완료되었습니다!');
                document.getElementById('todo-detail-modal').style.display = 'none';
                fetchMyTodos(); // 목록 다시 불러오기
            })
            .catch(err => {
                console.error('수정 실패:', err);
                alert('수정 실패!');
            });
    });

    document.getElementById('delete-detail').addEventListener('click', function () {
        const id = document.getElementById('save-detail').getAttribute('data-id');
        const token = localStorage.getItem('accessToken');

        if (!confirm('정말 이 할 일을 삭제하시겠습니까?')) return;

        fetch(`/api/todo/me/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': token,
                'Content-Type': 'application/json'
            }
        })
            .then(res => {
                if (res.ok) {
                    alert('삭제되었습니다!');
                    document.getElementById('todo-detail-modal').style.display = 'none';
                    fetchMyTodos(); // 목록 새로고침
                } else {
                    return res.json().then(data => { throw new Error(data.message || '삭제 실패'); });
                }
            })
            .catch(err => {
                console.error('삭제 오류:', err);
                alert('삭제 중 오류가 발생했습니다.');
            });
    });

};

function updateMonthDisplay() {
    const today = new Date(selectedDateKey);
    const monthDisplay = document.querySelector('.month-display');
    if (monthDisplay) {
        monthDisplay.textContent = `${today.getFullYear()}년 ${today.getMonth() + 1}월`;
    }
}

function formatTimeInput(dateString) {
    const d = new Date(dateString);
    const hh = String(d.getHours()).padStart(2, '0');
    const mm = String(d.getMinutes()).padStart(2, '0');
    return `${hh}:${mm}`;
}

function formatDateInput(dateString) {
    const d = new Date(dateString);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function toKSTISOString(date) {
    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mm = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');
    return `${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}`;
}