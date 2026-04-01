-- 宿舍报修管理系统数据库脚本

CREATE DATABASE IF NOT EXISTS dormitory_repair DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dormitory_repair;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    account VARCHAR(20) NOT NULL UNIQUE COMMENT '账号（学号/工号）',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密）',
    role TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-学生，2-管理员',
    name VARCHAR(50) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    building VARCHAR(20) COMMENT '宿舍楼栋',
    room VARCHAR(20) COMMENT '房间号',
    is_bound TINYINT DEFAULT 0 COMMENT '是否绑定宿舍：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_account (account),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 报修单表
CREATE TABLE IF NOT EXISTS repair_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报修单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '报修单号',
    user_id BIGINT NOT NULL COMMENT '报修人ID',
    building VARCHAR(20) NOT NULL COMMENT '宿舍楼栋',
    room VARCHAR(20) NOT NULL COMMENT '房间号',
    device_type TINYINT NOT NULL COMMENT '设备类型：1-水龙头，2-电灯，3-门窗，4-空调，5-热水器，6-其他',
    priority TINYINT DEFAULT 2 COMMENT '优先级：1-紧急，2-普通，3-低',
    description TEXT NOT NULL COMMENT '问题描述',
    images VARCHAR(500) COMMENT '图片URL，多个用逗号分隔',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已完成，3-已取消',
    handler_id BIGINT COMMENT '处理人ID（管理员）',
    handle_note TEXT COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    handle_time DATETIME COMMENT '处理时间',
    finish_time DATETIME COMMENT '完成时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_no (order_no),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修单表';

-- 评价表
CREATE TABLE IF NOT EXISTS evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '报修单ID',
    user_id BIGINT NOT NULL COMMENT '评价人ID',
    rating TINYINT NOT NULL COMMENT '评分：1-5星',
    content TEXT COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_order_id (order_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '接收人ID',
    type TINYINT NOT NULL COMMENT '类型：1-状态变更，2-系统通知',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-否，1-是',
    related_id BIGINT COMMENT '关联ID（如报修单ID）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
