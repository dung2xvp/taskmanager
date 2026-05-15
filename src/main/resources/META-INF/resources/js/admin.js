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
    
    if (res.status === 403) {
        showToast('Bạn không có quyền truy cập trang này!', 'error');
        setTimeout(() => window.location.href = '/dashboard.html', 2000);
        return null;
    }

    return res;
}

document.getElementById('btn-logout').addEventListener('click', () => {
    localStorage.clear();
    window.location.href = '/';
});

async function loadUsers() {
    const res = await apiFetch('/admin/users');
    if (!res) return;
    
    if (res.ok) {
        const users = await res.json();
        const tbody = document.getElementById('user-table-body');
        tbody.innerHTML = '';
        
        users.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${u.id}</td>
                <td>${escapeHtml(u.username)}</td>
                <td>${escapeHtml(u.fullName)}</td>
                <td>${escapeHtml(u.email)}</td>
                <td>${u.systemRole}</td>
                <td>
                    <span class="status-tag ${u.active ? 'status-active' : 'status-blocked'}">
                        ${u.active ? 'Hoạt động' : 'Đã khóa'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm ${u.active ? 'btn-ghost' : 'btn-danger'}" style="${u.active ? '' : 'background:var(--primary); color:white;'}" onclick="toggleUser(${u.id})">
                        ${u.active ? 'Khóa' : 'Mở khóa'}
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function loadStats() {
    const res = await apiFetch('/admin/stats');
    if (res && res.ok) {
        const data = await res.json();
        document.getElementById('stat-total-users').textContent = data.totalUsers;
        document.getElementById('stat-active-users').textContent = data.activeUsers;
        document.getElementById('stat-total-boards').textContent = data.totalBoards;
    }
}

async function loadBoards() {
    const res = await apiFetch('/admin/boards');
    if (!res) return;
    
    if (res.ok) {
        const boards = await res.json();
        const tbody = document.getElementById('board-table-body');
        tbody.innerHTML = '';
        
        boards.forEach(b => {
            const tr = document.createElement('tr');
            const dateStr = b.createdAt ? new Date(b.createdAt).toLocaleDateString('vi-VN') : 'N/A';
            tr.innerHTML = `
                <td>${b.id}</td>
                <td>${escapeHtml(b.name)}</td>
                <td>${escapeHtml(b.ownerName)}</td>
                <td>
                    <span class="status-tag ${b.visibility === 'PUBLIC' ? 'status-active' : 'status-blocked'}">
                        ${b.visibility}
                    </span>
                </td>
                <td>${dateStr}</td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="deleteBoard(${b.id})">Xóa</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function deleteBoard(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa bảng này không?')) return;
    
    const res = await apiFetch(`/admin/boards/${id}`, {
        method: 'DELETE'
    });
    
    if (res && res.ok) {
        showToast('Đã xóa bảng thành công!', 'success');
        loadBoards();
        loadStats();
    } else if (res) {
        const msg = await res.text();
        showToast(msg || 'Xóa thất bại', 'error');
    }
}

function switchTab(tab) {
    const usersBtn = document.getElementById('tab-users-btn');
    const boardsBtn = document.getElementById('tab-boards-btn');
    const usersPanel = document.getElementById('panel-users');
    const boardsPanel = document.getElementById('panel-boards');
    
    if (tab === 'users') {
        usersBtn.classList.add('active-tab');
        boardsBtn.classList.remove('active-tab');
        usersPanel.style.display = 'block';
        boardsPanel.style.display = 'none';
        loadUsers();
    } else {
        usersBtn.classList.remove('active-tab');
        boardsBtn.classList.add('active-tab');
        usersPanel.style.display = 'none';
        boardsPanel.style.display = 'block';
        loadBoards();
    }
}

async function toggleUser(id) {
    const res = await apiFetch(`/admin/users/${id}/toggle-active`, {
        method: 'PUT'
    });
    
    if (res && res.ok) {
        showToast('Cập nhật trạng thái thành công!', 'success');
        loadUsers();
        loadStats(); // Cập nhật lại số liệu
    } else if (res) {
        const msg = await res.text();
        showToast(msg || 'Thao tác thất bại', 'error');
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// Gán vào window để gọi được từ inline onclick
window.toggleUser = toggleUser;
window.switchTab = switchTab;
window.deleteBoard = deleteBoard;

// Khởi tạo
loadStats();
loadUsers();
