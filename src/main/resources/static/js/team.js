// 멤버 이니셜 생성 함수
function getInitials(name) {
    if (!name) return '??';

    // 공백으로 분리하여 각 단어의 첫 글자 가져오기
    const words = name.split(' ');
    if (words.length >= 2) {
        return (words[0][0] + words[1][0]).toUpperCase();
    }

    // 한 단어인 경우 첫 두 글자 또는 첫 글자만 반환
    return name.length > 1 ? name.substring(0, 2).toUpperCase() : name[0].toUpperCase();
}


// 탭 전환 관련 변수
let currentTab = 0; // 초기값은 '나의 팀' 탭 (인덱스 0)

// 팀 목록 컨테이너 - 한 번만 정의
const teamListContainer = document.querySelector('.team-list');

// 탭 메뉴 옵션 선택
const menuOptions = document.querySelectorAll('.menu-option');

// 탭 버튼 이벤트 설정 함수
function setupTabEvents() {
    const menuOptions = document.querySelectorAll('.menu-option');

    menuOptions.forEach(option => {
        option.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));

            // 활성 탭 표시 업데이트
            menuOptions.forEach(opt => opt.classList.remove('active'));
            this.classList.add('active');

            // 메뉴 슬라이더 위치 업데이트 (있는 경우)
            const menuSlider = document.getElementById('menu-slider');
            if (menuSlider) {
                // 슬라이더 너비는 버튼 너비에 맞게 (불필요하게 늘어나지 않도록)
                const optionWidth = this.offsetWidth;
                const optionLeft = this.offsetLeft;

                // 슬라이더 크기 및 위치 설정 (최대 너비 제한)
                menuSlider.style.width = `${optionWidth}px`;
                menuSlider.style.transform = `translateX(${optionLeft}px)`;

                // 슬라이더가 컨테이너를 벗어나지 않도록 제한
                const containerWidth = document.querySelector('.friends-menu').offsetWidth;
                if (optionLeft + optionWidth > containerWidth) {
                    menuSlider.style.width = `${containerWidth - optionLeft}px`;
                }
            }

            // 탭 상태 업데이트 및 데이터 로드 (이전과 같은 탭이어도 강제 로드)
            currentTab = index;

            if (index === 0) {
                loadMyTeams(); // 나의 팀 목록 로드
            } else if (index === 1) {
                loadTeamInvites(); // 팀 초대 목록 로드
            }
        });
    });
}

// 검색 기능 구현
const searchInput = document.querySelector('.search-input');
searchInput.addEventListener('input', function() {
    const searchTerm = this.value.toLowerCase().trim();

    // 현재 표시된 팀 아이템들을 필터링
    const teamItems = document.querySelectorAll('.team-item');

    teamItems.forEach(item => {
        const teamName = item.querySelector('.team-name')?.textContent.toLowerCase() || '';
        if (teamName.includes(searchTerm)) {
            item.style.display = 'flex';
        } else {
            item.style.display = 'none';
        }
    });
});

// 내 팀 목록을 불러오는 함수
function loadMyTeams() {
    // 로딩 표시
    teamListContainer.innerHTML = '<div class="loading">로딩 중...</div>';

    const token = localStorage.getItem('accessToken');
    if (!token) {
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/html/auth/login.html';
        return;
    }

    // API 호출
    fetch('/api/team/myteamlist', {
        method: 'GET',
        headers: {
            'Authorization': token
        }
    })
        .then(async response => {
            if (response.status === 401) {
                const errorData = await response.json();
                alert(errorData.message || '인증이 만료되었습니다. 다시 로그인해주세요.');
                localStorage.removeItem('accessToken');
                window.location.href = '/html/auth/login.html';
                return;
            }

            if (!response.ok) {
                throw new Error('팀 목록을 불러오는데 실패했습니다.');
            }

            return response.json();
        })
        .then(data => {
            if (!data) return; // 위에서 리턴된 경우 중단

            if (data.result === 'SUCCESS') {
                renderTeams(data.data); // API 응답 데이터 처리
            }
            else {
                teamListContainer.innerHTML = '<div class="error">팀 목록을 불러오는데 실패했습니다.</div>';
            }
        })
        .catch(error => {
            console.error('오류 발생:', error);
            teamListContainer.innerHTML = '<div class="error">서버 오류가 발생했습니다.</div>';
        });
}


