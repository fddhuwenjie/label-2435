package com.hotel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private BigDecimal originalTotalPrice;

    private BigDecimal stayDiscount;

    private BigDecimal earlyBirdDiscount;

    private BigDecimal totalPrice;

    private BigDecimal deposit;
}
