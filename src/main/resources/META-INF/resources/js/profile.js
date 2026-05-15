const token = localStorage.getItem('token');
if (!token) { window.location.href = '/login.html'; }

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const icons = { success: '✅', error: '❌', info: 'ℹ️' };
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${icons[type]}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('fade-out');
        toast.addEventListener('animationend', () => toast.remove());
    }, 3500);
}

async function apiFetch(path, options = {}) {
    const res = await fetch(path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token,
            ...(options.headers || {})
        }
    });

    if (res.status === 401) {
        localStorage.clear();
        window.location.href = '/login.html';
        return null;
    }

    return res;
}

document.getElementById('btn-logout').addEventListener('click', () => {
    localStorage.clear();
    window.location.href = '/';
});

async function loadProfile() {
    const res = await apiFetch('/users/me');
    if (res.ok) {
        const data = await res.json();
        document.getElementById('profile-avatar').textContent = data.fullName.charAt(0).toUpperCase();
        document.getElementById('profile-name-heading').textContent = data.fullName;
        document.getElementById('input-username').value = data.username;
        document.getElementById('input-email').value = data.email;
        document.getElementById('input-fullname').value = data.fullName;
        
        // Cập nhật lại cache
        localStorage.setItem('fullName', data.fullName);
    } else {
        showToast('Không thể tải thông tin hồ sơ', 'error');
    }
}

document.getElementById('profile-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('btn-save');
    btn.disabled = true;
    btn.textContent = 'Đang lưu...';
    
    const fullName = document.getElementById('input-fullname').value.trim();
    
    const res = await apiFetch('/users/me', {
        method: 'PUT',
        body: JSON.stringify({ fullName })
    });
    
    btn.disabled = false;
    btn.textContent = 'Lưu thay đổi';
    
    if (res.ok) {
        const data = await res.json();
        showToast('Đã cập nhật hồ sơ thành công!', 'success');
        localStorage.setItem('fullName', data.fullName);
        document.getElementById('profile-avatar').textContent = data.fullName.charAt(0).toUpperCase();
        document.getElementById('profile-name-heading').textContent = data.fullName;
    } else {
        showToast('Cập nhật thất bại', 'error');
    }
});

loadProfile();