// 팀 초대 목록 로드 함수
function loadTeamInvites() {
    // 로딩 표시
    teamListContainer.innerHTML = '<div class="loading">로딩 중...</div>';

    // API 호출
    fetch('/api/team/invitelist', {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem('accessToken')
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.result === 'SUCCESS') {
                displayTeamInvites(data.data);
            } else {
                // 오류 처리
                teamListContainer.innerHTML = '<div class="error">초대 목록을 불러오는데 실패했습니다.</div>';
            }
        })
        .catch(error => {
            console.error('초대 목록 로드 중 오류:', error);
            teamListContainer.innerHTML = '<div class="error">서버 오류가 발생했습니다.</div>';
        });
}

// 팀 목록 렌더링 함수
function renderTeams(teams) {
    // 컨테이너 비우기
    teamListContainer.innerHTML = '';

    if (teams.length === 0) {
        teamListContainer.innerHTML = '<div class="empty-list">소속된 팀이 없습니다.</div>';
        return;
    }

    // 각 팀 렌더링
    teams.forEach(teamData => {
        // 팀 이니셜 생성
        const team = teamData.myTeam;
        const userTeam = teamData.myUserTeam;
        const initials = getInitials(team.teamName);

        // HTML 생성
        const teamItem = document.createElement('div');
        teamItem.className = 'team-item';
        teamItem.innerHTML = `
    <div class="team-avatar">${initials}</div>
    <div class="team-info">
      <div class="team-name">
        ${team.teamName}
      </div>
    </div>
    <div class="team-actions">
      <button class="team-action-btn view-btn" title="선택" data-team-id="${team.id}">
        <i class="fas fa-sign-in-alt"></i>
      </button>
      <button class="team-action-btn kick-btn" title="탈퇴" data-team-id="${team.id}">
        <i class="fas fa-sign-out-alt"></i>
      </button>
    </div>
  `;

        teamListContainer.appendChild(teamItem);
    });

    // 버튼 이벤트 리스너 추가
    setupMemberActionButtons();
}

// 각 초대 렌더링 함수 수정
function displayTeamInvites(invites) {
    // 컨테이너 비우기
    teamListContainer.innerHTML = '';

    if (invites.length === 0) {
        teamListContainer.innerHTML = '<div class="empty-list">받은 초대가 없습니다.</div>';
        return;
    }

    // 각 초대 렌더링
    invites.forEach(invite => {
        // HTML 생성
        const team = invite.myTeam;
        const userTeam = invite.myUserTeam;
        const teamItem = document.createElement('div');

        teamItem.className = 'team-item';
        teamItem.innerHTML = `
<div class="invite-item" data-invite-id="${userTeam.id}" data-team-id="${team.id}">
  <div class="team-avatar">${getInitials(team.teamName)}</div>
  <div class="invite-content">
    <div class="team-info">
      <div class="team-name">${team.teamName}</div>
      <div class="invite-from">
        <i class="fas fa-user"></i> ${team.teamName}님이 초대함
      </div>
    </div>
    <div class="invite-actions">
      <button class="square-action-btn accept-btn" title="수락" data-invite-id="${userTeam.id}" data-team-id="${team.id}">
        <i class="fas fa-check"></i>
      </button>
      <button class="square-action-btn decline-btn" title="거절" data-invite-id="${userTeam.id}" data-team-id="${team.id}">
        <i class="fas fa-times"></i>
      </button>
    </div>
  </div>
</div>
  `;

        teamListContainer.appendChild(teamItem);
    });

    // 초대 버튼 이벤트 설정
    setupMemberActionButtons();
}

