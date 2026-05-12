package com.hotel.strategy;

import com.hotel.entity.SysHoliday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static com.hotel.util.PriceCalculator.getSeasonCoefficient;

@Component
public class HolidayPricingStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(HolidayPricingStrategy.class);
    private static final int ORDER = 3;

    @Override
    public void apply(PriceContext context) {
        Map<LocalDate, SysHoliday> holidayMap = context.getHolidayMap();
        if (holidayMap == null || holidayMap.isEmpty()) {
            log.debug("节假日定价策略跳过: 无节假日配置");
            return;
        }

        LocalDate checkInDate = context.getCheckInDate();
        LocalDate checkOutDate = context.getCheckOutDate();
        BigDecimal holidaySurchargeTotal = BigDecimal.ZERO;
        int holidayCount = 0;

        LocalDate currentDate = checkInDate;
        while (currentDate.isBefore(checkOutDate)) {
            SysHoliday holiday = holidayMap.get(currentDate);
            if (holiday != null) {
                BigDecimal seasonCoefficient = context.getRoom().getSeasonCoefficient();
                if (seasonCoefficient == null) {
                    seasonCoefficient = getSeasonCoefficient(currentDate.getMonthValue());
                }

                BigDecimal dayPrice = context.getBasePrice()
                        .multiply(seasonCoefficient);

                BigDecimal holidayCoefficient = holiday.getCoefficient();
                BigDecimal holidaySurcharge = dayPrice.multiply(holidayCoefficient.subtract(BigDecimal.ONE));
                holidaySurchargeTotal = holidaySurchargeTotal.add(holidaySurcharge);
                holidayCount++;
            }
            currentDate = currentDate.plusDays(1);
        }

        if (holidayCount > 0) {
            BigDecimal newTotal = context.getTotalPrice().add(holidaySurchargeTotal);
            context.setTotalPrice(newTotal);
            context.setHolidayRate(BigDecimal.ONE.add(
                    holidaySurchargeTotal.divide(context.getTotalPrice(), 4, java.math.RoundingMode.HALF_UP)
            ));
            log.debug("节假日定价策略应用: 节假日{}天, 加价总额={}, 调整后总价={}",
                    holidayCount, holidaySurchargeTotal, newTotal);
        } else {
            log.debug("节假日定价策略跳过: 入住期间无节假日");
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
