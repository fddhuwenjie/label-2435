package com.hotel.util.price;

import com.hotel.entity.SysHoliday;
import com.hotel.entity.SysRoom;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class PriceContext {

    private SysRoom room;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDate bookingDate;

    private int reserveDays;

    private List<SysHoliday> holidays;

    private Map<LocalDate, SysHoliday> holidayMap;

    private BigDecimal seasonCoefficient;

    private BigDecimal dailyPrice;

    private BigDecimal totalPrice;
}