// 멤버 액션 버튼 이벤트 설정 함수 수정
function setupMemberActionButtons() {
    // 프로필 보기 버튼
    document.querySelectorAll('.view-btn').forEach(button => {
        button.addEventListener('click', function () {
            const teamId = this.getAttribute('data-team-id');
            window.location.href = `/html/todo/teamCalendar.html?teamId=${teamId}`;
        });
    });

    // 강퇴 버튼
    document.querySelectorAll('.kick-btn').forEach(button => {
        button.addEventListener('click', function () {
            const teamId = this.getAttribute('data-team-id');
            const teamItem = this.closest('.team-item');
            const teamName = teamItem.querySelector('.team-name').textContent.trim();

            if (!teamId) {
                alert('팀 ID를 찾을 수 없습니다.');
                return;
            }

            if (confirm(`정말로 ${teamName} 팀에서 탈퇴 하시겠습니까?`)) {
                // API 호출로 팀원 강퇴
                fetch(`/api/team/${teamId}/member`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': localStorage.getItem('accessToken')
                    }
                })
                    .then(response => {
                        // 응답 상태에 따라 다른 처리
                        if (!response.ok) {
                            // HTTP 에러 상태 (400, 401, 403, 404, 500 등)
                            return response.json().then(errorData => {
                                // 서버에서 보낸 에러 데이터 사용
                                throw new Error(errorData.message || '요청 처리 중 오류가 발생했습니다.');
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.message.includes('팀장')) {
                            alert('팀 리더는 탈퇴할 수 없습니다. 먼저 리더 권한을 다른 멤버에게 위임해주세요.');
                        } else {
                            console.log('API 응답:', data);
                            // 성공적인 응답 처리
                            alert(`${teamName}팀에서 탈퇴했습니다.`);
                            // UI 업데이트 로직...
                            loadMyTeams(); // 팀 목록 새로고침
                        }
                    })
                    .catch(error => {
                        console.error('오류 발생:', error);

                        // 에러 메시지에 따라 다른 처리
                        if (error.message.includes('리더는 팀을 탈퇴할 수 없습니다') ||
                            error.message === LEADER_CANNOT_DENY_TEAM) {
                            alert('팀 리더는 탈퇴할 수 없습니다. 먼저 리더 권한을 다른 멤버에게 위임해주세요.');
                        } else {
                            alert(`오류가 발생했습니다: ${error.message}`);
                        }
                    });
            }
        });
    });

    // 수락 버튼 - 클래스 이름 변경: accept-invite -> accept-btn
    document.querySelectorAll('.accept-btn').forEach(button => {
        button.addEventListener('click', function() {
            // 버튼에서 직접 data-team-id 속성 가져오기
            const teamId = this.getAttribute('data-team-id');

            if (!teamId) {
                console.error('팀 ID를 찾을 수 없습니다:', this);
                alert('팀 정보를 찾을 수 없습니다.');
                return;
            }

            console.log('초대 수락 시도 - 팀 ID:', teamId); // 디버깅용

            fetch(`/api/team/${teamId}/member`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('accessToken')
                }
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => {
                            throw new Error(errorData.message || '요청 처리 중 오류가 발생했습니다.');
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('초대 수락 성공:', data);
                    alert(data.message || '초대를 수락했습니다.');

                    // 초대 수락 후 처리
                    if (currentTab === 1) {
                        loadTeamInvites(); // 초대 목록 새로고침
                    } else {
                        loadMyTeams(); // 팀 목록으로 새로고침
                    }
                })
                .catch(error => {
                    console.error('초대 수락 중 오류:', error);
                    alert(`오류가 발생했습니다: ${error.message}`);
                });
        });
    });

    // 거절 버튼 - 클래스 이름 변경: decline-invite -> decline-btn
    document.querySelectorAll('.decline-btn').forEach(button => {
        button.addEventListener('click', function() {
            const teamId = this.getAttribute('data-team-id');

            if (!teamId) {
                console.error('팀 ID를 찾을 수 없습니다:', this);
                alert('팀 정보를 찾을 수 없습니다.');
                return;
            }

            console.log('초대 거절 시도 - 팀 ID:', teamId); // 디버깅용

            fetch(`/api/team/${teamId}/member`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('accessToken')
                }
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => {
                            throw new Error(errorData.message || '요청 처리 중 오류가 발생했습니다.');
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('초대 거절 성공:', data);
                    alert(data.message || '초대를 거절했습니다.');
                    loadTeamInvites(); // 초대 목록 새로고침
                })
                .catch(error => {
                    console.error('초대 거절 중 오류:', error);
                    alert(`오류가 발생했습니다: ${error.message}`);
                });
        });
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

// Set up menu slider
function setupMenuSlider() {
    const menuSlider = document.getElementById('menu-slider');
    const menuOptions = document.querySelectorAll('.menu-option');
    const activeOption = document.querySelector('.menu-option.active');

    if (menuSlider && activeOption) {
        // 활성 탭의 너비와 위치 계산
        const optionWidth = activeOption.offsetWidth;
        const optionLeft = activeOption.offsetLeft;

        // 슬라이더 스타일 설정 - 정확한 위치로 초기화
        menuSlider.style.width = `${optionWidth}px`;
        menuSlider.style.transform = `translateX(${optionLeft}px)`;
    }

    // 각 메뉴 옵션에 이벤트 리스너 추가
    menuOptions.forEach(option => {
        option.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));

            // 활성 탭 업데이트
            menuOptions.forEach(opt => opt.classList.remove('active'));
            this.classList.add('active');

            // 슬라이더 위치 업데이트
            if (menuSlider) {
                const optionWidth = this.offsetWidth;
                const optionLeft = this.offsetLeft;

                menuSlider.style.width = `${optionWidth}px`;
                menuSlider.style.transform = `translateX(${optionLeft}px)`;
            }
        });
    });
}

