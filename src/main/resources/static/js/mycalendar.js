let allTodos = [];
let selectedDateKey = null;

// 커스텀 시간 선택기 기능
function initializeCustomTimePickers() {
    // 타임피커 요소 찾기
    const todoTimePicker = document.querySelector('#todo-modal .custom-time-picker');
    const detailTimePicker = document.querySelector('#todo-detail-modal .custom-time-picker');

    // 각각의 타임피커에 이벤트 리스너와 옵션 추가
    initializeTimePicker(todoTimePicker, 'todo');
    initializeTimePicker(detailTimePicker, 'detail');
}

function initializeTimePicker(pickerElement, prefix) {
    if (!pickerElement) return;

    const inputDisplay = document.getElementById(`${prefix}-time-display`);
    const inputHidden = document.getElementById(`${prefix}-time`);
    const dropdown = pickerElement.querySelector('.time-dropdown');
    const periodColumn = pickerElement.querySelector('.period-column');
    const hoursColumn = pickerElement.querySelector('.hours-column');
    const minutesColumn = pickerElement.querySelector('.minutes-column');

    // 입력 필드 클릭 시 드롭다운 표시
    inputDisplay.addEventListener('click', function() {
        // 다른 열려있는 드롭다운 닫기
        document.querySelectorAll('.time-dropdown.open').forEach(el => {
            if (el !== dropdown) el.classList.remove('open');
        });

        dropdown.classList.toggle('open');

        // 드롭다운이 열렸으면 옵션 생성
        if (dropdown.classList.contains('open')) {
            generateTimeOptions(periodColumn, hoursColumn, minutesColumn, inputDisplay, inputHidden);

            // 현재 선택된 시간이 있으면 스크롤 위치 조정
            scrollToSelected(periodColumn, hoursColumn, minutesColumn);
        }
    });

    // 외부 클릭 시 드롭다운 닫기
    document.addEventListener('click', function(e) {
        if (!pickerElement.contains(e.target)) {
            dropdown.classList.remove('open');
        }
    });
}

function generateTimeOptions(periodColumn, hoursColumn, minutesColumn, inputDisplay, inputHidden) {
    // 기존 옵션 제거
    periodColumn.innerHTML = '';
    hoursColumn.innerHTML = '';
    minutesColumn.innerHTML = '';

    // 현재 선택된 시간 가져오기
    let selectedPeriod = '오전'; // 기본값
    let selectedHour = 9;       // 기본값 (9시)
    let selectedMinute = 0;     // 기본값 (0분)

    const currentTime = inputDisplay.value;
    if (currentTime && currentTime.includes(':')) {
        const timePattern = /^(오전|오후) (\d{1,2}):(\d{2})$/;
        const matches = currentTime.match(timePattern);

        if (matches) {
            selectedPeriod = matches[1];
            selectedHour = parseInt(matches[2]);
            selectedMinute = parseInt(matches[3]);
        }
    }

    // 오전/오후 옵션 생성
    const periods = ['오전', '오후'];
    periods.forEach(period => {
        const periodOption = document.createElement('div');
        periodOption.className = 'time-option' + (period === selectedPeriod ? ' selected' : '');
        periodOption.textContent = period;
        periodOption.dataset.value = period;

        periodOption.addEventListener('click', function() {
            // 이전 선택 해제
            periodColumn.querySelectorAll('.time-option').forEach(opt => opt.classList.remove('selected'));
            // 새 선택 적용
            this.classList.add('selected');
            // 시간 업데이트
            updateSelectedTime(periodColumn, hoursColumn, minutesColumn, inputDisplay, inputHidden);
        });

        periodColumn.appendChild(periodOption);
    });

    // 시간 옵션 생성 (1-12)
    for (let hour = 1; hour <= 12; hour++) {
        const hourOption = document.createElement('div');
        hourOption.className = 'time-option' + (hour === selectedHour ? ' selected' : '');
        hourOption.textContent = hour;
        hourOption.dataset.value = hour;

        hourOption.addEventListener('click', function() {
            // 이전 선택 해제
            hoursColumn.querySelectorAll('.time-option').forEach(opt => opt.classList.remove('selected'));
            // 새 선택 적용
            this.classList.add('selected');
            // 시간 업데이트
            updateSelectedTime(periodColumn, hoursColumn, minutesColumn, inputDisplay, inputHidden);
        });

        hoursColumn.appendChild(hourOption);
    }

    // 분 옵션 생성 (00-59)
    for (let minute = 0; minute < 60; minute++) {
        const minuteOption = document.createElement('div');
        minuteOption.className = 'time-option' + (minute === selectedMinute ? ' selected' : '');
        minuteOption.textContent = minute.toString().padStart(2, '0');
        minuteOption.dataset.value = minute;

        minuteOption.addEventListener('click', function() {
            // 이전 선택 해제
            minutesColumn.querySelectorAll('.time-option').forEach(opt => opt.classList.remove('selected'));
            // 새 선택 적용
            this.classList.add('selected');
            // 시간 업데이트
            updateSelectedTime(periodColumn, hoursColumn, minutesColumn, inputDisplay, inputHidden);
        });

        minutesColumn.appendChild(minuteOption);
    }
}

