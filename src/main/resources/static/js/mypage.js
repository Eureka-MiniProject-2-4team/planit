// 비밀번호 변경 관련 모달 설정
function setupPasswordModals() {
    // 요소 참조
    const changePasswordBtn = document.getElementById('change-password-btn');

    const verifyPasswordModal = document.getElementById('verify-password-modal');
    const closeVerifyPasswordModal = document.getElementById('close-verify-password-modal');
    const cancelVerifyPassword = document.getElementById('cancel-verify-password');
    const confirmVerifyPassword = document.getElementById('confirm-verify-password');
    const currentPasswordVerify = document.getElementById('current-password-verify');

    const passwordModal = document.getElementById('password-modal');
    const closePasswordModal = document.getElementById('close-password-modal');
    const cancelPasswordChange = document.getElementById('cancel-password-change');
    const confirmPasswordChange = document.getElementById('confirm-password-change');
    const newPassword = document.getElementById('new-password');
    const confirmPassword = document.getElementById('confirm-password');

    // 모달 초기화 - 모든 모달 숨기기
    verifyPasswordModal.style.display = 'none';
    passwordModal.style.display = 'none';

    // 비밀번호 변경 버튼 클릭 시 비밀번호 확인 모달 표시
    changePasswordBtn.addEventListener('click', function() {
        currentPasswordVerify.value = ''; // 입력 필드 초기화
        verifyPasswordModal.style.display = 'flex';
        passwordModal.style.display = 'none'; // 다른 모달 확실히 숨기기
    });

    // 비밀번호 확인 모달 닫기
    closeVerifyPasswordModal.addEventListener('click', function() {
        verifyPasswordModal.style.display = 'none';
    });

    cancelVerifyPassword.addEventListener('click', function() {
        verifyPasswordModal.style.display = 'none';
    });

    // 비밀번호 확인 후 비밀번호 변경 모달 표시
    confirmVerifyPassword.addEventListener('click', function() {
        const password = currentPasswordVerify.value;

        if (!password) {
            alert('비밀번호를 입력해주세요.');
            return;
        }

        // 비밀번호 확인 API 호출 - 실제 구현 시 주석 해제
        verifyPassword(password)
            .then(response => {
                if (response.result === 'SUCCESS') {
                    verifyPasswordModal.style.display = 'none';

                    // 비밀번호 변경 모달 표시
                    newPassword.value = '';
                    confirmPassword.value = '';
                    passwordModal.style.display = 'flex';
                } else {
                    alert(response.message || '비밀번호가 일치하지 않습니다.');
                }
            })
            .catch(error => {
                console.error('비밀번호 확인 오류:', error);
                alert('서버 오류: 비밀번호 확인에 실패했습니다.');
            });
    });

    // 비밀번호 변경 모달 닫기
    closePasswordModal.addEventListener('click', function() {
        passwordModal.style.display = 'none';
    });

    cancelPasswordChange.addEventListener('click', function() {
        passwordModal.style.display = 'none';
    });

    // 비밀번호 변경 처리
    confirmPasswordChange.addEventListener('click', function() {
        const newPasswordValue = newPassword.value;
        const confirmPasswordValue = confirmPassword.value;

        // 입력 검증
        if (!newPasswordValue || !confirmPasswordValue) {
            alert('모든 필드를 입력해주세요.');
            return;
        }

        // 비밀번호 일치 확인
        if (newPasswordValue !== confirmPasswordValue) {
            alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
            return;
        }

        // 비밀번호 변경 API 호출 - 실제 구현 시 주석 해제
        const currentPassword = currentPasswordVerify.value;
        changePassword(currentPassword, newPasswordValue)
            .then(response => {
                if (response.result === 'SUCCESS') {
                    alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.');
                    localStorage.removeItem('accessToken'); // 토큰 제거
                    window.location.href = '/html/auth/login.html'; // 로그인 페이지로 이동
                } else {
                    alert(response.message || '비밀번호 변경 실패');
                }
            })
            .catch(error => {
                console.error('비밀번호 변경 오류:', error);
                alert('서버 오류: 비밀번호 변경에 실패했습니다.');
            });
    });

    // ESC키로 모달 닫기
    window.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            verifyPasswordModal.style.display = 'none';
            passwordModal.style.display = 'none';
        }
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        if (e.target === verifyPasswordModal) {
            verifyPasswordModal.style.display = 'none';
        }
        if (e.target === passwordModal) {
            passwordModal.style.display = 'none';
        }
    });
}// 닉네임 수정 기능 설정
function setupNicknameEdit() {
    const checkNicknameBtn = document.getElementById('check-nickname-btn');
    const editNicknameBtn = document.getElementById('edit-nickname-btn');
    const nicknameInput = document.getElementById('nickname');
    const nicknameStatus = document.getElementById('nickname-status');

    let isNicknameVerified = false;
    const originalNickname = nicknameInput.value.trim();

    // 닉네임 입력 필드 변경 시 검증 상태 초기화
    nicknameInput.addEventListener('input', function() {
        isNicknameVerified = false;
        editNicknameBtn.disabled = true;
        nicknameStatus.textContent = '';
        nicknameStatus.className = 'input-status';

        // 원래 닉네임과 같으면 중복 확인 없이 수정 가능하게 처리
        if (nicknameInput.value.trim() === originalNickname) {
            nicknameStatus.textContent = '현재 사용 중인 닉네임입니다.';
            nicknameStatus.classList.add('success');
            isNicknameVerified = true;
            editNicknameBtn.disabled = false;
        }
    });

    // 중복 확인 버튼 클릭 처리
    checkNicknameBtn.addEventListener('click', function() {
        const nickname = nicknameInput.value.trim();

        if (!nickname) {
            nicknameStatus.textContent = '닉네임을 입력해주세요.';
            nicknameStatus.className = 'input-status error';
            return;
        }

        if (nickname === originalNickname) {
            nicknameStatus.textContent = '현재 사용 중인 닉네임입니다.';
            nicknameStatus.className = 'input-status success';
            isNicknameVerified = true;
            editNicknameBtn.disabled = false;
            return;
        }

        // 중복 확인 API 호출 - 실제 구현 시 사용
        checkNicknameDuplicate(nickname)
            .then(response => {
                if (response.result === 'SUCCESS') {
                    nicknameStatus.textContent = '사용 가능한 닉네임입니다.';
                    nicknameStatus.className = 'input-status success';
                    isNicknameVerified = true;
                    editNicknameBtn.disabled = false;
                } else {
                    nicknameStatus.textContent = response.message || '이미 사용 중인 닉네임입니다.';
                    nicknameStatus.className = 'input-status error';
                    isNicknameVerified = false;
                    editNicknameBtn.disabled = true;
                }
            })
            .catch(error => {
                console.error('닉네임 중복 확인 오류:', error);
                nicknameStatus.textContent = '서버 오류: 중복 확인에 실패했습니다.';
                nicknameStatus.className = 'input-status error';
            });
    });

    // 수정 버튼 클릭 처리
    editNicknameBtn.addEventListener('click', function() {
        const nickname = nicknameInput.value.trim();

        if (!isNicknameVerified) {
            alert('닉네임 중복 확인이 필요합니다.');
            return;
        }

        // 실제 구현 시 API 호출 - 변경 요청
        updateUserProfile(nickname)
            .then(success => {
                if (success) {
                    alert('닉네임이 변경되었습니다.');
                    document.querySelector('.profile-name').textContent = nickname;
                }
            });
    });
}

