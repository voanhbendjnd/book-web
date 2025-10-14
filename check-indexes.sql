-- Script kiểm tra indexes trong database
-- Chạy các query này để xem indexes đã được tạo

-- 1. Kiểm tra tất cả indexes của database
SELECT 
    TABLE_NAME as 'Table',
    INDEX_NAME as 'Index Name',
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) as 'Columns',
    NON_UNIQUE as 'Non-Unique'
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'drinkwebShop'
  AND INDEX_NAME NOT IN ('PRIMARY')
GROUP BY TABLE_NAME, INDEX_NAME, NON_UNIQUE
ORDER BY TABLE_NAME, INDEX_NAME;

-- 2. Kiểm tra indexes của bảng users
SHOW INDEX FROM users;

-- 3. Kiểm tra indexes của bảng orders  
SHOW INDEX FROM orders;

-- 4. Kiểm tra indexes của bảng books
SHOW INDEX FROM books;

-- 5. Kiểm tra indexes của bảng cart_items
SHOW INDEX FROM cart_items;

-- 6. Kiểm tra indexes của bảng reviews
SHOW INDEX FROM reviews;

-- 7. Đếm số lượng indexes per table
SELECT 
    TABLE_NAME,
    COUNT(DISTINCT INDEX_NAME) as 'Index Count'
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'drinkwebShop'
  AND INDEX_NAME NOT IN ('PRIMARY')
GROUP BY TABLE_NAME
ORDER BY TABLE_NAME;

-- 8. Kiểm tra performance của một query
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';

-- 9. Kiểm tra performance của composite query
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com' AND refresh_token = 'test_token';
