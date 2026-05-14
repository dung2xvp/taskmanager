/**
 * board.js — Trang Kanban Board
 *
 * API cần backend:
 *   GET  /boards/{id}/board-state   → BoardStateDto
 *   POST /boards/{id}/lists         → BoardListDto  (body: { name })
 *   POST /lists/{id}/cards          → CardDto       (body: { title })
 *   PUT  /cards/{id}/move           → void          (body: { listId, position })
 */

// =============================================
// Khởi tạo
// =============================================
const token    = localStorage.getItem('token');
const fullName = localStorage.getItem('fullName') || 'User';
const userId   = localStorage.getItem('userId');

if (!token) { window.location.href = '/login.html'; }

// Đọc boardId từ URL: /board.html?id=123
const boardId = new URLSearchParams(window.location.search).get('id');
if (!boardId) { window.location.href = '/dashboard.html'; }

// Cập nhật avatar navbar
document.getElementById('nav-avatar').textContent = fullName.charAt(0).toUpperCase();

// State toàn cục
let boardState = null; // BoardStateDto

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
    if (res.status === 401) {
        localStorage.clear();
        window.location.href = '/login.html';
        return null;
    }
    return res;
}

// =============================================
// Load Board State
// =============================================
async function loadBoardState() {
    const res = await apiFetch(`/boards/${boardId}/board-state`);
    if (!res) return;

    if (!res.ok) {
        showToast('Không thể tải bảng Kanban', 'error');
        return;
    }

    boardState = await res.json();
    renderBoard(boardState);
}

// =============================================
// Render toàn bộ Board
// =============================================
function renderBoard(state) {
    // Cập nhật tiêu đề
    const boardName = state.board?.name || 'Board';
    document.title = `${boardName} — TaskFlow`;
    document.getElementById('board-title-display').textContent = boardName;
    document.getElementById('board-name-heading').textContent  = boardName;

    const visMap = { PRIVATE: '🔒 Riêng tư', PUBLIC: '🌐 Công khai', WORKSPACE: '👥 Workspace' };
    document.getElementById('board-visibility-tag').textContent =
        visMap[state.board?.visibility] || '';

    // Members preview
    const preview = document.getElementById('board-members-preview');
    preview.innerHTML = '';
    (state.members || []).slice(0, 5).forEach(m => {
        const av = document.createElement('div');
        av.className = 'member-avatar-sm';
        av.title = m.fullName || m.username;
        av.textContent = (m.fullName || m.username || '?').charAt(0).toUpperCase();
        preview.appendChild(av);
    });

    // Render Kanban columns
    const board = document.getElementById('kanban-board');
    board.innerHTML = '';

    (state.lists || []).forEach(list => {
        board.appendChild(createListElement(list, state.members, state.labels));
    });

    // Nút thêm list
    const addBtn = document.getElementById('add-list-btn') || createAddListBtn();
    addBtn.style.display = 'block';
    board.appendChild(addBtn);
}

// =============================================
// Tạo phần tử List Column
// =============================================
function createListElement(list, members, labels) {
    const col = document.createElement('div');
    col.className = 'kanban-list';
    col.dataset.listId = list.id;

    col.innerHTML = `
        <div class="list-header">
            <span class="list-title">${escapeHtml(list.name)}</span>
            <span class="list-card-count">${(list.cards || []).length}</span>
            <button class="list-menu-btn" title="Tuỳ chọn" onclick="listMenu(${list.id}, event)">⋯</button>
        </div>
        <div class="list-cards" id="cards-${list.id}"
             ondragover="onDragOver(event, ${list.id})"
             ondragleave="onDragLeave(event)"
             ondrop="onDrop(event, ${list.id})">
        </div>
        <div class="add-card-btn-wrap" id="add-card-wrap-${list.id}">
            <button class="btn-add-card" onclick="showAddCard(${list.id})">
                <span>+</span> Thêm thẻ
            </button>
        </div>
    `;

    // Render cards
    const cardsContainer = col.querySelector(`#cards-${list.id}`);
    (list.cards || []).forEach(card => {
        cardsContainer.appendChild(createCardElement(card, members, labels));
    });

    return col;
}