// Set up friend modal
function setupFriendModal() {
    const modal = document.getElementById('friend-modal');
    const addFriendBtn = document.getElementById('add-friend-btn');
    const closeModalBtn = document.getElementById('close-modal');
    const cancelBtn = document.getElementById('cancel-btn');
    const createTeamBtn = document.getElementById('create-team-btn');

    if (!modal || !addFriendBtn || !closeModalBtn || !cancelBtn || !createTeamBtn) {
        console.error('모달 요소를 찾을 수 없습니다.');
        return;
    }

    addFriendBtn.addEventListener('click', function() {
        console.log('모달 열기 버튼 클릭됨');  // 디버깅용
        modal.style.display = 'flex';
    });

    closeModalBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    cancelBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    createTeamBtn.addEventListener('click', function (){
        // 팀 이름과 설명 가져오기 - 수정된 placeholder에 맞게 선택자 변경
        const teamNameInput = document.getElementById('team-name');
        const teamDescInput = document.getElementById('team-desc');

        const teamName = teamNameInput.value.trim();
        const description = teamDescInput.value.trim();

        // 유효성 검사
        if (!teamName) {
            alert('팀 이름을 입력해주세요.');
            return;
        }

        // 팀 생성 요청 데이터
        const teamData = {
            teamName: teamName,
            description: description
        };

        // API 호출
        fetch('/api/team', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': localStorage.getItem('accessToken')
            },
            body: JSON.stringify(teamData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('팀 생성에 실패했습니다.');
                }
                return response.json();
            })
            .then(data => {
                if (data.result === 'SUCCESS') {
                    alert(data.message);

                    // 입력 필드 초기화
                    teamNameInput.value = '';
                    teamDescInput.value = '';

                    // 모달 닫기
                    modal.style.display = 'none';

                    // 팀 목록 새로고침
                    loadMyTeams();
                } else {
                    alert(data.message || '팀 생성에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('오류 발생:', error);
                alert(`오류가 발생했습니다: ${error.message}`);
            });
        modal.style.display = 'none';
    });
}

// 초기화 함수 - 메뉴 슬라이더 및 데이터 로드
function initTeamPanel() {
    // 탭 이벤트 설정
    setupTabEvents();

    // 새로고침 버튼 설정
    setupRefreshButton();

    // 초기 데이터 로드
    loadMyTeams();
}

// 새로고침 버튼 이벤트 추가 함수
function setupRefreshButton() {
    const refreshBtn = document.querySelector('.control-btn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', function () {
            // 현재 탭에 따라 데이터 새로고침
            if (currentTab === 0) {
                loadMyTeams();
            } else {
                loadTeamInvites();
            }
        });
    }
}

window.onload = function () {
    // 먼저 body를 표시하여 올바른 DOM 요소 크기와 위치를 계산할 수 있도록 함
    document.body.style.display = 'block';

    // 요소 생성 및 초기화
    createStars();
    setupFriendModal();

    // 메뉴 슬라이더를 body가 표시된 후에 설정
    setupMenuSlider();
    initTeamPanel();
}