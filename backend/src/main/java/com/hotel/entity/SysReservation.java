package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预订实体类
 */
@Data
@TableName("sys_reservation")
public class SysReservation {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 入住日期
     */
    private LocalDate checkInDate;

    /**
     * 退房日期
     */
    private LocalDate checkOutDate;

    /**
     * 预订天数
     */
    private Integer reserveDays;

    /**
     * 总价格
     */
    private BigDecimal totalPrice;

    /**
     * 定金
     */
    private BigDecimal deposit;

    /**
     * 定金状态：0=未支付，1=已支付，2=已退款，3=已扣罚
     */
    private Integer depositStatus;

    /**
     * 定金支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime depositPaidTime;

    /**
     * 预订时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reserveTime;

    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    /**
     * 状态：0=待入住，1=已取消，2=已完成，3=已违约
     */
    private Integer status;

    /**
     * 用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private SysUser user;

    /**
     * 房间信息（非数据库字段）
     */
    @TableField(exist = false)
    private SysRoom room;
}