// =============================================
// Tạo phần tử Card
// =============================================
function createCardElement(card, members, labels) {
    const el = document.createElement('div');
    el.className = 'kanban-card';
    el.dataset.cardId = card.id;
    el.draggable = true;

    // Cover color
    const coverHtml = card.coverColor
        ? `<div class="card-cover" style="background:${card.coverColor}"></div>`
        : '';

    // Labels
    const cardLabels = (card.labelIds || [])
        .map(id => (labels || []).find(l => l.id === id))
        .filter(Boolean);
    const labelsHtml = cardLabels.length > 0
        ? `<div class="card-labels">
             ${cardLabels.map(l => `<div class="card-label" style="background:${l.color}" title="${escapeHtml(l.name)}"></div>`).join('')}
           </div>`
        : '';

    // Members
    const cardMembers = (card.memberIds || [])
        .map(id => (members || []).find(m => m.userId === id))
        .filter(Boolean);
    const membersHtml = cardMembers.length > 0
        ? `<div class="card-members">
             ${cardMembers.map(m =>
                `<div class="card-member-avatar" title="${escapeHtml(m.fullName || m.username)}">
                   ${(m.fullName || m.username || '?').charAt(0).toUpperCase()}
                 </div>`
             ).join('')}
           </div>`
        : '';

    // Due date
    let dueHtml = '';
    let dueStyle = 'display: none;';
    let overClass = '';
    if (card.dueDate) {
        dueStyle = 'display: flex;';
        const due  = new Date(card.dueDate);
        const now  = new Date();
        const over = due < now;
        overClass = over ? 'overdue' : '';
        dueHtml = `${over ? '⚠️' : '📅'} ${due.toLocaleDateString('vi-VN')}`;
    }

    el.innerHTML = `
        ${coverHtml}
        ${labelsHtml}
        <div class="card-title">${escapeHtml(card.title)}</div>
        <div class="card-meta">
            <span class="card-due ${overClass}" style="${dueStyle}">${dueHtml}</span>
            ${membersHtml}
        </div>
    `;

    // Click → mở modal
    el.addEventListener('click', () => openCardModal(card, members, labels));

    // Drag events
    el.addEventListener('dragstart', e => onDragStart(e, card.id, card.listId));
    el.addEventListener('dragend',   () => el.classList.remove('dragging'));

    return el;
}

// =============================================
// Add List
// =============================================
function createAddListBtn() {
    const wrap = document.createElement('div');
    wrap.className = 'add-list-btn';
    wrap.id = 'add-list-btn';
    wrap.innerHTML = `
        <button class="btn-add-list" onclick="showAddListForm()">
            <span>+</span> Tạo cột danh sách công việc
        </button>
        <div class="add-list-form" id="add-list-form" style="display:none">
            <input id="new-list-name"
                   class="form-input"
                   type="text"
                   placeholder="VD: To Do, Đang làm, Hoàn thành..."
                   maxlength="100"
                   autocomplete="off"/>
            <div class="add-list-actions">
                <button class="btn btn-primary btn-sm" onclick="submitAddList()">Thêm</button>
                <button class="btn btn-ghost btn-sm" onclick="hideAddListForm()">Huỷ</button>
            </div>
        </div>
    `;
    return wrap;
}

function showAddListForm() {
    document.querySelector('.btn-add-list').style.display = 'none';
    const form = document.getElementById('add-list-form');
    form.style.display = 'flex';
    form.style.flexDirection = 'column';
    document.getElementById('new-list-name').focus();
}

function hideAddListForm() {
    document.querySelector('.btn-add-list').style.display = 'flex';
    document.getElementById('add-list-form').style.display = 'none';
    document.getElementById('new-list-name').value = '';
}

async function submitAddList() {
    const name = document.getElementById('new-list-name').value.trim();
    if (!name) return;

    const res = await apiFetch(`/boards/${boardId}/lists`, {
        method: 'POST',
        body: JSON.stringify({ name })
    });

    if (!res || !res.ok) {
        showToast('Tạo danh sách thất bại', 'error');
        return;
    }

    const newList = await res.json();

    if (!boardState) boardState = { lists: [], members: [], labels: [] };
    boardState.lists.push(newList);

    const board = document.getElementById('kanban-board');
    const addBtn = document.getElementById('add-list-btn');
    board.insertBefore(
        createListElement(newList, boardState.members, boardState.labels),
        addBtn
    );

    hideAddListForm();
    showToast(`Đã thêm danh sách "${name}"`, 'success');
}

