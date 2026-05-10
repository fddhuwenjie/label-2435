package com.hotel.util.price;

import com.hotel.util.PriceCalculator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class SeasonPricingStrategy implements PricingStrategy {

    @Override
    public void apply(PriceContext context) {
        BigDecimal seasonCoefficient = context.getRoom().getSeasonCoefficient();
        if (seasonCoefficient == null) {
            seasonCoefficient = PriceCalculator.getSeasonCoefficient(context.getCheckInDate().getMonthValue());
        }
        context.setSeasonCoefficient(seasonCoefficient);

        BigDecimal dailyPrice = context.getRoom().getBasePrice().multiply(seasonCoefficient);
        context.setDailyPrice(dailyPrice);

        log.debug("季节定价策略应用: 季节系数={}, 日基础价={}", seasonCoefficient, dailyPrice);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
