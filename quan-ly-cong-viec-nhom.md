# 📋 Hệ Thống Quản Lý Công Việc Nhóm Thời Gian Thực

> **Công nghệ:** Quarkus · WebSocket · HTML/CSS/JS · PostgreSQL

---

## 📌 1. Tổng Quan Dự Án

| Thành phần | Chi tiết |
|---|---|
| **Tên đề tài** | Hệ thống quản lý công việc nhóm thời gian thực |
| **Backend** | Quarkus (REST + WebSocket + Security JWT) |
| **Database** | PostgreSQL |
| **Frontend** | HTML / CSS / JavaScript |

---

## 🗓️ 2. Timeline Chi Tiết (5–6 Tuần)

---

### 🚀 Giai Đoạn 1: Phân Tích & Thiết Kế *(3–5 ngày)*

**Công việc:**
- Xác định chức năng:
  - Đăng ký / đăng nhập (JWT)
  - Quản lý user
  - Quản lý project
  - Quản lý task
  - Realtime update
- Thiết kế database:
  - User
  - Task
  - Project

**Kết quả:**
- ✅ ERD
- ✅ Use case diagram
- ✅ Luồng hệ thống

---

### 🧱 Giai Đoạn 2: Setup Hệ Thống *(2–3 ngày)*

**Công việc:**
- Tạo project Quarkus
- Setup IntelliJ + JDK 17
- Cài PostgreSQL
- Cấu hình `application.properties`

**Kết quả:**
- ✅ Project chạy (`mvn quarkus:dev`)
- ✅ Kết nối DB OK

---

### 🗄️ Giai Đoạn 3: Database + Entity *(4–6 ngày)*

**Công việc:**

1. Tạo bảng (PostgreSQL):
   - `user`
   - `task`
   - `project`

2. Tạo Entity:
   - `User.java`
   - `Task.java`
   - `Project.java`

3. Mapping quan hệ:
   - Task → User
   - Task → Project

**🆕 Nâng cao:** Tạo migration:
```sql
V1__init.sql
```

**Kết quả:**
- ✅ DB chuẩn
- ✅ Hibernate auto sync

---

### ⚙️ Giai Đoạn 4: Backend (REST API) *(6–8 ngày)*

**Công việc:**

📌 Controller:
- `/api/auth`
- `/api/tasks`
- `/api/projects`

📌 Service:
- Business logic

📌 Repository:
- Panache

**Kết quả:**
- ✅ CRUD đầy đủ: `create` / `update` / `delete` / `get`

---

### 🔐 Giai Đoạn 5: Security (JWT) *(4–6 ngày)*

> 👉 **Đây là điểm nâng cao quan trọng 🔥**

**Công việc:**

1. **Auth** — Login API:
```
POST /api/auth/login
```

2. **JWT:**
   - Tạo token
   - Verify token

3. **Password:**
   - Hash bằng BCrypt

> ⚠️ Giai đoạn dev: Cho phép `permit-all`

**Kết quả:**
- ✅ Login hoạt động
- ✅ API có bảo mật

---

### ⚡ Giai Đoạn 6: WebSocket (Realtime) *(3–5 ngày)*

**Công việc:**
- Tạo endpoint `/ws/tasks`
- Broadcast khi:
  - Tạo task
  - Update task
  - Delete task

**Kết quả:**
- ✅ Realtime hoạt động

---

### 🎨 Giai Đoạn 7: Frontend (HTML/CSS/JS) *(7–10 ngày)*

**Công việc:**

1. **UI:**
   - `login.html`
   - `dashboard.html`

2. **JS:**
   - Gọi API (fetch)
   - Lưu token
   - Connect WebSocket

**Ví dụ:**
```javascript
fetch("/api/tasks", {
  headers: {
    Authorization: "Bearer " + token
  }
});
```

**Kết quả:**
- ✅ UI chạy được
- ✅ Realtime update

---

### 🧪 Giai Đoạn 8: Test & Hoàn Thiện *(3–5 ngày)*

**Công việc:**
- Test:
  - API
  - JWT
  - WebSocket
- Fix bug

---

### 📄 Giai Đoạn 9: Báo Cáo & Demo *(3–5 ngày)*

**Nội dung:**
- Giới thiệu hệ thống
- Công nghệ sử dụng
- Kiến trúc hệ thống
- Demo trực tiếp

---

## 🔥 3. Phân Chia Theo Cấu Trúc Project

```
src/
├── backend/
│   ├── entity/          # DB Entities
│   ├── repository/      # Panache queries
│   ├── service/         # Business logic
│   └── controller/      # REST API
│
├── security/
│   ├── jwt/             # JWT tạo & verify
│   ├── filter/          # Security filter
│   └── AuthController   # Auth endpoints
│
├── websocket/
│   └── TaskSocket.java  # Realtime WebSocket
│
└── META-INF/
    └── resources/       # Frontend (HTML/CSS/JS)
```

---

## ⚙️ 4. Luồng Hệ Thống Hoàn Chỉnh

```
User login → nhận JWT
         ↓
Gọi API (Bearer token)
         ↓
Security filter check
         ↓
Controller → Service → DB
         ↓
WebSocket → gửi realtime
         ↓
Frontend update UI
```

---

## 📊 5. Tóm Tắt Timeline

| Giai đoạn | Nội dung | Thời gian |
|---|---|---|
| 1 | Phân tích & Thiết kế | 3–5 ngày |
| 2 | Setup hệ thống | 2–3 ngày |
| 3 | Database + Entity | 4–6 ngày |
| 4 | Backend REST API | 6–8 ngày |
| 5 | Security JWT | 4–6 ngày |
| 6 | WebSocket Realtime | 3–5 ngày |
| 7 | Frontend HTML/CSS/JS | 7–10 ngày |
| 8 | Test & Hoàn thiện | 3–5 ngày |
| 9 | Báo cáo & Demo | 3–5 ngày |
| **Tổng** | | **~5–6 tuần** |