// =============================================
// Add Card
// =============================================
function showAddCard(listId) {
    const wrap = document.getElementById(`add-card-wrap-${listId}`);
    wrap.innerHTML = `
        <div class="add-card-form">
            <input id="new-card-title-${listId}"
                   class="form-input"
                   type="text"
                   placeholder="Nhập tiêu đề thẻ..."
                   maxlength="500"
                   style="width: 100%; border: 1px solid var(--primary); padding: var(--space-2); margin-bottom: 8px; box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);"/>
            <textarea id="new-card-desc-${listId}"
                      class="add-card-textarea"
                      placeholder="Thêm mô tả chi tiết..."
                      rows="3"
                      maxlength="2000"
                      style="border: 1px solid var(--border); box-shadow: none;"></textarea>
            <div class="add-card-actions" style="margin-top: 8px;">
                <button class="btn btn-primary btn-sm" onclick="submitAddCard(${listId})">Thêm thẻ</button>
                <button class="btn btn-ghost btn-sm" onclick="cancelAddCard(${listId})">✕</button>
            </div>
        </div>
    `;
    document.getElementById(`new-card-title-${listId}`).focus();
}

function cancelAddCard(listId) {
    const wrap = document.getElementById(`add-card-wrap-${listId}`);
    wrap.innerHTML = `
        <button class="btn-add-card" onclick="showAddCard(${listId})">
            <span>+</span> Thêm thẻ
        </button>
    `;
}

async function submitAddCard(listId) {
    const titleInput = document.getElementById(`new-card-title-${listId}`);
    const descInput = document.getElementById(`new-card-desc-${listId}`);
    const title = titleInput?.value?.trim();
    const description = descInput?.value?.trim() || '';

    if (!title) {
        titleInput?.focus();
        return;
    }

    const res = await apiFetch(`/lists/${listId}/cards`, {
        method: 'POST',
        body: JSON.stringify({ title, description })
    });

    if (!res || !res.ok) {
        showToast('Tạo thẻ thất bại', 'error');
        return;
    }

    const newCard = await res.json();

    const list = boardState?.lists?.find(l => l.id == listId);
    if (list) list.cards = list.cards || [];
    list?.cards?.push(newCard);

    const container = document.getElementById(`cards-${listId}`);
    if (container) {
        container.appendChild(createCardElement(newCard, boardState?.members, boardState?.labels));
        // Cập nhật count
        const col = container.closest('.kanban-list');
        const count = col?.querySelector('.list-card-count');
        if (count && list) count.textContent = list.cards.length;
    }

    cancelAddCard(listId);
}

// =============================================
// Drag & Drop
// =============================================
let draggingCardId  = null;
let draggingListId  = null;

function onDragStart(e, cardId, listId) {
    draggingCardId = cardId;
    draggingListId = listId;
    e.dataTransfer.effectAllowed = 'move';
    setTimeout(() => e.target.classList.add('dragging'), 0);
}

function onDragOver(e, targetListId) {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    const col = document.querySelector(`[data-list-id="${targetListId}"]`);
    col?.classList.add('drag-over');
}

function onDragLeave(e) {
    const col = e.currentTarget.closest('.kanban-list');
    col?.classList.remove('drag-over');
}