function updateSelectedTime(periodColumn, hoursColumn, minutesColumn, inputDisplay, inputHidden) {
    const selectedPeriod = periodColumn.querySelector('.time-option.selected');
    const selectedHour = hoursColumn.querySelector('.time-option.selected');
    const selectedMinute = minutesColumn.querySelector('.time-option.selected');

    if (selectedPeriod && selectedHour && selectedMinute) {
        const period = selectedPeriod.dataset.value;
        const hour = parseInt(selectedHour.dataset.value);
        const minute = parseInt(selectedMinute.dataset.value);

        // 표시용 형식 (오전/오후 12시간제)
        const formattedTime = `${period} ${hour}:${minute.toString().padStart(2, '0')}`;
        inputDisplay.value = formattedTime;

        // 내부 저장용 형식 (24시간제)
        let hour24 = hour;
        if (period === '오후' && hour < 12) hour24 = hour + 12;
        if (period === '오전' && hour === 12) hour24 = 0;

        const timeValue = `${hour24.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
        inputHidden.value = timeValue;

        // 드롭다운 닫기
        const dropdown = periodColumn.closest('.time-dropdown');
        if (dropdown) {
            dropdown.classList.remove('open');
        }
    }
}

function scrollToSelected(periodColumn, hoursColumn, minutesColumn) {
    const selectedPeriod = periodColumn.querySelector('.time-option.selected');
    const selectedHour = hoursColumn.querySelector('.time-option.selected');
    const selectedMinute = minutesColumn.querySelector('.time-option.selected');

    if (selectedPeriod) {
        selectedPeriod.scrollIntoView({ block: 'center', behavior: 'auto' });
    }

    if (selectedHour) {
        selectedHour.scrollIntoView({ block: 'center', behavior: 'auto' });
    }

    if (selectedMinute) {
        selectedMinute.scrollIntoView({ block: 'center', behavior: 'auto' });
    }
}

// 시간 초기화 함수 수정
function initializeTodoForm() {
    const today = new Date();

    // Set datepicker to today and trigger the onSelect
    $("#todo-date-display").datepicker("setDate", today);
    // Format the date as needed for our hidden field
    const formattedDate = formatDateInput(today);
    $("#todo-date").val(formattedDate);

    // 시간 초기화 (현재 시간)
    setTimeDisplay('todo', today);
}

// 시간 표시 설정 함수
function setTimeDisplay(prefix, dateObj) {
    let hours24 = dateObj.getHours();
    let minutes = dateObj.getMinutes();

    // 12시간제 변환
    let period = hours24 < 12 ? '오전' : '오후';
    let hours12 = hours24 % 12;
    if (hours12 === 0) hours12 = 12; // 12시간제에서는 0시가 아닌 12시로 표시

    // 표시 형식
    const displayTime = `${period} ${hours12}:${minutes.toString().padStart(2, '0')}`;
    document.getElementById(`${prefix}-time-display`).value = displayTime;

    // 내부 저장용 24시간제 형식
    const storedTime = `${hours24.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
    document.getElementById(`${prefix}-time`).value = storedTime;

    console.log(`${prefix} 시간 초기화:`, displayTime, '(저장:', storedTime, ')');
}

// 상세보기에서 시간 설정 함수
function setDetailTime(timeString) {
    const timeArray = timeString.split(':');
    if (timeArray.length !== 2) return;

    const hours = parseInt(timeArray[0]);
    const minutes = parseInt(timeArray[1]);

    const date = new Date();
    date.setHours(hours, minutes, 0, 0);

    setTimeDisplay('detail', date);
}

// Function to highlight today's date in the UI
function highlightTodayDate() {
    const now = new Date();
    const today = now.toISOString().slice(0, 10);

    // Check if selected date is today
    const dateDisplay = document.querySelector('.date-display');
    if (selectedDateKey === today) {
        dateDisplay.classList.add('today');
    } else {
        dateDisplay.classList.remove('today');
    }
}

// Function to go to today's date
function goToToday() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    selectedDateKey = today.toISOString().slice(0, 10);

    generateCalendar();

    // Find and select today's cell
    setTimeout(() => {
        const todayCell = document.querySelector('.date-cell.today');
        if (todayCell) {
            todayCell.classList.add('selected');
            updateSelectedDate(today.getDate(), today);
            renderTodos(allTodos);
            highlightTodayDate();
        }
    }, 100);
}

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
        highlightTodayDate();
    });

    return dateCell;
}


