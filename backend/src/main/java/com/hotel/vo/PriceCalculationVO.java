package com.hotel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 价格计算结果
 */
@Data
public class PriceCalculationVO {

    /**
     * 房间ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roomId;

    /**
     * 房间号
     */
    private String roomNumber;

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
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 季节系数
     */
    private BigDecimal seasonCoefficient;

    /**
     * 总价格
     */
    private BigDecimal totalPrice;

    /**
     * 定金
     */
    private BigDecimal deposit;
}
