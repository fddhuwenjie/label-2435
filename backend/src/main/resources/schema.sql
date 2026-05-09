-- 酒店预订管理系统数据库初始化脚本
-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    id_card VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0=普通用户，1=管理员',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_id_card (id_card),
    KEY idx_role (role),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 房间表
CREATE TABLE IF NOT EXISTS sys_room (
    id BIGINT NOT NULL COMMENT '主键ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号（如：单001、双056）',
    room_type TINYINT NOT NULL COMMENT '房间类型：1=单人间，2=双人间',
    base_price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    season_coefficient DECIMAL(3,2) NOT NULL DEFAULT 1.00 COMMENT '季节系数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=空闲，1=已预订，2=已入住，3=维护',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_number (room_number),
    KEY idx_room_type (room_type),
    KEY idx_status (status),
    KEY idx_room_type_status (room_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 预订表
CREATE TABLE IF NOT EXISTS sys_reservation (
    id BIGINT NOT NULL COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    room_id BIGINT NOT NULL COMMENT '房间ID',
    check_in_date DATE NOT NULL COMMENT '入住日期',
    check_out_date DATE NOT NULL COMMENT '退房日期',
    reserve_days INT NOT NULL COMMENT '预订天数',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价格',
    deposit DECIMAL(10,2) NOT NULL COMMENT '定金',
    deposit_status TINYINT NOT NULL DEFAULT 0 COMMENT '定金状态：0=未支付，1=已支付，2=已退款，3=已扣罚',
    deposit_paid_time DATETIME DEFAULT NULL COMMENT '定金支付时间',
    reserve_time DATETIME NOT NULL COMMENT '预订时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待入住，1=已取消，2=已完成，3=已违约',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_room_id (room_id),
    KEY idx_status (status),
    KEY idx_reserve_time (reserve_time),
    KEY idx_check_in_date (check_in_date),
    KEY idx_check_out_date (check_out_date),
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES sys_room(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预订表';

-- 定金记录表
CREATE TABLE IF NOT EXISTS sys_deposit_record (
    id BIGINT NOT NULL COMMENT '主键ID',
    reservation_id BIGINT NOT NULL COMMENT '预订ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    operate_type TINYINT NOT NULL COMMENT '操作类型：0=支付，1=退款，2=扣罚',
    operate_time DATETIME NOT NULL COMMENT '操作时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_reservation_id (reservation_id),
    KEY idx_operate_type (operate_type),
    KEY idx_operate_time (operate_time),
    CONSTRAINT fk_deposit_reservation FOREIGN KEY (reservation_id) REFERENCES sys_reservation(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定金记录表';
