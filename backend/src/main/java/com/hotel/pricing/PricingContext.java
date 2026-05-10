package com.hotel.pricing;

import com.hotel.entity.SysRoom;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class PricingContext {

    private SysRoom room;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDate bookingDate;

    private int reserveDays;

    private BigDecimal basePrice;

    private BigDecimal currentPrice;

    private Map<LocalDate, BigDecimal> dayPrices;

    private BigDecimal seasonCoefficient;

    private String seasonName;

    private BigDecimal longStayDiscount;

    private BigDecimal earlyBirdDiscount;

    private boolean holiday;

    private BigDecimal holidayCoefficient;

    private String holidayName;
}
