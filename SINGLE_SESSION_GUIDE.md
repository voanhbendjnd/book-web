# Hướng dẫn Single Session Implementation

## Tổng quan

Tính năng Single Session đảm bảo rằng mỗi tài khoản chỉ có thể đăng nhập từ một thiết bị/location tại một thời điểm. Khi có người đăng nhập mới, session cũ sẽ bị vô hiệu hóa và người dùng cũ sẽ bị logout tự động.

## Cách hoạt động

### 1. Session Management

- Mỗi user có một `sessionId` duy nhất được lưu trong database
- Khi login, hệ thống tạo `sessionId` mới và invalidate session cũ
- JWT token chứa `sessionId` để xác thực

### 2. Validation Process

- Mỗi request được validate qua `SessionValidationFilter`
- Nếu `sessionId` trong token không khớp với database → reject request
- User nhận được response 401 và cần login lại

## Các thay đổi đã implement

### 1. User Entity

```java
@Column(name = "session_id")
private String sessionId;
```

### 2. SessionManager Service

- `createNewSession()`: Tạo session mới
- `isValidSession()`: Kiểm tra session hợp lệ
- `invalidateSession()`: Vô hiệu hóa session
- `invalidateExistingSession()`: Vô hiệu hóa session cũ trước khi tạo mới

### 3. SecurityUtils

- Cập nhật `createAccessToken()` để include `sessionId`
- Thêm `getCurrentSessionId()` để lấy sessionId từ JWT

### 4. AuthController

- Login: Tạo session mới và invalidate session cũ
- Refresh token: Tạo session mới
- Logout: Invalidate session

### 5. CustomJwtAuthenticationConverter

- Validate sessionId ngay trong quá trình JWT authentication
- Reject authentication nếu session không hợp lệ

### 6. SecurityConfig

- Sử dụng `CustomJwtAuthenticationConverter` thay vì default converter

## Cách test

### 1. Chạy test script

```bash
# Cài đặt axios nếu chưa có
npm install axios

# Chỉnh sửa email/password trong debug-session.js
# Chạy test
node debug-session.js
```

### 2. Test manual

1. Login với tài khoản A → ghi nhớ token
2. Gọi API protected với token A → thành công
3. Login lại với tài khoản A → nhận token mới
4. Gọi API với token cũ → bị reject (401)
5. Gọi API với token mới → thành công

## Lưu ý quan trọng

### 1. Database Migration

Cần tạo migration để thêm column `session_id` vào bảng `users`:

```sql
ALTER TABLE users ADD COLUMN session_id VARCHAR(255);
```

### 2. Performance

- Session validation chạy trên mỗi request
- Có thể cache session info để tối ưu performance
- Xem xét cleanup session cũ định kỳ

### 3. Security

- SessionId được generate ngẫu nhiên (UUID)
- Session bị invalidate ngay khi logout
- Không có session "zombie" tồn tại

### 4. User Experience

- User sẽ nhận thông báo "Session expired" khi bị logout
- Frontend cần handle 401 response và redirect đến login
- Có thể implement notification khi session bị invalidate

## Tối ưu hóa có thể thực hiện

### 1. Redis Cache

```java
@Cacheable("userSessions")
public boolean isValidSession(String email, String sessionId) {
    // Cache session validation
}
```

### 2. WebSocket Notification

```java
// Gửi notification đến client khi session bị invalidate
@EventListener
public void handleSessionInvalidate(SessionInvalidateEvent event) {
    // Send WebSocket message
}
```

### 3. Session Cleanup

```java
@Scheduled(fixedRate = 300000) // 5 minutes
public void cleanupExpiredSessions() {
    // Clean up old sessions
}
```

## Troubleshooting

### 1. Session không được invalidate

- Kiểm tra SessionManager có được inject đúng không
- Kiểm tra database có update sessionId không

### 2. Filter không hoạt động

- Kiểm tra SecurityConfig có add filter đúng không
- Kiểm tra filter order

### 3. JWT không chứa sessionId

- Kiểm tra SecurityUtils.createAccessToken()
- Kiểm tra JWT claims

## Kết luận

Implementation này đảm bảo:

- ✅ Mỗi user chỉ có 1 session active
- ✅ Session cũ bị invalidate khi login mới
- ✅ Security được tăng cường
- ✅ Performance acceptable
- ✅ Dễ maintain và extend
