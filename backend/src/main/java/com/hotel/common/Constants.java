package com.hotel.common;

/**
 * 系统常量
 */
public class Constants {

    /**
     * 用户角色
     */
    public static class Role {
        public static final int USER = 0;      // 普通用户
        public static final int ADMIN = 1;     // 管理员
    }

    /**
     * 房间类型
     */
    public static class RoomType {
        public static final int SINGLE = 1;    // 单人间
        public static final int DOUBLE = 2;    // 双人间
    }

    /**
     * 房间状态
     */
    public static class RoomStatus {
        public static final int FREE = 0;      // 空闲
        public static final int RESERVED = 1;  // 已预订
        public static final int OCCUPIED = 2;  // 已入住
        public static final int MAINTENANCE = 3; // 维护
    }

    /**
     * 预订状态
     */
    public static class ReservationStatus {
        public static final int PENDING = 0;   // 待入住
        public static final int CANCELLED = 1; // 已取消
        public static final int COMPLETED = 2; // 已完成
        public static final int BREACHED = 3;  // 已违约
    }

    /**
     * 定金状态
     */
    public static class DepositStatus {
        public static final int UNPAID = 0;    // 未支付
        public static final int PAID = 1;      // 已支付
        public static final int REFUNDED = 2;  // 已退款
        public static final int FORFEITED = 3; // 已扣罚
    }

    /**
     * 定金操作类型
     */
    public static class DepositOperateType {
        public static final int PAY = 0;       // 支付
        public static final int REFUND = 1;    // 退款
        public static final int FORFEIT = 2;   // 扣罚
    }

    /**
     * 取消预订免罚时限（小时）
     */
    public static final int CANCEL_FREE_HOURS = 6;

    /**
     * 定金比例
     */
    public static final double DEPOSIT_RATE = 0.1;

    /**
     * 周末折扣系数
     */
    public static final double WEEKEND_RATE = 1.0;

    /**
     * 工作日折扣系数
     */
    public static final double WEEKDAY_RATE = 0.5;
}
