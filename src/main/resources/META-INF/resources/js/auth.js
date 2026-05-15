/**
 * auth.js — Xử lý đăng nhập và đăng ký
 * Gọi API: POST /auth/login, POST /auth/register
 */

const API_BASE = '';

// =============================================
// Toast Notification
// =============================================
function showToast(message, type = 'info', duration = 3500) {
    const container = document.getElementById('toast-container');
    const icons = { success: '✅', error: '❌', info: 'ℹ️' };

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${icons[type]}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        toast.addEventListener('animationend', () => toast.remove());
    }, duration);
}

// =============================================
// Tab Switch: Login <-> Register
// =============================================
const tabLogin    = document.getElementById('tab-login');
const tabRegister = document.getElementById('tab-register');
const formLogin   = document.getElementById('form-login');
const formRegister = document.getElementById('form-register');

tabLogin.addEventListener('click', () => {
    tabLogin.classList.add('active');
    tabRegister.classList.remove('active');
    formLogin.style.display = 'block';
    formRegister.style.display = 'none';
});

tabRegister.addEventListener('click', () => {
    tabRegister.classList.add('active');
    tabLogin.classList.remove('active');
    formRegister.style.display = 'block';
    formLogin.style.display = 'none';
});

// =============================================
// Utilities
// =============================================
function setLoading(btn, isLoading) {
    btn.disabled = isLoading;
    btn.classList.toggle('btn-loading', isLoading);
}

function saveSession(data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('username', data.username);
    localStorage.setItem('fullName', data.fullName);
    localStorage.setItem('systemRole', data.systemRole);
}

// =============================================
// Redirect nếu đã đăng nhập
// =============================================
(function checkAlreadyLoggedIn() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('systemRole');
    if (token) {
        if (role === 'ADMIN') {
            window.location.href = '/admin.html';
        } else {
            window.location.href = '/dashboard.html';
        }
    }
})();

// =============================================
// UC02 — Đăng nhập
// =============================================
document.getElementById('login-form').addEventListener('submit', async function (e) {
    e.preventDefault();
    const btn = document.getElementById('login-btn');
    setLoading(btn, true);

    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await res.json();

        if (!res.ok) {
            throw new Error(data.message || data.error || 'Sai tên đăng nhập hoặc mật khẩu');
        }

        saveSession(data);
        showToast(`Chào mừng trở lại, ${data.fullName}! 👋`, 'success');
        setTimeout(() => { 
            if (data.systemRole === 'ADMIN') {
                window.location.href = '/admin.html';
            } else {
                window.location.href = '/dashboard.html';
            }
        }, 800);

    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        setLoading(btn, false);
    }
});

// =============================================
// UC01 — Đăng ký
// =============================================
document.getElementById('register-form').addEventListener('submit', async function (e) {
    e.preventDefault();
    const btn = document.getElementById('register-btn');

    const fullName = document.getElementById('reg-fullname').value.trim();
    const username = document.getElementById('reg-username').value.trim();
    const email    = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const confirm  = document.getElementById('reg-confirm').value;

    if (password !== confirm) {
        showToast('Mật khẩu xác nhận không khớp', 'error');
        return;
    }

    if (password.length < 6) {
        showToast('Mật khẩu phải có ít nhất 6 ký tự', 'error');
        return;
    }

    setLoading(btn, true);

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName })
        });

        const data = await res.json();

        if (!res.ok) {
            throw new Error(data.message || data.error || 'Đăng ký thất bại');
        }

        saveSession(data);
        showToast(`Tài khoản đã được tạo thành công! 🎉`, 'success');
        setTimeout(() => { window.location.href = '/dashboard.html'; }, 800);

    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        setLoading(btn, false);
    }
});
