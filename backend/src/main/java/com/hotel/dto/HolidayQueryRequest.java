package com.hotel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayQueryRequest {

    private Integer current = 1;

    private Integer size = 10;

    private LocalDate startDate;

    private LocalDate endDate;
}
