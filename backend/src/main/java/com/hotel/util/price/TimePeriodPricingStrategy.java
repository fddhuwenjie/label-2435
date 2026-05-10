package com.hotel.util.price;

import com.hotel.common.Constants;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
public class TimePeriodPricingStrategy implements PricingStrategy {

    @Override
    public void apply(PriceContext context) {
        LocalDate checkInDate = context.getCheckInDate();
        LocalDate checkOutDate = context.getCheckOutDate();
        int days = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        context.setReserveDays(days);

        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDate currentDate = checkInDate;
        BigDecimal dailyBase = context.getDailyPrice() != null
                ? context.getDailyPrice()
                : context.getRoom().getBasePrice();

        while (currentDate.isBefore(checkOutDate)) {
            BigDecimal dayPrice = dailyBase;

            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            double timeRate;
            if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
                timeRate = Constants.WEEKDAY_RATE;
            } else {
                timeRate = Constants.WEEKEND_RATE;
            }
            dayPrice = dayPrice.multiply(BigDecimal.valueOf(timeRate));

            totalPrice = totalPrice.add(dayPrice);
            currentDate = currentDate.plusDays(1);
        }

        context.setTotalPrice(totalPrice);
        log.debug("时段定价策略应用: 基础总价={}", totalPrice);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
