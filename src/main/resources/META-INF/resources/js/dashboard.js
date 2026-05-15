/**
 * dashboard.js — Trang danh sách Board
 *
 * API cần backend implement:
 *   GET  /boards/my   → List<BoardSummaryDto>
 *   POST /boards      → BoardSummaryDto  (body: { name, description })
 *
 * BoardSummaryDto: { id, name, description, ownerId, visibility }
 */

// =============================================
// Khởi tạo
// =============================================
const token    = localStorage.getItem('token');
const fullName = localStorage.getItem('fullName') || 'User';
const userId   = localStorage.getItem('userId');

// Redirect về login nếu chưa đăng nhập
if (!token) {
    window.location.href = '/login.html';
}

// Hiện tên user lên navbar
document.getElementById('nav-username').textContent = fullName;
document.getElementById('nav-avatar').textContent   = fullName.charAt(0).toUpperCase();

// =============================================
// Toast
// =============================================
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

// =============================================
// API helper
// =============================================
async function apiFetch(path, options = {}) {
    const res = await fetch(path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token,
            ...(options.headers || {})
        }
    });

    // Token hết hạn → về login
    if (res.status === 401) {
        localStorage.clear();
        window.location.href = '/login.html';
        return null;
    }

    return res;
}

// =============================================
// Render Board Cards
// =============================================
function visibilityLabel(v) {
    return { PRIVATE: '🔒 Riêng tư', PUBLIC: '🌐 Công khai', WORKSPACE: '👥 Workspace' }[v] || v;
}

function renderBoardCard(board) {
    const isOwner = String(board.ownerId) === String(userId);
    const card = document.createElement('a');
    card.className = 'board-card';
    card.href = `/board.html?id=${board.id}`;
    card.innerHTML = `
        <div class="board-card-name">${escapeHtml(board.name)}</div>
        <div class="board-card-desc ${board.description ? '' : 'empty'}">
            ${board.description ? escapeHtml(board.description) : 'Chưa có mô tả'}
        </div>
        <div class="board-card-footer">
            <span class="board-visibility-badge ${board.visibility}">
                ${visibilityLabel(board.visibility)}
            </span>
            <span class="board-open-btn">Mở bảng →</span>
        </div>
    `;
    return card;
}

function renderSkeleton() {
    return `
        <div class="board-card-skeleton">
            <div class="skeleton skeleton-title"></div>
            <div class="skeleton skeleton-desc"></div>
            <div class="skeleton skeleton-desc2"></div>
            <div class="skeleton skeleton-foot"></div>
        </div>`;
}

function renderEmptyState() {
    const empty = document.createElement('div');
    empty.className = 'empty-state';
    empty.innerHTML = `
        <div class="empty-state-icon">🗂️</div>
        <div class="empty-state-title">Chưa có bảng nào</div>
        <p class="empty-state-desc">Tạo Workspace đầu tiên của bạn để bắt đầu quản lý công việc theo mô hình Kanban.</p>
        <button class="btn btn-primary" onclick="openModal()">+ Tạo Workspace đầu tiên</button>
    `;
    return empty;
}

function escapeHtml(str) {
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// =============================================
// Load danh sách Board
// =============================================
async function loadBoards() {
    const grid = document.getElementById('board-grid');

    // Hiện skeleton
    grid.innerHTML = Array(4).fill(renderSkeleton()).join('');

    const res = await apiFetch('/boards/my');
    if (!res) return;

    if (!res.ok) {
        showToast('Không thể tải danh sách bảng', 'error');
        grid.innerHTML = '';
        return;
    }

    const boards = await res.json();
    grid.innerHTML = '';

    if (boards.length === 0) {
        grid.appendChild(renderEmptyState());
    } else {
        boards.forEach(b => grid.appendChild(renderBoardCard(b)));
    }

    // Thêm nút tạo board mới ở cuối grid (nếu có board rồi)
    if (boards.length > 0) {
        const newCard = document.createElement('div');
        newCard.className = 'board-card-new';
        newCard.onclick = openModal;
        newCard.innerHTML = `
            <div class="board-card-new-icon">+</div>
            <div class="board-card-new-label">Tạo bảng mới</div>
        `;
        grid.appendChild(newCard);
    }
}

// =============================================
// Modal tạo Board
// =============================================
function openModal() {
    document.getElementById('modal-backdrop').classList.add('open');
    document.getElementById('board-name').focus();
}

function closeModal() {
    document.getElementById('modal-backdrop').classList.remove('open');
    document.getElementById('create-board-form').reset();
}

// Đóng modal khi click ra ngoài
document.getElementById('modal-backdrop').addEventListener('click', function (e) {
    if (e.target === this) closeModal();
});

// Đóng modal bằng Escape
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeModal();
});

// Nút mở modal trên header
document.getElementById('btn-create-board').addEventListener('click', openModal);
document.getElementById('modal-close-btn').addEventListener('click', closeModal);

// =============================================
// UC05 — Tạo Board mới
// =============================================
document.getElementById('create-board-form').addEventListener('submit', async function (e) {
    e.preventDefault();
    const btn  = document.getElementById('create-board-btn');
    const name = document.getElementById('board-name').value.trim();
    const desc = document.getElementById('board-desc').value.trim();

    if (!name) {
        showToast('Vui lòng nhập tên bảng', 'error');
        return;
    }

    btn.disabled = true;
    btn.classList.add('btn-loading');

    const res = await apiFetch('/boards', {
        method: 'POST',
        body: JSON.stringify({ name, description: desc })
    });

    btn.disabled = false;
    btn.classList.remove('btn-loading');

    if (!res || !res.ok) {
        showToast('Tạo bảng thất bại', 'error');
        return;
    }

    showToast('Đã tạo bảng thành công! 🎉', 'success');
    closeModal();
    loadBoards(); // Reload lại danh sách
});

// =============================================
// Đăng xuất
// =============================================
document.getElementById('btn-logout').addEventListener('click', function () {
    localStorage.clear();
    window.location.href = '/';
});

// =============================================
// Khởi động
// =============================================
loadBoards();