// 중복 확인 API 함수 - 실제 구현 시 사용
async function checkNicknameDuplicate(nickname) {
    try {
        const response = await fetch(`/auth/check-nickName?value=${encodeURIComponent(nickname)}`);
        return await response.json();
    } catch (error) {
        console.error('닉네임 중복 확인 중 오류:', error);
        throw error;
    }
}// 별 생성 함수
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

// 모달 관리
function setupModals() {
    // 비밀번호 변경 모달
    const passwordChangeBtn = document.getElementById('change-password-btn');
    const passwordModal = document.getElementById('password-modal');
    const closePasswordModal = document.getElementById('close-password-modal');
    const cancelPasswordChange = document.getElementById('cancel-password-change');
    const confirmPasswordChange = document.getElementById('confirm-password-change');

    // 비밀번호 변경 버튼 클릭 시 모달 표시
    passwordChangeBtn.addEventListener('click', function() {
        passwordModal.style.display = 'flex';
    });

    // 닫기 버튼 클릭 시 모달 닫기
    closePasswordModal.addEventListener('click', function() {
        passwordModal.style.display = 'none';
    });

    // 취소 버튼 클릭 시 모달 닫기
    cancelPasswordChange.addEventListener('click', function() {
        passwordModal.style.display = 'none';
    });

    // 비밀번호 변경 버튼 클릭 시 처리
    confirmPasswordChange.addEventListener('click', function() {
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmPassword = document.getElementById('confirm-password').value;

        // 입력 검증
        if (!currentPassword || !newPassword || !confirmPassword) {
            alert('모든 필드를 입력해주세요.');
            return;
        }

        // 비밀번호 일치 확인
        if (newPassword !== confirmPassword) {
            alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
            return;
        }

        // 비밀번호 변경 API 호출 - 실제 구현 시 주석 해제
        changePassword(currentPassword, newPassword)
            .then(response => {
                if (response.result === 'SUCCESS') {
                    alert('비밀번호가 변경되었습니다.');
                    passwordModal.style.display = 'none';
                } else {
                    alert(response.message || '비밀번호 변경 실패');
                }
            })
            .catch(error => {
                console.error('비밀번호 변경 오류:', error);
                alert('서버 오류: 비밀번호 변경에 실패했습니다.');
            });

    });

    // 계정 비활성화 및 회원 탈퇴
    const deactivateBtn = document.getElementById('deactivate-btn');
    const deleteAccountBtn = document.getElementById('delete-account-btn');
    const verifyModal = document.getElementById('verify-modal');
    const closeVerifyModal = document.getElementById('close-verify-modal');
    const cancelVerify = document.getElementById('cancel-verify');
    const confirmVerify = document.getElementById('confirm-verify');

    // 확인 모달
    const confirmActionModal = document.getElementById('confirm-action-modal');
    const closeConfirmModal = document.getElementById('close-confirm-modal');
    const cancelAction = document.getElementById('cancel-action');
    const confirmAction = document.getElementById('confirm-action');

    let currentAction = null;

    // 비활성화 버튼
    deactivateBtn.addEventListener('click', function() {
        currentAction = 'deactivate';
        document.getElementById('confirm-action-title').textContent = '계정 비활성화';
        document.getElementById('confirm-action-message').textContent =
            '로그인을 하시면 계정 비활성화가 자동으로 해제됩니다. 계속하시겠습니까?';
        confirmActionModal.style.display = 'flex';
    });

    // 탈퇴 버튼
    deleteAccountBtn.addEventListener('click', function() {
        currentAction = 'delete';
        document.getElementById('confirm-action-title').textContent = '회원 탈퇴';
        document.getElementById('confirm-action-message').textContent =
            '회원 탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다. 정말로 탈퇴하시겠습니까?';
        confirmActionModal.style.display = 'flex';
    });

    // 확인 모달 닫기
    closeConfirmModal.addEventListener('click', function() {
        confirmActionModal.style.display = 'none';
    });

    // 확인 모달 취소
    cancelAction.addEventListener('click', function() {
        confirmActionModal.style.display = 'none';
    });

    // 확인 모달 확인
    confirmAction.addEventListener('click', function() {
        confirmActionModal.style.display = 'none';

        // 비밀번호 확인 모달 표시
        verifyModal.style.display = 'flex';
    });

    // 비밀번호 확인 모달 닫기
    closeVerifyModal.addEventListener('click', function() {
        verifyModal.style.display = 'none';
    });

    // 비밀번호 확인 취소
    cancelVerify.addEventListener('click', function() {
        verifyModal.style.display = 'none';
    });

    // 비밀번호 확인 완료
    confirmVerify.addEventListener('click', function() {
        const password = document.getElementById('verify-password').value;

        if (!password) {
            alert('비밀번호를 입력해주세요.');
            return;
        }

        // 비밀번호 확인 API 호출 - 실제 구현 시 주석 해제
        verifyPassword(password)
            .then(response => {
                if (response.result === 'SUCCESS') {
                    verifyModal.style.display = 'none';

                    if (currentAction === 'deactivate') {
                        deactivateAccount();
                    } else if (currentAction === 'delete') {
                        deleteAccount();
                    }
                } else {
                    alert(response.message || '비밀번호가 일치하지 않습니다.');
                }
            })
            .catch(error => {
                console.error('비밀번호 확인 오류:', error);
                alert('서버 오류: 비밀번호 확인에 실패했습니다.');
            });


    });

    // ESC키로 모달 닫기
    window.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            passwordModal.style.display = 'none';
            verifyModal.style.display = 'none';
            confirmActionModal.style.display = 'none';
        }
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        if (e.target === passwordModal) {
            passwordModal.style.display = 'none';
        }
        if (e.target === verifyModal) {
            verifyModal.style.display = 'none';
        }
        if (e.target === confirmActionModal) {
            confirmActionModal.style.display = 'none';
        }
    });
}

