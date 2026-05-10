package com.hotel.util.price;

import com.hotel.common.Constants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
public class EarlyBirdDiscountStrategy implements PricingStrategy {

    @Override
    public void apply(PriceContext context) {
        LocalDate bookingDate = context.getBookingDate();
        if (bookingDate == null) {
            bookingDate = LocalDate.now();
        }

        long daysInAdvance = ChronoUnit.DAYS.between(bookingDate, context.getCheckInDate());
        BigDecimal discountRate = BigDecimal.ONE;

        if (daysInAdvance >= Constants.EarlyBirdDiscount.MIN_DAYS_30) {
            discountRate = BigDecimal.valueOf(Constants.EarlyBirdDiscount.RATE_30_DAYS);
        } else if (daysInAdvance >= Constants.EarlyBirdDiscount.MIN_DAYS_14) {
            discountRate = BigDecimal.valueOf(Constants.EarlyBirdDiscount.RATE_14_DAYS);
        }

        if (discountRate.compareTo(BigDecimal.ONE) != 0) {
            BigDecimal discountedPrice = context.getTotalPrice().multiply(discountRate);
            context.setTotalPrice(discountedPrice);
            log.debug("早鸟优惠策略应用: 提前预订天数={}, 折扣率={}, 优惠后总价={}",
                    daysInAdvance, discountRate, discountedPrice);
        }
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
