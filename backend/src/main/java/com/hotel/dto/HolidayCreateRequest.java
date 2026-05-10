package com.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HolidayCreateRequest {

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotNull(message = "名称不能为空")
    private String name;

    @NotNull(message = "系数不能为空")
    private BigDecimal coefficient;
}
