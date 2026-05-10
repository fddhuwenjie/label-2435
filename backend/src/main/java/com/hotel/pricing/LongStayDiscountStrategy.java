package com.hotel.pricing;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class LongStayDiscountStrategy implements PricingStrategy {

    private static final int THREE_NIGHTS = 3;
    private static final int SEVEN_NIGHTS = 7;
    private static final BigDecimal THREE_NIGHT_DISCOUNT = new BigDecimal("0.95");
    private static final BigDecimal SEVEN_NIGHT_DISCOUNT = new BigDecimal("0.90");

    @Override
    public PricingContext apply(PricingContext context) {
        int days = context.getReserveDays();
        BigDecimal currentPrice = context.getCurrentPrice();

        if (days >= SEVEN_NIGHTS) {
            BigDecimal discount = SEVEN_NIGHT_DISCOUNT;
            BigDecimal discountedPrice = currentPrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
            context.setLongStayDiscount(discount);
            context.setCurrentPrice(discountedPrice);
            log.debug("连住优惠: days={}, discount={}, discountedPrice={}", days, discount, discountedPrice);
        } else if (days >= THREE_NIGHTS) {
            BigDecimal discount = THREE_NIGHT_DISCOUNT;
            BigDecimal discountedPrice = currentPrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
            context.setLongStayDiscount(discount);
            context.setCurrentPrice(discountedPrice);
            log.debug("连住优惠: days={}, discount={}, discountedPrice={}", days, discount, discountedPrice);
        } else {
            context.setLongStayDiscount(BigDecimal.ONE);
        }

        return context;
    }
}
