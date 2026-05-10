package com.hotel.common;

import java.math.BigDecimal;

public class Constants {

    public static class Role {
        public static final int USER = 0;
        public static final int ADMIN = 1;
    }

    public static class RoomType {
        public static final int SINGLE = 1;
        public static final int DOUBLE = 2;
    }

    public static class RoomStatus {
        public static final int FREE = 0;
        public static final int RESERVED = 1;
        public static final int OCCUPIED = 2;
        public static final int MAINTENANCE = 3;
    }

    public static class ReservationStatus {
        public static final int PENDING = 0;
        public static final int CANCELLED = 1;
        public static final int COMPLETED = 2;
        public static final int BREACHED = 3;
    }

    public static class DepositStatus {
        public static final int UNPAID = 0;
        public static final int PAID = 1;
        public static final int REFUNDED = 2;
        public static final int FORFEITED = 3;
    }

    public static class DepositOperateType {
        public static final int PAY = 0;
        public static final int REFUND = 1;
        public static final int FORFEIT = 2;
    }

    public static final int CANCEL_FREE_HOURS = 6;

    public static final BigDecimal DEPOSIT_RATE = new BigDecimal("0.1");

    public static final BigDecimal WEEKEND_RATE = new BigDecimal("1.0");

    public static final BigDecimal WEEKDAY_RATE = new BigDecimal("0.5");
}
