package com.hotel.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.hotel.util.PriceCalculator.getSeasonCoefficient;

@Component
public class SeasonPricingStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(SeasonPricingStrategy.class);
    private static final int ORDER = 1;

    @Override
    public void apply(PriceContext context) {
        int days = (int) ChronoUnit.DAYS.between(context.getCheckInDate(), context.getCheckOutDate());
        context.setReserveDays(days);

        LocalDate currentDate = context.getCheckInDate();
        BigDecimal totalBase = BigDecimal.ZERO;

        while (currentDate.isBefore(context.getCheckOutDate())) {
            BigDecimal seasonCoefficient = context.getRoom().getSeasonCoefficient();
            if (seasonCoefficient == null) {
                seasonCoefficient = getSeasonCoefficient(currentDate.getMonthValue());
            }
            BigDecimal dayPrice = context.getBasePrice().multiply(seasonCoefficient);
            totalBase = totalBase.add(dayPrice);
            currentDate = currentDate.plusDays(1);
        }

        BigDecimal checkInSeasonCoefficient = context.getRoom().getSeasonCoefficient();
        if (checkInSeasonCoefficient == null) {
            checkInSeasonCoefficient = getSeasonCoefficient(context.getCheckInDate().getMonthValue());
        }
        context.setSeasonCoefficient(checkInSeasonCoefficient);
        context.setTotalPrice(totalBase);

        log.debug("季节定价策略应用: 基础总价={}, 季节系数={}", totalBase, checkInSeasonCoefficient);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
