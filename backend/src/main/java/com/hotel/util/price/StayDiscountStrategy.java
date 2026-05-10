package com.hotel.util.price;

import com.hotel.common.Constants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class StayDiscountStrategy implements PricingStrategy {

    @Override
    public void apply(PriceContext context) {
        int days = context.getReserveDays();
        BigDecimal discountRate = BigDecimal.ONE;

        if (days >= Constants.StayDiscount.MIN_DAYS_7) {
            discountRate = BigDecimal.valueOf(Constants.StayDiscount.RATE_7_DAYS);
        } else if (days >= Constants.StayDiscount.MIN_DAYS_3) {
            discountRate = BigDecimal.valueOf(Constants.StayDiscount.RATE_3_DAYS);
        }

        if (discountRate.compareTo(BigDecimal.ONE) != 0) {
            BigDecimal discountedPrice = context.getTotalPrice().multiply(discountRate);
            context.setTotalPrice(discountedPrice);
            log.debug("连住优惠策略应用: 天数={}, 折扣率={}, 优惠后总价={}",
                    days, discountRate, discountedPrice);
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
