package com.hotel.strategy;

import com.hotel.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.hotel.util.PriceCalculator.getSeasonCoefficient;

@Component
public class WeekdayWeekendPricingStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(WeekdayWeekendPricingStrategy.class);
    private static final int ORDER = 2;

    @Override
    public void apply(PriceContext context) {
        LocalDate checkInDate = context.getCheckInDate();
        LocalDate checkOutDate = context.getCheckOutDate();
        int days = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        int weekdayCount = 0;
        int weekendCount = 0;
        LocalDate currentDate = checkInDate;

        while (currentDate.isBefore(checkOutDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
                weekdayCount++;
            } else {
                weekendCount++;
            }
            currentDate = currentDate.plusDays(1);
        }

        BigDecimal weekdayRate = BigDecimal.valueOf(Constants.WEEKDAY_RATE);
        BigDecimal weekendRate = BigDecimal.valueOf(Constants.WEEKEND_RATE);

        BigDecimal avgRate;
        if (days > 0) {
            BigDecimal totalRate = weekdayRate.multiply(BigDecimal.valueOf(weekdayCount))
                    .add(weekendRate.multiply(BigDecimal.valueOf(weekendCount)));
            avgRate = totalRate.divide(BigDecimal.valueOf(days), 4, java.math.RoundingMode.HALF_UP);
        } else {
            avgRate = BigDecimal.ONE;
        }

        BigDecimal newTotal = BigDecimal.ZERO;
        currentDate = checkInDate;

        while (currentDate.isBefore(checkOutDate)) {
            BigDecimal seasonCoefficient = context.getRoom().getSeasonCoefficient();
            if (seasonCoefficient == null) {
                seasonCoefficient = getSeasonCoefficient(currentDate.getMonthValue());
            }

            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            BigDecimal dayRate = (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5)
                    ? weekdayRate : weekendRate;

            BigDecimal dayPrice = context.getBasePrice()
                    .multiply(seasonCoefficient)
                    .multiply(dayRate);
            newTotal = newTotal.add(dayPrice);
            currentDate = currentDate.plusDays(1);
        }

        context.setTotalPrice(newTotal);
        context.setWeekdayWeekendRate(avgRate);

        log.debug("工作日/周末定价策略应用: 工作日{}天, 周末{}天, 调整后总价={}",
                weekdayCount, weekendCount, newTotal);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
