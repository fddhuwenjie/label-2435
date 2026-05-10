package com.hotel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HolidayUpdateRequest {

    private LocalDate date;

    private String name;

    @DecimalMin(value = "1.0", message = "加价系数不能小于1.0")
    private BigDecimal coefficient;
}
