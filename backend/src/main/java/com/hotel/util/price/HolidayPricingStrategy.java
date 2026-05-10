package com.hotel.util.price;

import com.hotel.common.Constants;
import com.hotel.entity.SysHoliday;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HolidayPricingStrategy implements PricingStrategy {

    @Override
    public void apply(PriceContext context) {
        if (context.getHolidays() == null || context.getHolidays().isEmpty()) {
            log.debug("节假日定价策略: 无节假日配置，跳过");
            return;
        }

        Map<LocalDate, SysHoliday> holidayMap = context.getHolidays().stream()
                .collect(Collectors.toMap(SysHoliday::getDate, h -> h));
        context.setHolidayMap(holidayMap);

        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDate currentDate = context.getCheckInDate();
        BigDecimal dailyBase = context.getDailyPrice() != null
                ? context.getDailyPrice()
                : context.getRoom().getBasePrice();

        while (currentDate.isBefore(context.getCheckOutDate())) {
            BigDecimal dayPrice = dailyBase;

            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            double timeRate;
            if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
                timeRate = Constants.WEEKDAY_RATE;
            } else {
                timeRate = Constants.WEEKEND_RATE;
            }
            dayPrice = dayPrice.multiply(BigDecimal.valueOf(timeRate));

            SysHoliday holiday = holidayMap.get(currentDate);
            if (holiday != null && holiday.getCoefficient() != null) {
                dayPrice = dayPrice.multiply(holiday.getCoefficient());
            }

            totalPrice = totalPrice.add(dayPrice);
            currentDate = currentDate.plusDays(1);
        }

        context.setTotalPrice(totalPrice);
        log.debug("节假日定价策略应用: 节假日调整后总价={}", totalPrice);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