// 요일 포함된 날짜 표시 형식으로 변경
function updateSelectedDate(day, referenceDate = new Date()) {
    const dateDisplay = document.querySelector('.date-display');
    const date = new Date(referenceDate.getFullYear(), referenceDate.getMonth(), day);

    // 요일 배열 정의
    const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
    const dayOfWeek = weekdays[date.getDay()];

    // 날짜 형식 지정 (YYYY년 MM월 DD일 (요일))
    const options = {year: 'numeric', month: 'long', day: 'numeric'};
    const dateStr = date.toLocaleDateString('ko-KR', options);

    // 요일 추가한 형식으로 표시
    dateDisplay.innerHTML = `<i class="fas fa-calendar-day"></i> ${dateStr} (${dayOfWeek})`;

    // Check if the selected date is today
    const today = new Date();
    if (date.getFullYear() === today.getFullYear() &&
        date.getMonth() === today.getMonth() &&
        date.getDate() === today.getDate()) {
        dateDisplay.classList.add('today');
    } else {
        dateDisplay.classList.remove('today');
    }
}

function fetchMyTodos() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/html/auth/login.html';
        return;
    }

    fetch('/api/todo/me', {
        method: 'GET',
        headers: {
            'Authorization': token,
            'Content-Type': 'application/json'
        }
    })
        .then(async res => {
            if (res.status === 401) {
                const errorData = await res.json();
                alert(errorData.message || '인증이 만료되었습니다. 다시 로그인해주세요.');
                window.location.href = '/html/auth/login.html';
                return;
            }

            return res.json(); // 정상 응답 처리
        })
        .then(response => {
            if (!response) return; // 위에서 401로 빠진 경우 처리 중단

            console.log('내 투두:', response);
            if (Array.isArray(response.data?.personalTodosDto)) {
                allTodos = response.data.personalTodosDto;

                const today = new Date();
                today.setHours(0, 0, 0, 0);

                generateCalendar();
                renderTodos(allTodos);
                highlightTodayDate();

                // ✅ 정상 응답일 때만 본문 보이기
                document.body.style.display = 'block';
            } else {
                console.error('투두 데이터가 배열이 아님:', response.data);
            }
        })
        .catch(err => {
            console.error('투두 조회 실패', err);
            alert('데이터를 불러오는 중 오류가 발생했습니다.');
        });
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
            e.stopPropagation(); // 클릭 이벤트 버블링 방지 (상세보기 안 뜨게)
            e.preventDefault();  // 기본 동작 방지 (리다이렉트 방지)

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
                    // 상태만 변경하고 목록을 다시 불러오지 않음
                    // 체크박스 상태를 유지하기 위해 allTodos에서 해당 todo 업데이트
                    const todoIndex = allTodos.findIndex(t => t.id === todo.id);
                    if (todoIndex >= 0) {
                        allTodos[todoIndex].isCompleted = newStatus;
                    }
                })
                .catch(err => console.error('상태 업데이트 실패:', err));

            // 실제 체크박스 상태 변경 (preventDefault로 인한 기본 동작 방지 때문에 필요)
            this.checked = newStatus;
            return false; // 폼 제출 방지
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

        content.addEventListener('click', function (e) {
            document.getElementById('detail-title').value = todo.title;
            document.getElementById('detail-content').value = todo.content || '';

            // 날짜 및 시간 설정
            const todoDate = new Date(todo.targetDate);

            // 데이트피커 설정
            $("#detail-date-display").datepicker("setDate", todoDate);
            $("#detail-date").val(formatDateInput(todoDate));

            // 커스텀 시간 설정 함수 호출
            setDetailTime(formatTimeInput(todo.targetDate));

            document.getElementById('detail-status').value = todo.isCompleted;
            document.getElementById('save-detail').setAttribute('data-id', todo.id);
            document.getElementById('todo-detail-modal').style.display = 'flex';
        });

        taskList.appendChild(taskItem);
    });
}


