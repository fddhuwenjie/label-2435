package com.hotel.strategy;

import com.hotel.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class EarlyBirdDiscountStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(EarlyBirdDiscountStrategy.class);
    private static final int ORDER = 5;

    @Override
    public void apply(PriceContext context) {
        LocalDateTime bookingTime = context.getBookingTime();
        if (bookingTime == null) {
            log.debug("早鸟优惠: 未提供预订时间, 跳过优惠计算");
            return;
        }

        LocalDateTime checkInDateTime = context.getCheckInDate().atStartOfDay();
        long daysInAdvance = ChronoUnit.DAYS.between(bookingTime, checkInDateTime);

        BigDecimal discountRate = BigDecimal.ONE;

        if (daysInAdvance >= Constants.EARLY_BIRD_DISCOUNT_THRESHOLD_2) {
            discountRate = BigDecimal.valueOf(Constants.EARLY_BIRD_DISCOUNT_RATE_2);
            log.debug("早鸟优惠: 提前{}天预订, 享受9折优惠", daysInAdvance);
        } else if (daysInAdvance >= Constants.EARLY_BIRD_DISCOUNT_THRESHOLD_1) {
            discountRate = BigDecimal.valueOf(Constants.EARLY_BIRD_DISCOUNT_RATE_1);
            log.debug("早鸟优惠: 提前{}天预订, 享受95折优惠", daysInAdvance);
        } else {
            log.debug("早鸟优惠: 提前{}天预订, 不满足优惠条件", daysInAdvance);
        }

        if (discountRate.compareTo(BigDecimal.ONE) < 0) {
            BigDecimal newTotal = context.getTotalPrice().multiply(discountRate);
            context.setTotalPrice(newTotal);
            context.setEarlyBirdDiscount(discountRate);
            log.debug("早鸟优惠策略应用: 折扣率={}, 调整后总价={}", discountRate, newTotal);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
