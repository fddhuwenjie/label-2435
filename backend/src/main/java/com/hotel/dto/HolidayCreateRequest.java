package com.hotel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HolidayCreateRequest {

    @NotNull(message = "节假日日期不能为空")
    @Future(message = "节假日日期必须是未来日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "节假日名称不能为空")
    private String name;

    @NotNull(message = "加价系数不能为空")
    @DecimalMin(value = "1.0", message = "加价系数不能小于1.0")
    private BigDecimal coefficient;
}