// API 함수들 - 실제 구현 시 사용
async function loadUserData() {
    try {
        const token = localStorage.getItem('accessToken');
        if (!token) throw new Error('토큰 없음');

        const response = await fetch('/api/users/me', {
            method: 'GET',
            headers: {
                'Authorization': token
            }
        });

        const result = await response.json();

        if (!response.ok || result.result !== 'SUCCESS') {
            throw new Error(result.message || '사용자 정보 로드 실패');
        }

        const userData = result.data;

        // 사용자 정보 표시
        document.getElementById('username').value = userData.userName;
        document.getElementById('nickname').value = userData.nickname;
        document.getElementById('email').value = userData.email;

        document.querySelector('.profile-name').textContent = userData.nickname || userData.userName;
        document.querySelector('.profile-email').textContent = userData.email;

        const initials = (userData.nickname || userData.userName).substring(0, 2).toUpperCase();
        document.querySelector('.profile-avatar').textContent = initials;

        return userData;

    } catch (error) {
        console.error('사용자 데이터 로드 실패:', error.message);
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        localStorage.removeItem('accessToken');
        window.location.href = '/html/auth/login.html';
    }
}


async function updateUserProfile(nickname) {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/users/me', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify({
                newNickName: nickname
            })
        });

        const result = await response.json();

        if (response.ok && result.result === 'SUCCESS') {
            return true;
        } else {
            alert(result.message || '프로필 업데이트 실패');
            return false;
        }
    } catch (error) {
        console.error('프로필 업데이트 중 오류:', error);
        alert('서버 오류: 프로필 업데이트에 실패했습니다.');
        return false;
    }
}