async function onDrop(e, targetListId) {
    e.preventDefault();
    const col = document.querySelector(`[data-list-id="${targetListId}"]`);
    col?.classList.remove('drag-over');

    if (!draggingCardId || draggingListId === targetListId) return;

    // Gọi API di chuyển card
    const res = await apiFetch(`/lists/cards/${draggingCardId}/move`, {
        method: 'PUT',
        body: JSON.stringify({ listId: targetListId, position: 9999 })
    });

    if (!res || !res.ok) {
        showToast('Di chuyển thẻ thất bại', 'error');
        draggingCardId = null;
        draggingListId = null;
        return;
    }

    // Di chuyển card trong UI
    const cardEl = document.querySelector(`[data-card-id="${draggingCardId}"]`);
    const targetContainer = document.getElementById(`cards-${targetListId}`);
    if (cardEl && targetContainer) {
        // Cập nhật listId trên card
        cardEl.dataset.listId = targetListId;
        targetContainer.appendChild(cardEl);
    }

    // Cập nhật state
    if (boardState) {
        const srcList = boardState.lists.find(l => l.id == draggingListId);
        const dstList = boardState.lists.find(l => l.id == targetListId);
        if (srcList && dstList) {
            const cardIdx = srcList.cards.findIndex(c => c.id == draggingCardId);
            if (cardIdx >= 0) {
                const [moved] = srcList.cards.splice(cardIdx, 1);
                moved.listId = targetListId;
                dstList.cards.push(moved);
                
                // Cập nhật count UI
                const srcCol = document.querySelector(`[data-list-id="${draggingListId}"]`);
                if (srcCol) srcCol.querySelector('.list-card-count').textContent = srcList.cards.length;
                const dstCol = document.querySelector(`[data-list-id="${targetListId}"]`);
                if (dstCol) dstCol.querySelector('.list-card-count').textContent = dstList.cards.length;
            }
        }
    }

    draggingCardId = null;
    draggingListId = null;
}

// =============================================
// Card Modal
// =============================================
let currentOpenedCard = null;

function openCardModal(card, members, labels) {
    currentOpenedCard = card;
    const titleInput = document.getElementById('card-modal-title');
    const descInput = document.getElementById('card-modal-desc');
    
    titleInput.value = card.title;
    descInput.value = card.description || '';
    
    // Reset actions
    document.getElementById('card-desc-actions').style.display = 'none';

    // Members
    const membersEl = document.getElementById('card-modal-members');
    membersEl.innerHTML = (card.memberIds || [])
        .map(id => members?.find(m => m.userId === id))
        .filter(Boolean)
        .map(m => `<div class="member-avatar-sm" title="${escapeHtml(m.fullName || m.username)}">
                     ${(m.fullName || m.username || '?').charAt(0).toUpperCase()}
                   </div>`)
        .join('') || '<span style="color:var(--text-muted);font-size:12px">Chưa có</span>';

    // Labels
    const labelsEl = document.getElementById('card-modal-labels');
    labelsEl.innerHTML = (card.labelIds || [])
        .map(id => labels?.find(l => l.id === id))
        .filter(Boolean)
        .map(l => `<span class="card-label-chip" style="background:${l.color}">${escapeHtml(l.name)}</span>`)
        .join('') || '<span style="color:var(--text-muted);font-size:12px">Chưa có</span>';

    // Due date
    const dueEl = document.getElementById('card-modal-due');
    dueEl.value = card.dueDate ? card.dueDate.substring(0, 10) : '';

    document.getElementById('card-modal-backdrop').classList.add('open');
}

function closeCardModal() {
    document.getElementById('card-modal-backdrop').classList.remove('open');
}

document.getElementById('card-modal-close').addEventListener('click', closeCardModal);
document.getElementById('card-modal-backdrop').addEventListener('click', function (e) {
    if (e.target === this) closeCardModal();
});
document.addEventListener('keydown', e => { if (e.key === 'Escape') closeCardModal(); });

// -- Xử lý sửa Tiêu đề Card --
const titleInput = document.getElementById('card-modal-title');
titleInput.addEventListener('blur', async () => {
    if (!currentOpenedCard) return;
    const newTitle = titleInput.value.trim();
    if (!newTitle) {
        titleInput.value = currentOpenedCard.title; // revert
        return;
    }
    if (newTitle !== currentOpenedCard.title) {
        currentOpenedCard.title = newTitle;
        await updateCardDetails(currentOpenedCard);
        
        // Update UI in list
        const cardEl = document.querySelector(`[data-card-id="${currentOpenedCard.id}"] .card-title`);
        if (cardEl) cardEl.textContent = newTitle;
    }
});
titleInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        titleInput.blur();
    }
});

