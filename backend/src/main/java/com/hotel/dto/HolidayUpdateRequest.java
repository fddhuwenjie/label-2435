package com.hotel.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HolidayUpdateRequest {

    private LocalDate date;
    private String name;

    @DecimalMin(value = "1.0", message = "加价系数不能小于1.0")
    private BigDecimal coefficient;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
}
