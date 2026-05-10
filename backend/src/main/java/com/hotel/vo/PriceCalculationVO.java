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

    @JsonSerialize(using = ToStringSerializer.class)
    private Long roomId;

    private String roomNumber;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer reserveDays;

    private BigDecimal basePrice;

    private BigDecimal seasonCoefficient;

    private String seasonName;

    private Boolean holiday;

    private BigDecimal holidayCoefficient;

    private String holidayName;

    private BigDecimal longStayDiscount;

    private BigDecimal earlyBirdDiscount;

    private BigDecimal totalPrice;

    private BigDecimal deposit;
}
