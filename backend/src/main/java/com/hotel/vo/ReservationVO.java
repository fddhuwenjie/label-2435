package com.hotel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hotel.entity.SysReservation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预订视图对象
 */
@Data
public class ReservationVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String username;
    private String userRealName;
    private String userPhone;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roomId;
    private String roomNumber;
    private Integer roomType;
    private String roomTypeName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer reserveDays;
    private BigDecimal totalPrice;
    private BigDecimal deposit;
    private Integer depositStatus;
    private String depositStatusName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime depositPaidTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
    private Integer status;
    private String statusName;

    public static ReservationVO fromEntity(SysReservation reservation) {
        ReservationVO vo = new ReservationVO();
        vo.setId(reservation.getId());
        vo.setUserId(reservation.getUserId());
        vo.setRoomId(reservation.getRoomId());
        vo.setCheckInDate(reservation.getCheckInDate());
        vo.setCheckOutDate(reservation.getCheckOutDate());
        vo.setReserveDays(reservation.getReserveDays());
        vo.setTotalPrice(reservation.getTotalPrice());
        vo.setDeposit(reservation.getDeposit());
        vo.setDepositStatus(reservation.getDepositStatus());
        vo.setDepositStatusName(getDepositStatusName(reservation.getDepositStatus()));
        vo.setDepositPaidTime(reservation.getDepositPaidTime());
        vo.setReserveTime(reservation.getReserveTime());
        vo.setCancelTime(reservation.getCancelTime());
        vo.setStatus(reservation.getStatus());
        vo.setStatusName(getStatusName(reservation.getStatus()));

        // 设置关联信息
        if (reservation.getUser() != null) {
            vo.setUsername(reservation.getUser().getUsername());
            vo.setUserRealName(reservation.getUser().getRealName());
            vo.setUserPhone(reservation.getUser().getPhone());
        }
        if (reservation.getRoom() != null) {
            vo.setRoomNumber(reservation.getRoom().getRoomNumber());
            vo.setRoomType(reservation.getRoom().getRoomType());
            vo.setRoomTypeName(reservation.getRoom().getRoomType() == 1 ? "单人间" : "双人间");
        }

        return vo;
    }

    private static String getDepositStatusName(Integer status) {
        return switch (status) {
            case 0 -> "未支付";
            case 1 -> "已支付";
            case 2 -> "已退款";
            case 3 -> "已扣罚";
            default -> "未知";
        };
    }

    private static String getStatusName(Integer status) {
        return switch (status) {
            case 0 -> "待入住";
            case 1 -> "已取消";
            case 2 -> "已完成";
            case 3 -> "已违约";
            default -> "未知";
        };
    }
}
