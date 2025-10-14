-- Database Optimization Script
-- Run these commands in your MySQL database

-- User table indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_email_refresh_token ON users(email, refresh_token(100));
CREATE INDEX idx_user_active ON users(active);

-- Book table indexes  
CREATE INDEX idx_book_title ON books(title);
CREATE INDEX idx_book_active ON books(active);
CREATE INDEX idx_book_price ON books(price);
CREATE INDEX idx_book_stock ON books(stockQuantity);

-- Order table indexes
CREATE INDEX idx_order_user_id ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_created_date ON orders(orderCreateDate);
CREATE INDEX idx_order_user_status ON orders(user_id, status);

-- OrderItem table indexes
CREATE INDEX idx_order_item_order_id ON order_items(order_id);
CREATE INDEX idx_order_item_book_id ON order_items(book_id);

-- Cart table indexes
CREATE INDEX idx_cart_user_id ON carts(user_id);

-- CartItem table indexes
CREATE INDEX idx_cart_item_book_cart ON cart_items(book_id, cart_id);
CREATE INDEX idx_cart_item_cart_id ON cart_items(cart_id);

-- Review table indexes
CREATE INDEX idx_review_book_id ON reviews(book_id);
CREATE INDEX idx_review_user_id ON reviews(user_id);

-- Category table indexes
CREATE INDEX idx_category_active ON categories(active);

-- Role table indexes
CREATE INDEX idx_role_active ON roles(active);

-- Permission table indexes
CREATE INDEX idx_permission_api_path ON permissions(api_path);
CREATE INDEX idx_permission_method ON permissions(method);
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'drinkwebShop'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

show index from users