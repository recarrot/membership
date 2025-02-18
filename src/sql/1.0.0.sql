-- 创建数据库
CREATE DATABASE IF NOT EXISTS membership_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE membership_db;

-- 创建会员表
CREATE TABLE IF NOT EXISTS members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(50) NOT NULL UNIQUE COMMENT '会员卡号',
    name VARCHAR(100) NOT NULL COMMENT '会员姓名',
    id_number VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    age INT COMMENT '年龄',
    join_date DATETIME NOT NULL COMMENT '入会时间',
    consumption_count INT NOT NULL DEFAULT 0 COMMENT '消费次数',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否激活',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_card_number (card_number),
    INDEX idx_name (name)
) COMMENT '会员表';

-- 创建交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL COMMENT '会员ID',
    type VARCHAR(20) NOT NULL COMMENT '交易类型(RECHARGE:充值, CONSUMPTION:消费)',
    amount DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    INDEX idx_member_id (member_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (member_id) REFERENCES members(id)
) COMMENT '交易记录表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) NOT NULL COMMENT '角色',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) COMMENT '用户表';

-- 插入默认管理员账号
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$12$L3/09TJoIFwE2fh21RZGXeOk0y4wu9yaRy4o.G1jSk7HF9ZHXKYOu', 'ADMIN')
ON DUPLICATE KEY UPDATE username = username;
-- 注意: 这里的密码是 'admin123' 的 BCrypt 加密形式
