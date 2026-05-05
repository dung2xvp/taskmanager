# Gợi ý xây dựng hệ thống quản lý công việc nhóm thời gian thực (Quarkus + WebSocket + HTML/CSS/JS)

## 1) Mục tiêu & phạm vi

Hệ thống quản lý công việc nhóm thời gian thực cho phép người dùng:

- Tạo / sửa / xoá công việc (Task)
- Theo dõi tiến độ theo Kanban: **Todo / Doing / Done**
- Kéo thả để chuyển cột và sắp xếp trực quan
- Đồng bộ thời gian thực giữa nhiều người dùng qua **WebSocket**
- Backend **Quarkus** xử lý bất đồng bộ để đảm bảo hiệu năng khi nhiều người truy cập đồng thời
- Frontend thuần **HTML/CSS/JavaScript** (đơn giản nhưng tương tác tốt)

> Tài liệu này mang tính **định hướng kiến trúc** (không cung cấp code).

---

## 2) Khởi tạo project Quarkus (backend)

Bạn có thể khởi tạo bằng một trong các cách sau:

- Dùng **Quarkus CLI**
- Dùng trang **code.quarkus.io** để chọn extensions rồi generate project

Khuyến nghị chọn:

- **Java**: 17+
- **Build tool**: Maven (phổ biến) hoặc Gradle
- **Packaging**: `jar` (dễ chạy dev); về sau có thể tối ưu native nếu cần

---

## 3) Dependency/Extensions nên thêm (Quarkus)

### 3.1. Tối thiểu cho bài toán

- **REST**: `quarkus-rest`
- **JSON**: `quarkus-rest-jackson`
- **WebSocket**: `quarkus-websockets`
- **ORM**: `quarkus-hibernate-orm-panache`
- **Database driver**:
  - Khuyến nghị: `quarkus-jdbc-postgresql`
  - Hoặc: `quarkus-jdbc-mysql`
- **Validation**: `quarkus-hibernate-validator`

### 3.2. Rất nên có (vận hành & tài liệu)

- **OpenAPI/Swagger**: `quarkus-smallrye-openapi`
- **Health check**: `quarkus-smallrye-health`

### 3.3. Authentication/Authorization (nếu có đăng nhập)

Chọn 1 trong 2 hướng:

- **OIDC**: `quarkus-oidc` (tích hợp Keycloak/SSO)
- **JWT**: `quarkus-smallrye-jwt` (JWT tự quản)

### 3.4. Nâng cao (khi cần scale nhiều instance)

- **Messaging**: `quarkus-smallrye-reactive-messaging` (Kafka/Redis PubSub) để broadcast realtime giữa nhiều node
- **Observability** (tuỳ chọn): Micrometer / OpenTelemetry để đo hiệu năng và theo dõi

---

## 4) Entity cần có (gợi ý mô hình dữ liệu)

### 4.1. Cốt lõi nghiệp vụ

- **User**
  - Thuộc tính gợi ý: `id`, `email/username`, `passwordHash` (nếu self-host), `systemRole`, `createdAt`, `updatedAt`
- **Project** (hoặc Workspace/Team)
  - `id`, `name`, `description`, `ownerId`, `createdAt`, `updatedAt`
- **ProjectMember**
  - `projectId`, `userId`, `roleInProject`, `joinedAt`
- **Task**
  - `id`, `projectId`
  - `title`, `description`
  - `status` (**TODO/DOING/DONE**)
  - `position/order` (phục vụ sắp xếp trong cột và kéo thả)
  - `priority`, `dueDate`
  - `assigneeId`, `createdBy`, `updatedBy`
  - `createdAt`, `updatedAt`
  - `version` (optimistic locking, rất hữu ích khi nhiều người update đồng thời)
- **Comment** (tuỳ chọn, nhưng hay có)
  - `id`, `taskId`, `authorId`, `content`, `createdAt`

### 4.2. Enum/giá trị chuẩn hoá

- **TaskStatus**: `TODO`, `DOING`, `DONE`
- **TaskPriority**: `LOW`, `MEDIUM`, `HIGH` (tuỳ bạn)
- **ProjectRole**: `ADMIN`, `MEMBER`, `VIEWER`
- **SystemRole** (toàn hệ thống): `ADMIN`, `USER` (tuỳ bạn)

---

## 5) Cấu trúc project (KHÔNG theo module nghiệp vụ)

Bạn đã chọn hướng **không chia theo module nghiệp vụ**, vì vậy nên tổ chức theo **tầng kỹ thuật**:

```text
src/main/java/org/myapp/taskmanager/
├─ config/                 (cấu hình: CORS, Jackson, Security, OpenAPI...)
├─ controller/             (REST API)
├─ websocket/              (WebSocket endpoint + quản lý session/room + message/event)
├─ service/                (business logic)
├─ repository/             (DB access)
├─ entity/                 (JPA entities + enums)
├─ dto/                    (request/response models)
│  ├─ request/
│  └─ response/
├─ mapper/                 (entity <-> dto)
├─ exception/              (custom exception + global handler)
└─ util/                   (helper chung)
```

