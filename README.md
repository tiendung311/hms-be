# HMS Backend

Hệ thống backend cho **Hotel Management System (HMS)** sử dụng Spring Boot.  
Cung cấp các chức năng quản lý phòng, đặt phòng, thanh toán trực tuyến (PayOS), gửi email, thống kê doanh thu, và quản trị hệ thống.

---

## 🛠️ Công nghệ sử dụng

- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- MySQL
- Lombok
- Clerk SDK
- PayOS SDK
- Spring Scheduler
- Java Mail Sender
- RESTful API

---

## ⚙️ Cấu trúc dự án

```
src/
├── config/ # Cấu hình PayOS, bảo mật,...
├── controller/ # REST API endpoints
├── entity/ # Entities: Users, Bookings, Payments, Rooms,...
├── model/ # DTOs phục vụ request/response
├── repository/ # JPA Repositories
├── service/ # Business logic
├── service/impl/ # Triển khai cụ thể của services
└── util/ # Tiện ích phụ trợ
```

---

## 🚀 Khởi động dự án

### 1. Clone repository

```bash
git clone https://github.com/your-org/hms-backend.git
cd hms-backend
```

### 2. Cấu hình application.yml
Tạo file src/main/resources/application.yml với nội dung sau:

```bash
yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hms_db
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

payos:
  client-id: your_client_id
  api-key: your_api_key
  checksum-key: your_checksum_key

mail:
  host: smtp.gmail.com
  port: 587
  username: your_email@gmail.com
  password: your_email_password
```
  
  🔐 Gợi ý bảo mật: Không nên commit file này lên GitHub. Hãy sử dụng .env và @Value trong Spring Boot nếu cần.

### 3. Build và chạy server
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Mặc định server sẽ chạy tại: http://localhost:8080

---

### 📦 Tính năng chính
🏨 Đặt phòng
- Tìm kiếm và đặt phòng theo loại và ngày
- Đơn đặt phòng ở trạng thái Chờ sẽ bị huỷ nếu quá 15 phút chưa thanh toán

💵 Thanh toán PayOS
- Tạo và huỷ link thanh toán
- Lắng nghe webhook PayOS để cập nhật trạng thái thanh toán
- Gửi email sau khi thanh toán thành công

🛏️ Quản lý phòng
- Tự động cập nhật trạng thái phòng qua cron job
- Xác nhận check-in/check-out

📊 Thống kê
- Tổng doanh thu, tổng hoàn tiền
- Doanh thu ròng từng tháng
- Lọc theo khoảng thời gian
