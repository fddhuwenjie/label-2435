package com.hotel.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HolidayUpdateRequest {

    private LocalDate date;

    private String name;

    private BigDecimal coefficient;
}
