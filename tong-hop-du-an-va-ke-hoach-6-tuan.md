# Tổng Hợp Dự Án Và Kế Hoạch 6 Tuần

## 1. Tổng quan hiện trạng dự án

Dự án hiện tại là một backend Java sử dụng **Quarkus** với định hướng xây dựng **hệ thống quản lý công việc nhóm thời gian thực theo mô hình Kanban**. Về nền tảng kỹ thuật, dự án đã có các dependency quan trọng trong `pom.xml` như:

- Quarkus REST
- REST Jackson
- Hibernate Validator
- Hibernate ORM Panache
- PostgreSQL JDBC
- WebSocket
- OpenAPI
- JUnit và RestAssured cho test

Điều này cho thấy nền tảng công nghệ đã được chuẩn bị đúng hướng cho một hệ thống có API, database và realtime.

## 2. Những gì đã làm được

### 2.1. Cấu trúc package backend đã được tổ chức lại

Mã nguồn Java hiện đã được sắp xếp lại theo hướng dễ mở rộng hơn, gồm các package:

- `org.myapp.common.controller`
- `org.myapp.common.entity`
- `org.myapp.user.entity`
- `org.myapp.project.entity`
- `org.myapp.task.entity`
- `org.myapp.comment.entity`
- `org.myapp.websocket`

Đây là bước nền quan trọng để tiếp tục thêm `repository`, `service`, `dto`, `controller` theo từng feature.

### 2.2. Đã có bộ entity cốt lõi cho bài toán task management

Hiện tại dự án đã có các entity chính:

- `BaseEntity`
  - chứa `id`, `createdAt`, `updatedAt`
  - dùng chung cho toàn bộ entity

- `User`
  - `username`
  - `email`
  - `passwordHash`
  - `fullName`
  - `systemRole`
  - `active`

- `Project`
  - `name`
  - `description`
  - `owner`
  - `status`

- `ProjectMember`
  - liên kết người dùng với project
  - có `roleInProject`
  - có `joinedAt`

- `Task`
  - `title`
  - `description`
  - `status`
  - `priority`
  - `project`
  - `createdBy`
  - `assignee`
  - `dueDate`

- `Comment`
  - `task`
  - `author`
  - `content`

### 2.3. Đã có enum nghiệp vụ cơ bản

- `SystemRole`
- `ProjectRole`
- `ProjectStatus`
- `TaskStatus`
- `TaskPriority`

Các enum này giúp mô hình dữ liệu đã bám khá sát nhu cầu thực tế của hệ thống quản lý công việc nhóm.

### 2.4. Đã có cấu hình PostgreSQL và Hibernate

Trong `src/main/resources/application.properties`, dự án đã có:

- cấu hình datasource PostgreSQL
- cấu hình Hibernate ORM
- bật log SQL

Điều này cho phép dự án có thể làm việc với database local ngay trong giai đoạn phát triển.

### 2.5. Đã có endpoint kiểm tra backend hoạt động

Hiện có `HealthController` với endpoint:

- `GET /hello`

Endpoint này chỉ ở mức kiểm tra hệ thống, chưa phải API nghiệp vụ.

### 2.6. Đã có WebSocket starter

Hiện có class `StartWebSocket` trong package `org.myapp.websocket`.

Nó đã xử lý các sự kiện:

- `onOpen`
- `onClose`
- `onError`
- `onMessage`

Tuy nhiên phần này mới chỉ dừng ở mức log sự kiện, chưa gắn vào nghiệp vụ đồng bộ task theo thời gian thực.

### 2.7. Đã có định hướng sản phẩm và đặc tả chức năng

Trong quá trình làm việc, mô tả dự án đã được chốt rõ:

- hệ thống quản lý công việc nhóm thời gian thực
- mô hình Kanban với `Todo`, `Doing`, `Done`
- đồng bộ dữ liệu bằng WebSocket
- frontend dùng HTML, CSS, JavaScript
- backend dùng Quarkus

Ngoài ra, dự án cũng đã có một file định hướng là `quan-ly-cong-viec-nhom.md`.

## 3. Những gì chưa hoàn thành

Mặc dù đã có nền tảng khá tốt, dự án hiện vẫn mới ở mức **skeleton backend + data model**, chưa phải một sản phẩm hoàn chỉnh.

### 3.1. Chưa có tầng repository, service, controller nghiệp vụ

Hiện chưa có:

