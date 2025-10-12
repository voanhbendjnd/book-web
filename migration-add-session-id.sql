-- Migration script để thêm session_id column vào bảng users
-- Chạy script này trên database để enable tính năng single session

-- Thêm column session_id vào bảng users
ALTER TABLE users ADD COLUMN session_id VARCHAR(255) NULL;

-- Thêm index để tối ưu query performance
CREATE INDEX idx_users_session_id ON users(session_id);

-- Comment cho column
ALTER TABLE users MODIFY COLUMN session_id VARCHAR(255) NULL COMMENT 'Session ID để track session hiện tại, null nếu user chưa login';

-- Kiểm tra kết quả
DESCRIBE users;

-- Optional: Clean up session_id cũ nếu có (uncomment nếu cần)
-- UPDATE users SET session_id = NULL WHERE session_id IS NOT NULL;
