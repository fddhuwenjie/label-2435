package com.hotel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HolidayCreateRequest {

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotBlank(message = "节假日名称不能为空")
    private String name;

    @NotNull(message = "加价系数不能为空")
    @DecimalMin(value = "1.0", message = "加价系数不能小于1.0")
    private BigDecimal coefficient;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
}
