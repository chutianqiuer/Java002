-- Mall Database Initialization Script

-- Create databases
CREATE DATABASE IF NOT EXISTS mall_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mall_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mall_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mall_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mall_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mall_user;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status INT DEFAULT 1 COMMENT '1:active, 0:inactive',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted INT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample users
INSERT INTO users (username, password, real_name, phone, email, status) VALUES
('admin', 'admin123', 'Administrator', '13800138000', 'admin@mall.com', 1),
('user1', 'user123', 'Zhang San', '13800138001', 'zhangsan@mall.com', 1),
('user2', 'user123', 'Li Si', '13800138002', 'lisi@mall.com', 1),
('user3', 'user123', 'Wang Wu', '13800138003', 'wangwu@mall.com', 1);

USE mall_product;

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    unit VARCHAR(20) DEFAULT 'piece',
    category VARCHAR(50),
    image_url VARCHAR(500),
    status INT DEFAULT 1 COMMENT '1:available, 0:unavailable',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted INT DEFAULT 0,
    INDEX idx_product_name (product_name),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample products
INSERT INTO products (product_name, description, price, stock, unit, category, status) VALUES
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB', 8999.00, 100, 'piece', 'Electronics', 1),
('MacBook Pro 14', 'Apple MacBook Pro 14 inch M3', 14999.00, 50, 'piece', 'Electronics', 1),
('AirPods Pro', 'Apple AirPods Pro 2nd generation', 1899.00, 200, 'piece', 'Electronics', 1),
('iPad Air', 'Apple iPad Air 5th generation', 4799.00, 80, 'piece', 'Electronics', 1),
('Watch Ultra', 'Apple Watch Ultra 2', 5999.00, 60, 'piece', 'Electronics', 1),
('小米手机14', 'Xiaomi 14 12GB+256GB', 3999.00, 150, 'piece', 'Electronics', 1),
('华为Mate60', 'Huawei Mate 60 Pro', 6999.00, 100, 'piece', 'Electronics', 1),
('戴森吹风机', 'Dyson Supersonic HD03', 2999.00, 70, 'piece', 'Appliances', 1),
('SK-II 护肤套装', 'SK-II PITERA Essential Radiance Kit', 1999.00, 120, 'set', 'Beauty', 1),
('茅台飞天', 'Moutai Feitian 53度 500ml', 1499.00, 50, 'bottle', 'Food', 1);

-- Inventory logs table
CREATE TABLE IF NOT EXISTS inventory_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    order_no VARCHAR(100),
    change_type INT NOT NULL COMMENT '1:sell, 2:refund, 3:replenish, 4:adjust',
    before_stock INT NOT NULL,
    after_stock INT NOT NULL,
    change_quantity INT NOT NULL,
    operator VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted INT DEFAULT 0,
    INDEX idx_product_id (product_id),
    INDEX idx_order_no (order_no),
    INDEX idx_change_type (change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE mall_order;

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_amount DECIMAL(10,2) NOT NULL,
    status INT DEFAULT 0 COMMENT '0:pending, 1:paid, 2:shipped, 3:confirmed, 4:cancelled, 5:refunding, 6:refunded',
    shipping_address VARCHAR(500),
    receiver_name VARCHAR(50),
    receiver_phone VARCHAR(20),
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted INT DEFAULT 0,
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE mall_payment;

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(100) NOT NULL UNIQUE,
    order_no VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method INT DEFAULT 1 COMMENT '1:wechat, 2:alipay, 3:bank_card, 4:credit',
    status INT DEFAULT 0 COMMENT '0:pending, 1:success, 2:failed, 3:refunding, 4:refunded',
    transaction_id VARCHAR(200),
    paid_time VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted INT DEFAULT 0,
    INDEX idx_payment_no (payment_no),
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE mall_admin;

-- Operation logs table
CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    module VARCHAR(50),
    operation VARCHAR(100),
    method VARCHAR(200),
    params TEXT,
    result TEXT,
    status INT DEFAULT 1 COMMENT '1:success, 0:failed',
    ip VARCHAR(50),
    duration BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_module (module),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
