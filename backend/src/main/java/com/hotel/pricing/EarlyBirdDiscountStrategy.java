package com.hotel.pricing;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
public class EarlyBirdDiscountStrategy implements PricingStrategy {

    private static final long FOURTEEN_DAYS = 14;
    private static final long THIRTY_DAYS = 30;
    private static final BigDecimal FOURTEEN_DAY_DISCOUNT = new BigDecimal("0.95");
    private static final BigDecimal THIRTY_DAY_DISCOUNT = new BigDecimal("0.90");

    @Override
    public PricingContext apply(PricingContext context) {
        LocalDate bookingDate = context.getBookingDate();
        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }

        long daysInAdvance = ChronoUnit.DAYS.between(bookingDate, context.getCheckInDate());
        BigDecimal currentPrice = context.getCurrentPrice();

        if (daysInAdvance >= THIRTY_DAYS) {
            BigDecimal discount = THIRTY_DAY_DISCOUNT;
            BigDecimal discountedPrice = currentPrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
            context.setEarlyBirdDiscount(discount);
            context.setCurrentPrice(discountedPrice);
            log.debug("早鸟优惠: daysInAdvance={}, discount={}, discountedPrice={}", daysInAdvance, discount, discountedPrice);
        } else if (daysInAdvance >= FOURTEEN_DAYS) {
            BigDecimal discount = FOURTEEN_DAY_DISCOUNT;
            BigDecimal discountedPrice = currentPrice.multiply(discount).setScale(2, RoundingMode.HALF_UP);
            context.setEarlyBirdDiscount(discount);
            context.setCurrentPrice(discountedPrice);
            log.debug("早鸟优惠: daysInAdvance={}, discount={}, discountedPrice={}", daysInAdvance, discount, discountedPrice);
        } else {
            context.setEarlyBirdDiscount(BigDecimal.ONE);
        }

        return context;
    }
}