async function changePassword(currentPassword, newPassword) {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/users/me/password', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        });

        return await response.json();
    } catch (error) {
        console.error('비밀번호 변경 중 오류:', error);
        throw error;
    }
}

async function verifyPassword(password) {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/auth/verify-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify({
                password: password
            })
        });

        return await response.json();
    } catch (error) {
        console.error('비밀번호 확인 중 오류:', error);
        throw error;
    }
}

async function deactivateAccount() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/users/me', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify({
                isActive: false
            })
        });

        const result = await response.json();

        if (response.ok && result.result === 'SUCCESS') {
            alert('계정이 비활성화되었습니다.');
            localStorage.removeItem('accessToken');
            window.location.href = '/html/auth/login.html';
        } else {
            alert(result.message || '계정 비활성화 실패');
        }
    } catch (error) {
        console.error('계정 비활성화 중 오류:', error);
        alert('서버 오류: 계정 비활성화에 실패했습니다.');
    }
}

async function deleteAccount() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/users/me', {
            method: 'DELETE',
            headers: {
                'Authorization': token
            }
        });

        const result = await response.json();

        if (response.ok && result.result === 'SUCCESS') {
            alert('회원 탈퇴가 완료되었습니다.');
            localStorage.removeItem('accessToken');
            window.location.href = '/html/auth/login.html';
        } else {
            alert(result.message || '회원 탈퇴 실패');
        }
    } catch (error) {
        console.error('회원 탈퇴 중 오류:', error);
        alert('서버 오류: 회원 탈퇴에 실패했습니다.');
    }
}

// 페이지 로드 시 실행
window.onload = function() {
    createStars();
    setupModals();
    setupNicknameEdit();
    setupPasswordModals();

    // 페이지 로드 시 모든 모달 초기화
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        modal.style.display = 'none';
    });

    // 실제 구현 시 사용자 데이터 로드
    loadUserData();
};