- `UserRepository`, `ProjectRepository`, `TaskRepository`, `CommentRepository`
- `UserService`, `ProjectService`, `TaskService`, `CommentService`
- `ProjectController`, `TaskController`, `AuthController`, `CommentController`

Điều đó có nghĩa là phần entity đã có, nhưng chưa có lớp xử lý nghiệp vụ và chưa có API thật để thao tác dữ liệu.

### 3.2. Chưa có API CRUD cho project và task

Hệ thống hiện chưa có các chức năng API như:

- tạo project
- thêm thành viên vào project
- tạo task
- sửa task
- xóa task
- lấy danh sách task theo project
- chuyển trạng thái task giữa các cột Kanban
- thêm bình luận cho task

### 3.3. Chưa có xác thực và phân quyền

Hiện dự án chưa có:

- đăng ký
- đăng nhập
- JWT
- current user
- hash mật khẩu bằng BCrypt
- kiểm tra quyền truy cập theo project membership

Ngoài ra trong `application.properties`, phần security hiện vẫn đang để:

- `quarkus.security.enabled=false`

Đây là khoảng trống lớn nhất nếu muốn biến dự án thành hệ thống nhiều người dùng thực tế.

### 3.4. Chưa có realtime WebSocket đúng nghĩa nghiệp vụ

Hiện chưa có:

- WebSocket room theo `projectId`
- session registry
- chuẩn message JSON cho event
- broadcast realtime khi task được tạo, sửa, xóa, di chuyển cột
- xác thực người dùng khi kết nối WebSocket

Nói cách khác, WebSocket hiện chỉ là ví dụ khởi đầu, chưa phải cơ chế đồng bộ realtime cho Kanban board.

### 3.5. Chưa có frontend thực tế

Trong `src/main/resources/META-INF/index.html`, nội dung hiện gần như trống.

Chưa có:

- trang đăng nhập
- dashboard project
- giao diện Kanban board
- CSS cho cột task
- JavaScript gọi REST API
- JavaScript kết nối WebSocket
- thao tác kéo thả task giữa các cột

### 3.6. Chưa có dữ liệu mẫu và migration hoàn chỉnh

Hiện `import.sql` vẫn chỉ là file mẫu mặc định.

Chưa có:

- dữ liệu seed thực tế cho user/project/task
- migration như Flyway hoặc Liquibase
- tách cấu hình môi trường `dev`, `test`, `prod`

### 3.7. Chưa có test nghiệp vụ

Hiện tại test mới chỉ ở mức starter cho endpoint `/hello`.

Chưa có test cho:

- auth
- CRUD project
- CRUD task
- phân quyền
- WebSocket
- comment

### 3.8. README chưa phản ánh đúng dự án

`README.md` hiện vẫn gần như là README mặc định của Quarkus.

Chưa có:

- mô tả đúng mục tiêu dự án
- hướng dẫn chạy PostgreSQL
- hướng dẫn seed dữ liệu
- mô tả API
- mô tả WebSocket
- mô tả kiến trúc hệ thống

## 4. Đánh giá tổng thể

Nếu so với mô tả sản phẩm:

> Hệ thống quản lý công việc nhóm thời gian thực cho phép người dùng tạo, chỉnh sửa, xóa và theo dõi tiến độ công việc theo mô hình Kanban, sử dụng WebSocket để đồng bộ tức thì giữa nhiều người dùng, với frontend HTML/CSS/JS và backend Quarkus.

thì hiện tại dự án mới hoàn thành tốt phần:

- nền tảng công nghệ
- cấu trúc package
- mô hình dữ liệu
- cấu hình database cơ bản
- starter endpoint
- starter websocket

Các phần **chưa hoàn thành** chiếm phần lớn giá trị sản phẩm:

- API nghiệp vụ
- bảo mật
- realtime thật sự
- frontend
- test
- tài liệu hoàn chỉnh

## 5. Kế hoạch thực hiện trong 6 tuần

## Tuần 1: Hoàn thiện nền backend

### Mục tiêu

Chuyển từ trạng thái chỉ có entity sang backend có thể phát triển tiếp một cách ổn định.

### Công việc

- tạo `repository` cho `User`, `Project`, `ProjectMember`, `Task`, `Comment`
- tạo `service` cơ bản cho `Project` và `Task`
- tạo `dto` cho request/response
- thống nhất format response và error handling
- bổ sung dữ liệu mẫu trong `import.sql`
- cập nhật `README.md` để mô tả đúng dự án

### Kết quả mong đợi

- backend có cấu trúc đầy đủ hơn
- có dữ liệu mẫu để test API
- tài liệu chạy local rõ ràng