function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    let hours = date.getHours();
    const minutes = date.getMinutes();

    // 12시간제로 변환
    const period = hours < 12 ? '오전' : '오후';
    if (hours > 12) hours -= 12;
    else if (hours === 0) hours = 12;

    return `${period} ${hours}:${minutes.toString().padStart(2, '0')}`;
}

function createTodo() {
    const token = localStorage.getItem('accessToken');
    if (!token) return alert('로그인이 필요합니다');

    const modal = document.getElementById('todo-modal');
    modal.style.display = 'flex';

    // Initialize form with today's date and time
    initializeTodoForm();

    document.getElementById('save-todo').onclick = function() {
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
                // Clear the form for next time
                document.getElementById('todo-title').value = '';
                document.getElementById('todo-content').value = '';
            })
            .catch(err => console.error('할 일 생성 실패:', err));
    };

    document.getElementById('cancel-todo').onclick = function() {
        modal.style.display = 'none';
    };
}

// jQuery DatePicker만 초기화하는 함수
function initializeDatePicker() {
    // Date picker for todo form
    $("#todo-date-display").datepicker({
        dateFormat: 'mm / dd (D)',
        dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
        dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        showMonthAfterYear: true,
        yearSuffix: '년',
        firstDay: 0,
        onSelect: function(dateText, inst) {
            const selectedDate = $(this).datepicker('getDate');
            const formattedDate = formatDateInput(selectedDate);
            $("#todo-date").val(formattedDate);
        }
    });

    // Date picker for detail form
    $("#detail-date-display").datepicker({
        dateFormat: 'mm / dd (D)',
        dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
        dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        showMonthAfterYear: true,
        yearSuffix: '년',
        firstDay: 0,
        onSelect: function(dateText, inst) {
            const selectedDate = $(this).datepicker('getDate');
            const formattedDate = formatDateInput(selectedDate);
            $("#detail-date").val(formattedDate);
        }
    });
}

window.onload = function() {
    if (selectedDateKey == null) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        selectedDateKey = today.toISOString().slice(0, 10);
    }

    // 날짜 선택기(jQuery DatePicker)만 초기화
    initializeDatePicker();
    // 커스텀 시간 선택기 초기화
    initializeCustomTimePickers();
    createStars();
    fetchMyTodos();
    highlightTodayDate();

    document.getElementById('close-detail').addEventListener('click', function() {
        document.getElementById('todo-detail-modal').style.display = 'none';
    });

    document.getElementById('prev-month').addEventListener('click', function() {
        console.log(selectedDateKey);
        const date = new Date(selectedDateKey);
        console.log(date);
        const prevMonth = new Date(date.getFullYear(), date.getMonth() - 1, 2);
        console.log(prevMonth);
        selectedDateKey = prevMonth.toISOString().slice(0, 10);
        console.log('selected day', selectedDateKey);

        generateCalendar();
        fetchMyTodos();
        highlightTodayDate();
    });

    document.getElementById('next-month').addEventListener('click', function() {
        const date = new Date(selectedDateKey);
        console.log(date);
        const nextMonth = new Date(date.getFullYear(), date.getMonth() + 1, 2);
        console.log(nextMonth);
        selectedDateKey = nextMonth.toISOString().slice(0, 10);
        console.log(selectedDateKey);

        generateCalendar();
        fetchMyTodos();
        highlightTodayDate();
    });

    // Add "Today" button event listener
    document.getElementById('today-btn').addEventListener('click', function() {
        goToToday();
    });

    document.querySelector('.add-task-btn').addEventListener('click', function() {
        initializeTodoForm(); // Initialize with today's date
        createTodo();
    });

    document.getElementById('save-detail').addEventListener('click', function() {
        const id = this.getAttribute('data-id');
        const title = document.getElementById('detail-title').value;
        const content = document.getElementById('detail-content').value;
        const dateInput = document.getElementById('detail-date').value;
        const time = document.getElementById('detail-time').value;
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

    document.getElementById('delete-detail').addEventListener('click', function() {
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