### 5.1. Gợi ý class nằm ở đâu

- `controller/`: `AuthController`, `UserController`, `ProjectController`, `TaskController`, `CommentController`
- `service/`: `AuthService`, `UserService`, `ProjectService`, `TaskService`, `CommentService`
- `repository/`: `UserRepository`, `ProjectRepository`, `ProjectMemberRepository`, `TaskRepository`, `CommentRepository`
- `entity/`: `User`, `Project`, `ProjectMember`, `Task`, `Comment` + enums (`TaskStatus`, `TaskPriority`, `ProjectRole`, `SystemRole`)
- `dto/request/`: `TaskCreateRequest`, `TaskUpdateRequest`, `TaskMoveRequest`, `ProjectCreateRequest`, `LoginRequest`, ...
- `dto/response/`: `TaskResponse`, `ProjectResponse`, `BoardResponse`, `UserResponse`, ...
- `websocket/`: endpoint + session manager + event/payload
- `exception/`: `NotFoundException`, `ForbiddenException`, `BadRequestException` + global mapper/handler
- `config/`: cấu hình CORS, JSON, security, OpenAPI...
- `mapper/`: mapping `Entity <-> DTO` (manual hoặc dùng MapStruct nếu muốn)

---

## 6) Realtime với WebSocket (định hướng thiết kế)

### 6.1. “Room/Channel” theo Project

- Mỗi project tương ứng một “phòng” realtime: ví dụ `project:{projectId}`
- Client mở board của project nào thì subscribe/join room của project đó
- Server broadcast event cho room (tất cả thành viên đang online trong project)

### 6.2. Chuẩn hoá event realtime

Nên chuẩn hoá các loại event tối thiểu:

- `TASK_CREATED`
- `TASK_UPDATED`
- `TASK_DELETED`
- `TASK_MOVED` (kéo thả giữa các cột)
- `TASK_REORDERED` (đổi thứ tự trong cùng cột)

Payload nên có:

- `eventId`, `type`, `projectId`, `taskId`
- `actorUserId`
- `timestamp`
- `data` (thông tin thay đổi: status, position, fields changed...)

### 6.3. Nguyên tắc đồng bộ đúng và ổn định

- **REST**: dùng cho CRUD + validate quyền + ghi DB
- **WebSocket**: dùng để **broadcast** thay đổi cho các client khác
- Thực tế hay dùng flow:
  - REST nhận yêu cầu → ghi DB (transaction) → phát “domain event” → WebSocket broadcast
- Dùng **optimistic locking** (`version`) để giảm lỗi update đè khi nhiều người thao tác cùng lúc

### 6.4. Sắp xếp khi kéo thả (position/order)

Để kéo thả mượt, cần thuộc tính `position`:

- Cách đơn giản: dùng số thưa (1000, 2000, 3000...) và chèn giữa
- Khi dày quá thì reindex cột (hiếm khi cần nếu chọn chiến lược tốt)

---

## 7) Frontend (HTML/CSS/JS) – cấu trúc gợi ý

Nếu muốn triển khai nhanh và đơn giản, có thể đặt frontend tĩnh ngay trong Quarkus:

`src/main/resources/META-INF/resources/`

```text
src/main/resources/META-INF/resources/
├─ index.html
├─ pages/
│  ├─ login.html
│  ├─ dashboard.html
│  └─ board.html
├─ css/
│  ├─ base.css
│  ├─ layout.css
│  ├─ board.css
│  └─ modal.css
├─ js/
│  ├─ api/                 (gọi REST)
│  │  ├─ auth-api.js
│  │  ├─ project-api.js
│  │  └─ task-api.js
│  ├─ websocket/           (kết nối WS, handle event)
│  │  └─ board-socket.js
│  ├─ board/               (render Kanban + drag & drop + modal)
│  │  ├─ board-render.js
│  │  ├─ drag-drop.js
│  │  └─ task-modal.js
│  ├─ common/
│  │  ├─ utils.js
│  │  ├─ storage.js
│  │  └─ constants.js
│  └─ main.js
└─ assets/
   ├─ icons/
   └─ images/
```

Gợi ý flow frontend:

- Khi mở board:
  - Gọi REST để lấy state hiện tại (board + tasks)
  - Mở WebSocket và join room theo `projectId`
- Khi user kéo thả:
  - Gửi request (REST hoặc WS command) để cập nhật `status` + `position`
  - Nhận event broadcast và cập nhật UI cho tất cả client

---

## 8) Checklist tối thiểu để “đúng bài”

- CRUD Task
- Kanban 3 cột: Todo/Doing/Done
- Kéo thả chuyển cột + sắp xếp trong cột
- WebSocket broadcast thay đổi theo project
- Phân quyền theo ProjectMember (tối thiểu: member mới sửa được)
- Xử lý concurrent update (optimistic locking hoặc cơ chế tương đương)

