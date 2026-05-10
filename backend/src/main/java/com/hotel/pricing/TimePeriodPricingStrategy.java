package com.hotel.pricing;

import com.hotel.common.Constants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class TimePeriodPricingStrategy implements PricingStrategy {

    @Override
    public PricingContext apply(PricingContext context) {
        Map<LocalDate, BigDecimal> existingDayPrices = context.getDayPrices();
        if (existingDayPrices == null) {
            return context;
        }

        Map<LocalDate, BigDecimal> updatedDayPrices = new LinkedHashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<LocalDate, BigDecimal> entry : existingDayPrices.entrySet()) {
            LocalDate date = entry.getKey();
            BigDecimal seasonPrice = entry.getValue();

            BigDecimal timeRate = getTimeRate(date);
            BigDecimal dayPrice = seasonPrice.multiply(timeRate).setScale(2, RoundingMode.HALF_UP);
            updatedDayPrices.put(date, dayPrice);
            totalPrice = totalPrice.add(dayPrice);
        }

        context.setDayPrices(updatedDayPrices);
        context.setCurrentPrice(totalPrice);

        log.debug("时段定价: totalPrice={}", totalPrice);
        return context;
    }

    private BigDecimal getTimeRate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
            return Constants.WEEKDAY_RATE;
        } else {
            return Constants.WEEKEND_RATE;
        }
    }
}