## Tuần 2: Xây dựng REST API cho project và task

### Mục tiêu

Có thể thao tác project và task qua REST API.

### Công việc

- tạo API project:
  - tạo project
  - lấy danh sách project
  - lấy chi tiết project
  - thêm thành viên

- tạo API task:
  - tạo task
  - sửa task
  - xóa task
  - đổi trạng thái task
  - lấy danh sách task theo project

- tạo API comment cơ bản
- thêm validation đầu vào
- thêm xử lý lỗi chuẩn

### Kết quả mong đợi

- project và task có thể CRUD qua API
- Swagger/OpenAPI phản ánh đúng API thực tế

## Tuần 3: Xây dựng auth và phân quyền

### Mục tiêu

Cho hệ thống có xác thực người dùng và bảo vệ API.

### Công việc

- tạo API đăng ký
- tạo API đăng nhập
- sinh JWT
- hash mật khẩu bằng BCrypt
- bật security
- lấy current user từ token
- kiểm tra quyền theo `ProjectMember`

### Kết quả mong đợi

- người dùng đăng nhập được
- API được bảo vệ
- chỉ thành viên project mới truy cập được dữ liệu project

## Tuần 4: Xây dựng realtime WebSocket cho Kanban

### Mục tiêu

Khi một người thao tác task, các người dùng khác trong cùng project nhìn thấy thay đổi ngay.

### Công việc

- thay `StartWebSocket` demo bằng socket nghiệp vụ theo `projectId`
- tạo session registry
- định nghĩa format event JSON
- broadcast khi:
  - tạo task
  - sửa task
  - chuyển cột task
  - xóa task
  - thêm comment

- xác thực kết nối WebSocket

### Kết quả mong đợi

- board Kanban đồng bộ realtime
- WebSocket gắn chặt với nghiệp vụ task/project

## Tuần 5: Xây dựng frontend HTML/CSS/JS

### Mục tiêu

Có giao diện thực tế để demo hệ thống.

### Công việc

- tạo `login.html`
- tạo `dashboard.html`
- tạo `board.html`
- viết CSS cho layout Kanban
- viết JavaScript gọi API bằng `fetch`
- viết JavaScript lưu token
- viết JavaScript kết nối WebSocket
- hỗ trợ kéo thả task giữa các cột

### Kết quả mong đợi

- có giao diện đăng nhập
- có dashboard project
- có Kanban board hoạt động được
- có realtime update trên giao diện

## Tuần 6: Kiểm thử, hoàn thiện và chuẩn bị demo

### Mục tiêu

Chuyển từ bản chạy được sang bản có thể demo và báo cáo.

### Công việc

- viết test cho:
  - auth
  - project
  - task
  - comment
  - permission

- kiểm tra các tình huống lỗi
- hoàn thiện README
- viết tài liệu kiến trúc
- chuẩn bị dữ liệu demo
- chuẩn bị kịch bản demo

### Kết quả mong đợi

- có bản MVP hoàn chỉnh
- có tài liệu đủ dùng cho báo cáo hoặc bảo vệ
- có dữ liệu và luồng demo rõ ràng

## 6. Ưu tiên thực hiện ngay bây giờ

Nếu bắt đầu triển khai tiếp từ hôm nay, thứ tự ưu tiên nên là:

1. Tạo `repository + service + dto + controller` cho `Project` và `Task`
2. Tạo seed data trong `import.sql`
3. Viết lại `README.md` theo mô tả dự án thật
4. Làm `AuthController` và JWT
5. Sau đó mới làm WebSocket realtime đúng nghĩa
6. Cuối cùng mới làm frontend hoàn chỉnh

## 7. Kết luận

Dự án hiện tại đã có **bộ khung kỹ thuật đúng hướng**, đặc biệt là:

- nền Quarkus đã sẵn sàng
- dữ liệu cốt lõi đã được mô hình hóa
- cấu trúc package đã được tổ chức lại tốt hơn trước

Tuy nhiên, để trở thành **hệ thống quản lý công việc nhóm thời gian thực hoàn chỉnh**, dự án vẫn còn thiếu phần lớn lớp ứng dụng và giao diện người dùng.

Nếu bám đúng kế hoạch 6 tuần ở trên, bạn hoàn toàn có thể phát triển dự án này thành một bản MVP đủ tốt để:

- demo
- làm đồ án
- báo cáo môn học
- hoặc tiếp tục mở rộng thành sản phẩm hoàn chỉnh hơn.
