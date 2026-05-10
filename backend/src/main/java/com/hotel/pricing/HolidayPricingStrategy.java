package com.hotel.pricing;

import com.hotel.entity.SysHoliday;
import com.hotel.service.SysHolidayService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HolidayPricingStrategy implements PricingStrategy {

    private final SysHolidayService holidayService;

    public HolidayPricingStrategy(SysHolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @Override
    public PricingContext apply(PricingContext context) {
        Map<LocalDate, BigDecimal> existingDayPrices = context.getDayPrices();
        if (existingDayPrices == null) {
            return context;
        }

        List<LocalDate> dates = new ArrayList<>(existingDayPrices.keySet());
        List<SysHoliday> holidays = holidayService.getHolidaysByDates(dates);
        Map<LocalDate, SysHoliday> holidayMap = holidays.stream()
                .collect(Collectors.toMap(SysHoliday::getDate, h -> h, (a, b) -> a));

        Map<LocalDate, BigDecimal> updatedDayPrices = new LinkedHashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        boolean hasHoliday = false;
        BigDecimal maxCoefficient = BigDecimal.ONE;
        String holidayName = null;

        for (Map.Entry<LocalDate, BigDecimal> entry : existingDayPrices.entrySet()) {
            LocalDate date = entry.getKey();
            BigDecimal dayPrice = entry.getValue();

            SysHoliday holiday = holidayMap.get(date);
            if (holiday != null) {
                BigDecimal coefficient = holiday.getCoefficient();
                dayPrice = dayPrice.multiply(coefficient).setScale(2, RoundingMode.HALF_UP);
                hasHoliday = true;
                if (coefficient.compareTo(maxCoefficient) > 0) {
                    maxCoefficient = coefficient;
                    holidayName = holiday.getName();
                }
                log.debug("节假日加价: date={}, holiday={}, coefficient={}", date, holiday.getName(), coefficient);
            }

            updatedDayPrices.put(date, dayPrice);
            totalPrice = totalPrice.add(dayPrice);
        }

        context.setDayPrices(updatedDayPrices);
        context.setCurrentPrice(totalPrice);
        context.setHoliday(hasHoliday);
        context.setHolidayCoefficient(hasHoliday ? maxCoefficient : null);
        context.setHolidayName(holidayName);

        log.debug("节假日定价: hasHoliday={}, totalPrice={}", hasHoliday, totalPrice);
        return context;
    }
}
