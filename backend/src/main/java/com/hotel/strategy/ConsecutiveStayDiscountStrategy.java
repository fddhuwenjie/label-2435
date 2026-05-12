package com.hotel.strategy;

import com.hotel.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ConsecutiveStayDiscountStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(ConsecutiveStayDiscountStrategy.class);
    private static final int ORDER = 4;

    @Override
    public void apply(PriceContext context) {
        int days = context.getReserveDays();
        BigDecimal discountRate = BigDecimal.ONE;

        if (days >= Constants.CONSECUTIVE_STAY_DISCOUNT_THRESHOLD_2) {
            discountRate = BigDecimal.valueOf(Constants.CONSECUTIVE_STAY_DISCOUNT_RATE_2);
            log.debug("连住优惠: 入住{}天, 享受9折优惠", days);
        } else if (days >= Constants.CONSECUTIVE_STAY_DISCOUNT_THRESHOLD_1) {
            discountRate = BigDecimal.valueOf(Constants.CONSECUTIVE_STAY_DISCOUNT_RATE_1);
            log.debug("连住优惠: 入住{}天, 享受95折优惠", days);
        } else {
            log.debug("连住优惠: 入住{}天, 不满足优惠条件", days);
        }

        if (discountRate.compareTo(BigDecimal.ONE) < 0) {
            BigDecimal newTotal = context.getTotalPrice().multiply(discountRate);
            context.setTotalPrice(newTotal);
            context.setConsecutiveStayDiscount(discountRate);
            log.debug("连住优惠策略应用: 折扣率={}, 调整后总价={}", discountRate, newTotal);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
