// 현재 팀원 목록을 저장하는 변수
let currentTeamMembers = [];
let currentUser = null;

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

// URL에서 팀 ID 가져오기
function getTeamId() {
    const urlParams = new URLSearchParams(window.location.search);
    const teamId = urlParams.get('teamId');
    return teamId;
}

// 인증 헤더 생성
function authHeaders() {
    return {
        'Authorization': localStorage.getItem('accessToken'),
        'Content-Type': 'application/json'
    };
}

// 현재 사용자 정보 가져오기
async function getCurrentUser() {
    try {
        const response = await fetch('/api/users/me', {
            method: 'GET',
            headers: authHeaders()
        });

        if (!response.ok) {
            throw new Error('사용자 정보를 불러오는데 실패했습니다.');
        }

        const data = await response.json();
        if (data.result === 'SUCCESS') {
            currentUser = data.data;
            return data.data;
        } else {
            throw new Error('사용자 정보를 불러오는데 실패했습니다.');
        }
    } catch (error) {
        console.error('오류 발생:', error);
        alert(`사용자 정보를 불러오는 중 오류가 발생했습니다: ${error.message}`);
        return null;
    }
}

// 팀 정보 수정 함수 - 개선된 버전
function updateTeamInfo() {
    try {
        console.log('updateTeamInfo 함수 시작');
        const teamId = getTeamId();
        const teamName = document.getElementById('team-name').value.trim();
        const description = document.getElementById('team-info').value.trim();

        console.log('팀 정보:', { teamId, teamName, description });

        // 유효성 검사
        if (!teamName) {
            alert('팀 이름을 입력해주세요.');
            return;
        }

        // API 호출하여 팀 정보 수정
        fetch(`/api/team/${teamId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': localStorage.getItem('accessToken')
            },
            body: JSON.stringify({
                id: teamId,
                teamName: teamName,
                description: description
            })
        })
            .then(response => {
                console.log('API 응답 상태:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('API 응답 데이터:', data);

                // 서버 응답의 result 필드를 확인하여 성공/실패 처리
                if (data.result === 'SUCCESS') {
                    alert('팀 정보가 성공적으로 저장되었습니다.');
                } else {
                    // 서버에서 반환한 실패 메시지 표시
                    alert(`팀 정보 저장 실패: ${data.message || '알 수 없는 오류가 발생했습니다.'}`);
                    console.error('저장 실패:', data.message);
                }
            })
            .catch(error => {
                console.error('API 호출 중 오류 발생:', error);
                alert(`오류가 발생했습니다: ${error.message}`);
            });
    } catch (error) {
        console.error('updateTeamInfo 함수 내부 오류:', error);
        alert(`팀 정보 수정 중 오류가 발생했습니다: ${error.message}`);
    }
}

// 팀원 목록을 불러오는 함수
async function loadTeamMembers(teamId) {
    try {
        const response = await fetch(`/api/team/${teamId}/member/list`, {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('accessToken'),
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401) {
            alert('로그인이 필요합니다.');
            window.location.href = '/html/auth/login.html';
            return;
        }

        if (response.status === 403) {
            const err = await response.json();
            alert(err.message || '팀에 대한 접근 권한이 없습니다.');
            window.location.href = document.referrer || '/html/todo/myCalendar.html';
            return;
        }

        if (!response.ok) {
            throw new Error('팀원 목록을 불러오는데 실패했습니다.');
        }

        const data = await response.json();
        currentTeamMembers = data.data || [];
        console.log('여기까진 성공');
        renderTeamMembers(currentTeamMembers);
        return data;

    } catch (error) {
        console.error('오류 발생:', error);
        alert(`팀원 목록을 불러오는 중 오류가 발생했습니다: ${error.message}`);
    }
}

// 팀원 목록 렌더링 함수
function renderTeamMembers(members) {
    const memberListContainer = document.querySelector('.member-list');

    // 컨테이너 비우기
    memberListContainer.innerHTML = '';

    // 팀장이 먼저 표시되도록 정렬
    const sortedMembers = [...members].sort((a, b) => {
        if (a.role === 'LEADER') return -1;
        if (b.role === 'LEADER') return 1;
        return 0;
    });

    // 각 멤버 렌더링
    sortedMembers.forEach(member => {
        // 멤버 이니셜 생성
        const initials = getInitials(member.userNickName || member.userName);

        // 팀장 여부 확인
        const isLeader = member.role === 'LEADER';

        // HTML 생성
        const memberItem = document.createElement('div');
        memberItem.className = 'member-item';

        // 방출 버튼이 항상 보이도록 HTML 수정
        memberItem.innerHTML = `
            <div class="member-avatar">${initials}</div>
            <div class="member-info">
                <div class="member-name">
                    ${member.userNickName || member.userName}
                    ${isLeader ? '<span class="leader-badge">팀장</span>' : ''}
                </div>
            </div>
            <div class="member-actions always-visible">
                ${(!isLeader) ? `
                    <button class="member-action-btn kick-btn" title="팀에서 내보내기" data-user-id="${member.userId}">
                        <i class="fas fa-user-minus"></i>
                    </button>
                ` : ''}
            </div>
        `;

        memberListContainer.appendChild(memberItem);
    });

    // 버튼 이벤트 리스너 추가
    setupMemberActionButtons();
}

// 멤버 액션 버튼 이벤트 설정
function setupMemberActionButtons() {
    // 강퇴 버튼
    document.querySelectorAll('.kick-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            const memberItem = this.closest('.member-item');
            const memberName = memberItem.querySelector('.member-name').textContent.trim();

            if (!userId) {
                alert('사용자 ID를 찾을 수 없습니다.');
                return;
            }

            // 현재 팀 ID 가져오기
            const teamId = getTeamId();

            if (!teamId) {
                alert('팀 ID를 찾을 수 없습니다.');
                return;
            }

            if (confirm(`정말로 ${memberName}님을 팀에서 내보내시겠습니까?`)) {
                // API 호출로 팀원 강퇴
                fetch(`/api/team/${teamId}/member/${userId}`, {
                    method: 'DELETE',
                    headers: authHeaders()
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('팀원 강퇴에 실패했습니다.');
                        }
                        return response.json();
                    })
                    .then(data => {
                        // 성공 시 UI에서 멤버 제거 (애니메이션 효과)
                        memberItem.style.transition = 'all 0.3s ease';
                        memberItem.style.opacity = '0';
                        memberItem.style.height = '0';
                        memberItem.style.padding = '0';
                        memberItem.style.margin = '0';
                        memberItem.style.overflow = 'hidden';

                        setTimeout(() => {
                            memberItem.remove();

                            // 현재 팀원 목록에서도 제거
                            const memberIndex = currentTeamMembers.findIndex(m => m.userId === userId);
                            if (memberIndex > -1) {
                                currentTeamMembers.splice(memberIndex, 1);
                            }
                        }, 300);

                        alert(`${memberName}님을 팀에서 내보냈습니다.`);
                    })
                    .catch(error => {
                        console.error('오류 발생:', error);
                        alert(`오류가 발생했습니다: ${error.message}`);
                    });
            }
        });
    });
}

// 별 생성 함수
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

// 버튼 이벤트 설정
// 버튼 이벤트 설정 함수 재정의
function setupButtons() {
    try {
        console.log('setupButtons 함수 시작');

        const backButton = document.getElementById('back-button');
        console.log('backButton:', backButton);

        const teamCalendarButton = document.getElementById('team-calendar-button');
        console.log('teamCalendarButton:', teamCalendarButton);

        const deleteTeamButton = document.getElementById('delete-team-button');
        console.log('deleteTeamButton:', deleteTeamButton);

        const saveButton = document.getElementById('save-button');
        console.log('saveButton:', saveButton);

        if (backButton) {
            backButton.addEventListener('click', function() {
                if (confirm('팀 목록으로 돌아가시겠습니까?')) {
                    window.location.href = 'team.html';
                }
            });
            console.log('backButton 이벤트 설정 완료');
        } else {
            console.warn('back-button 요소를 찾을 수 없습니다');
        }

        if (teamCalendarButton) {
            teamCalendarButton.addEventListener('click', function() {
                const teamId = getTeamId();
                window.location.href = `/html/todo/teamCalendar.html?teamId=${teamId}`;
            });
            console.log('teamCalendarButton 이벤트 설정 완료');
        } else {
            console.warn('team-calendar-button 요소를 찾을 수 없습니다');
        }

        if (saveButton) {
            saveButton.addEventListener('click', updateTeamInfo);
            console.log('saveButton 이벤트 설정 완료');
        } else {
            console.warn('save-button 요소를 찾을 수 없습니다');
        }

        if (deleteTeamButton) {
            deleteTeamButton.addEventListener('click', function() {
                const teamId = getTeamId();
                if (confirm('정말로 팀을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
                    if (confirm('모든 팀 데이터가 영구적으로 삭제됩니다. 계속하시겠습니까?')) {
                        // API 호출하여 팀 삭제
                        fetch(`/api/team/${teamId}`, {
                            method: 'DELETE',
                            headers: {
                                'Authorization': localStorage.getItem('accessToken'),
                                'Content-Type': 'application/json'
                            }
                        })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('팀 삭제에 실패했습니다.');
                                }
                                return response.json();
                            })
                            .then(data => {
                                alert('팀이 성공적으로 삭제되었습니다.');
                                window.location.href = 'team.html';
                            })
                            .catch(error => {
                                console.error('오류 발생:', error);
                                alert(`오류가 발생했습니다: ${error.message}`);
                            });
                    }
                }
            });
            console.log('deleteTeamButton 이벤트 설정 완료');
        } else {
            console.warn('delete-team-button 요소를 찾을 수 없습니다');
        }

        console.log('setupButtons 함수 완료');
    } catch (error) {
        console.error('setupButtons 함수 오류:', error);
    }
}

// 사용자 검색 기능
async function searchUser() {
    const username = document.getElementById('username-input').value.trim();
    if (!username) {
        return alert('검색할 사용자 이름을 입력해주세요.');
    }

    try {
        const res = await fetch(`/api/users/${encodeURIComponent(username)}`, {
            headers: authHeaders()
        });
        const result = await res.json();
        const container = document.getElementById('search-results');
        container.innerHTML = '';

        // 검색 결과가 있는 경우 컨테이너를 활성화
        if (result.result === 'SUCCESS' && result.data) {
            container.classList.add('active');
            const user = result.data;

            // 이미 팀에 있는 사용자인지 확인
            const isAlreadyInTeam = currentTeamMembers.some(member => member.userId === user.id);

            // 본인인지 확인
            const isCurrentUser = currentUser && currentUser.id === user.id;

            const div = document.createElement('div');
            div.className = 'search-result-item';

            // 버튼 상태 결정
            let buttonHtml = '';
            if (isCurrentUser) {
                buttonHtml = `<button class="request-btn" disabled>본인입니다</button>`;
            } else {
                // 팀에 이미 속해 있는 경우, 팀장인지 일반 팀원인지 확인
                const teamMember = currentTeamMembers.find(member => member.userId === user.id);
                if (teamMember) {
                    if (teamMember.role === 'LEADER') {
                        buttonHtml = `<button class="request-btn" disabled>팀장입니다</button>`;
                    } else {
                        buttonHtml = `<button class="request-btn" disabled>등록된 팀원</button>`;
                    }
                } else {
                    buttonHtml = `<button class="request-btn invite-btn" data-user-id="${user.id}"><i class="fas fa-user-plus"></i> 초대하기</button>`;
                }
            }

            div.innerHTML = `
                <div class="search-result-info">
                    <div class="search-result-avatar">${user.nickName?.charAt(0) || 'U'}</div>
                    <div>
                        <div class="search-result-name">${user.nickName || user.userName}</div>
                        <div class="friend-email">${user.email || ''}</div>
                    </div>
                </div>
                ${buttonHtml}
            `;
            container.appendChild(div);
            setupInviteButtons();
        } else {
            container.classList.add('active');
            container.innerHTML = '<div class="no-results">검색 결과가 없습니다.</div>';
        }
    } catch (error) {
        console.error('사용자 검색 중 오류:', error);
        alert('사용자 검색 중 오류가 발생했습니다.');
    }
}

// 초대 버튼 이벤트 설정
function setupInviteButtons() {
    document.querySelectorAll('.invite-btn').forEach(button => {
        button.addEventListener('click', async function() {
            const userId = this.getAttribute('data-user-id');
            const userName = this.closest('.search-result-item').querySelector('.search-result-name').textContent;
            const teamId = getTeamId();

            if (confirm(`${userName}님을 팀에 초대하시겠습니까?`)) {
                try {
                    // API 호출로 팀원 초대
                    const response = await fetch(`/api/team/${teamId}/member`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': localStorage.getItem('accessToken')
                        },
                        body: JSON.stringify({
                            userId: userId,
                            teamId: teamId,
                            search: userName
                        })
                    });

                    if (!response.ok) {
                        throw new Error('팀원 초대에 실패했습니다.');
                    }

                    const data = await response.json();
                    alert(`${userName}님에게 초대 메시지가 전송되었습니다.`);

                    // 버튼 상태 업데이트
                    this.disabled = true;
                    this.innerHTML = '초대 완료';
                    this.classList.remove('invite-btn');

                    // 검색 입력 필드 초기화
                    document.getElementById('username-input').value = '';
                } catch (error) {
                    console.error('초대 중 오류:', error);
                    alert(`초대 중 오류가 발생했습니다: ${error.message}`);
                }
            }
        });
    });
}

// 팀 정보 로드 함수
async function loadTeamInfo(teamId) {
    try {
        // 로딩 상태 표시
        const teamNameInput = document.getElementById('team-name');
        const teamInfoInput = document.getElementById('team-info');

        if (teamNameInput) teamNameInput.value = '로딩 중...';
        if (teamInfoInput) teamInfoInput.value = '로딩 중...';

        const response = await fetch(`/api/team/${teamId}`, {
            method: 'GET',
            headers: authHeaders()
        });

        if (!response.ok) {
            throw new Error('팀 정보를 불러오는데 실패했습니다.');
        }

        const data = await response.json();

        // API 응답 구조 확인
        if (data.result === 'SUCCESS' && data.data) {
            const team = data.data;

            // 폼에 데이터 채우기
            if (teamNameInput) teamNameInput.value = team.teamName || '';
            if (teamInfoInput) teamInfoInput.value = team.description || '';
        } else {
            throw new Error(data.message || '팀 정보를 불러오는데 실패했습니다.');
        }
    } catch (error) {
        console.error('팀 정보 로드 중 오류:', error);
        alert(`팀 정보를 불러오는 중 오류가 발생했습니다: ${error.message}`);

        // 입력 필드 초기화
        const teamNameInput = document.getElementById('team-name');
        const teamInfoInput = document.getElementById('team-info');

        if (teamNameInput) teamNameInput.value = '';
        if (teamInfoInput) teamInfoInput.value = '';
    }
}

// 검색 기능 이벤트 설정
function setupSearchFunctionality() {
    try {
        const searchButton = document.getElementById('search-button');
        const usernameInput = document.getElementById('username-input');
        const searchResultsContainer = document.getElementById('search-results');

        // DOM 요소 확인
        if (!searchButton) {
            console.error('search-button 요소를 찾을 수 없습니다.');
            return;
        }
        if (!usernameInput) {
            console.error('username-input 요소를 찾을 수 없습니다.');
            return;
        }
        if (!searchResultsContainer) {
            console.error('search-results 요소를 찾을 수 없습니다.');
            return;
        }

        // searchUser 함수가 있는지 확인
        if (typeof searchUser !== 'function') {
            console.error('searchUser 함수가 정의되지 않았습니다.');
            return;
        }

        // 검색 버튼 클릭 이벤트
        searchButton.addEventListener('click', searchUser);

        // 엔터 키 이벤트
        usernameInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchUser();
            }
        });

        // 검색 입력 필드 포커스 이벤트
        usernameInput.addEventListener('focus', function() {
            // 검색 결과 컨테이너가 비어있지 않으면 표시
            if (searchResultsContainer.innerHTML.trim() !== '') {
                searchResultsContainer.classList.add('active');
            }
        });

        // 검색 입력 필드 변경 이벤트 (초기화 시)
        usernameInput.addEventListener('input', function() {
            if (this.value.trim() === '') {
                // 입력 필드가 비었을 때 검색 결과 숨기기
                searchResultsContainer.classList.remove('active');
                searchResultsContainer.innerHTML = '';
            }
        });

        // 문서 클릭 이벤트 (검색 결과 외부 클릭 시 결과 숨기기)
        document.addEventListener('click', function(e) {
            // 클릭된 요소가 검색 폼이나 검색 결과 컨테이너의 자식이 아니면 결과 숨기기
            if (!e.target.closest('.search-form')) {
                searchResultsContainer.classList.remove('active');
            }
        });

        console.log('검색 기능 설정 완료!');
    } catch (error) {
        console.error('setupSearchFunctionality 함수에서 오류 발생:', error);
    }
}

// 페이지 초기화
document.addEventListener('DOMContentLoaded', async function() {
    const teamId = getTeamId();

    if (!teamId) {
        alert('팀 ID를 찾을 수 없습니다. 팀 목록 페이지로 이동합니다.');
        window.location.href = 'team.html';
        return;
    }

    // ✅ 팀장 권한 확인 먼저 수행
    try {
        const res = await fetch(`/api/team/${teamId}/check-leader`, {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('accessToken')
            }
        });

        if (res.status === 401) {
            alert('로그인이 필요합니다.');
            window.location.href = '/html/auth/login.html';
            return;
        }

        if (res.status === 403) {
            const err = await res.json();
            alert(err.message || '팀장만 접근할 수 있는 페이지입니다.');
            window.location.href = document.referrer || '/html/todo/myCalendar.html';
            return;
        }

        if (!res.ok) {
            throw new Error('권한 확인 중 오류가 발생했습니다.');
        }

        // ✅ 팀장 권한 확인 성공 시, 페이지 표시 및 초기화
        document.body.style.display = 'block';

        console.log('getCurrentUser 시작');
        await getCurrentUser();
        console.log('getCurrentUser 완료');

        console.log('createStars 시작');
        createStars();
        console.log('createStars 완료');

        console.log('setupButtons 시작');
        setupButtons();
        console.log('setupButtons 완료');

        console.log('setupSearchFunctionality 시작');
        setupSearchFunctionality();
        console.log('setupSearchFunctionality 완료');

        // 팀 정보와 멤버 로드
        console.log('loadTeamInfo 시작');
        await loadTeamInfo(teamId);
        console.log('loadTeamInfo 완료');

        console.log('loadTeamMembers 시작');
        await loadTeamMembers(teamId);
        console.log('loadTeamMembers 완료');

    } catch (err) {
        console.error('권한 확인 중 오류:', err);
        alert('페이지 접근 권한을 확인하는 중 오류가 발생했습니다.');
        window.location.href = 'team.html';
    }
});