// -- Xử lý sửa Mô tả Card --
const descInput = document.getElementById('card-modal-desc');
const descActions = document.getElementById('card-desc-actions');
const btnSaveDesc = document.getElementById('btn-save-desc');
const btnCancelDesc = document.getElementById('btn-cancel-desc');

descInput.addEventListener('focus', () => {
    descActions.style.display = 'flex';
});

btnCancelDesc.addEventListener('click', () => {
    if (!currentOpenedCard) return;
    descInput.value = currentOpenedCard.description || '';
    descActions.style.display = 'none';
});

btnSaveDesc.addEventListener('click', async () => {
    if (!currentOpenedCard) return;
    const newDesc = descInput.value.trim();
    if (newDesc !== (currentOpenedCard.description || '')) {
        currentOpenedCard.description = newDesc;
        await updateCardDetails(currentOpenedCard);
    }
    descActions.style.display = 'none';
});

// -- Xử lý sửa Ngày đến hạn --
const dueInput = document.getElementById('card-modal-due');
dueInput.addEventListener('change', async () => {
    if (!currentOpenedCard) return;
    const newDue = dueInput.value || null; // YYYY-MM-DD
    
    // Check if changed
    const currentDue = currentOpenedCard.dueDate ? currentOpenedCard.dueDate.substring(0, 10) : null;
    if (newDue !== currentDue) {
        currentOpenedCard.dueDate = newDue;
        await updateCardDetails(currentOpenedCard);
        
        // Update UI in list
        const cardDueEl = document.querySelector(`[data-card-id="${currentOpenedCard.id}"] .card-due`);
        if (cardDueEl) {
            if (newDue) {
                cardDueEl.innerHTML = `⏱ ${new Date(newDue).toLocaleDateString('vi-VN')}`;
                cardDueEl.style.display = 'flex';
            } else {
                cardDueEl.style.display = 'none';
                cardDueEl.innerHTML = '';
            }
        }
    }
});

async function updateCardDetails(card) {
    const res = await apiFetch(`/lists/cards/${card.id}`, {
        method: 'PUT',
        body: JSON.stringify({
            title: card.title,
            description: card.description,
            dueDate: card.dueDate || ""
        })
    });
    if (res && res.ok) {
        showToast('Đã lưu thay đổi', 'success');
    } else {
        showToast('Lưu thay đổi thất bại', 'error');
    }
}

// =============================================
// List Menu
// =============================================
let activeDropdown = null;

function closeDropdown() {
    if (activeDropdown) {
        activeDropdown.remove();
        activeDropdown = null;
    }
}

document.addEventListener('click', closeDropdown);

function listMenu(listId, event) {
    event.stopPropagation();
    closeDropdown();

    const list = boardState?.lists?.find(l => l.id == listId);
    if (!list) return;

    const btn = event.currentTarget;
    const rect = btn.getBoundingClientRect();

    const menu = document.createElement('div');
    menu.className = 'dropdown-menu';
    menu.style.top = `${rect.bottom + window.scrollY + 4}px`;
    
    // Đảm bảo menu không bị tràn màn hình bên phải
    let left = rect.left + window.scrollX - 100;
    if (left < 0) left = rect.left + window.scrollX;
    menu.style.left = `${left}px`;

    menu.innerHTML = `
        <button class="dropdown-item rename-btn">✏️ Đổi tên cột</button>
        <button class="dropdown-item danger delete-btn">🗑️ Xoá cột</button>
    `;

    menu.querySelector('.rename-btn').addEventListener('click', (e) => {
        e.stopPropagation();
        closeDropdown();
        renameList(listId);
    });

    menu.querySelector('.delete-btn').addEventListener('click', (e) => {
        e.stopPropagation();
        closeDropdown();
        deleteList(listId);
    });

    document.body.appendChild(menu);
    activeDropdown = menu;
}

