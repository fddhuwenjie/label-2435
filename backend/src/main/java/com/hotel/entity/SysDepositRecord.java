package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定金记录实体类
 */
@Data
@TableName("sys_deposit_record")
public class SysDepositRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 预订ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long reservationId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 操作类型：0=支付，1=退款，2=扣罚
     */
    private Integer operateType;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预订信息（非数据库字段）
     */
    @TableField(exist = false)
    private SysReservation reservation;
}