// =============================================
// Custom Dialog (thay thế prompt/confirm)
// =============================================
function showCustomPrompt(title, defaultValue, callback) {
    const backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop open';
    backdrop.style.zIndex = '9999';
    backdrop.style.display = 'flex';
    backdrop.style.alignItems = 'center';
    backdrop.style.justifyContent = 'center';
    
    backdrop.innerHTML = `
        <div class="card-modal" style="max-width: 400px; padding: var(--space-5); transform: none;">
            <div style="font-size: 16px; font-weight: 600; margin-bottom: 15px; color: var(--text-primary);">${escapeHtml(title)}</div>
            <input type="text" id="custom-prompt-input" class="form-input" value="${escapeHtml(defaultValue)}" style="width: 100%; margin-bottom: 20px;">
            <div style="display: flex; justify-content: flex-end; gap: 10px;">
                <button class="btn btn-ghost" id="custom-prompt-cancel">Huỷ</button>
                <button class="btn btn-primary" id="custom-prompt-ok">Lưu</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(backdrop);
    const input = document.getElementById('custom-prompt-input');
    input.focus();
    input.select();
    
    const close = (value) => {
        backdrop.remove();
        callback(value);
    };
    
    document.getElementById('custom-prompt-cancel').onclick = () => close(null);
    document.getElementById('custom-prompt-ok').onclick = () => close(input.value);
    input.onkeydown = (e) => {
        if (e.key === 'Enter') close(input.value);
        if (e.key === 'Escape') close(null);
    };
}

function showCustomConfirm(title, callback) {
    const backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop open';
    backdrop.style.zIndex = '9999';
    backdrop.style.display = 'flex';
    backdrop.style.alignItems = 'center';
    backdrop.style.justifyContent = 'center';
    
    backdrop.innerHTML = `
        <div class="card-modal" style="max-width: 400px; padding: var(--space-5); transform: none;">
            <div style="font-size: 16px; font-weight: 600; margin-bottom: 20px; line-height: 1.5; color: var(--text-primary);">${escapeHtml(title)}</div>
            <div style="display: flex; justify-content: flex-end; gap: 10px;">
                <button class="btn btn-ghost" id="custom-confirm-cancel">Huỷ</button>
                <button class="btn" style="background: var(--danger-light); color: white; border: none; border-radius: var(--radius-md); padding: 8px 16px; font-weight: 500; cursor: pointer;" id="custom-confirm-ok">Xoá</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(backdrop);
    
    const close = (value) => {
        backdrop.remove();
        callback(value);
    };
    
    document.getElementById('custom-confirm-cancel').onclick = () => close(false);
    document.getElementById('custom-confirm-ok').onclick = () => close(true);
}

async function renameList(listId) {
    const list = boardState?.lists?.find(l => l.id == listId);
    if (!list) return;

    showCustomPrompt('Nhập tên mới cho cột:', list.name, async (newName) => {
        if (newName && newName.trim() !== '' && newName !== list.name) {
            const res = await apiFetch(`/boards/${boardId}/lists/${listId}`, {
                method: 'PUT',
                body: JSON.stringify({ name: newName.trim() })
            });
            if (res && res.ok) {
                list.name = newName.trim();
                const titleEl = document.querySelector(`[data-list-id="${listId}"] .list-title`);
                if (titleEl) titleEl.textContent = list.name;
                showToast('Đổi tên cột thành công', 'success');
            } else {
                showToast('Đổi tên cột thất bại', 'error');
            }
        }
    });
}

async function deleteList(listId) {
    const list = boardState?.lists?.find(l => l.id == listId);
    if (!list) return;

    showCustomConfirm(`Bạn có chắc chắn muốn xoá cột "${list.name}" và tất cả thẻ bên trong?`, async (confirmed) => {
        if (confirmed) {
            const res = await apiFetch(`/boards/${boardId}/lists/${listId}`, {
                method: 'DELETE'
            });
            if (res && res.ok) {
                boardState.lists = boardState.lists.filter(l => l.id != listId);
                const col = document.querySelector(`[data-list-id="${listId}"]`);
                if (col) col.remove();
                showToast('Đã xoá cột', 'success');
            } else {
                showToast('Xoá cột thất bại', 'error');
            }
        }
    });
}

// =============================================
// Đăng xuất
// =============================================
document.getElementById('btn-logout').addEventListener('click', () => {
    localStorage.clear();
    window.location.href = '/';
});

// =============================================
// Tiện ích
// =============================================
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// =============================================
// Khởi động
// =============================================
loadBoardState();
