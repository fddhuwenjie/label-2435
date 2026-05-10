package com.hotel.pricing;

import com.hotel.entity.SysRoom;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class SeasonPricingStrategy implements PricingStrategy {

    private static final BigDecimal SPRING_COEFFICIENT = new BigDecimal("1.0");
    private static final BigDecimal SUMMER_COEFFICIENT = new BigDecimal("1.2");
    private static final BigDecimal AUTUMN_COEFFICIENT = new BigDecimal("1.0");
    private static final BigDecimal WINTER_COEFFICIENT = new BigDecimal("0.8");

    @Override
    public PricingContext apply(PricingContext context) {
        SysRoom room = context.getRoom();
        BigDecimal roomCoefficient = room.getSeasonCoefficient();

        Map<LocalDate, BigDecimal> dayPrices = new LinkedHashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDate currentDate = context.getCheckInDate();

        while (currentDate.isBefore(context.getCheckOutDate())) {
            BigDecimal seasonCoefficient = roomCoefficient != null
                    ? roomCoefficient
                    : getSeasonCoefficient(currentDate.getMonthValue());
            BigDecimal dayPrice = context.getBasePrice().multiply(seasonCoefficient)
                    .setScale(2, RoundingMode.HALF_UP);
            dayPrices.put(currentDate, dayPrice);
            totalPrice = totalPrice.add(dayPrice);
            currentDate = currentDate.plusDays(1);
        }

        BigDecimal effectiveCoefficient = roomCoefficient != null
                ? roomCoefficient
                : getSeasonCoefficient(context.getCheckInDate().getMonthValue());

        context.setDayPrices(dayPrices);
        context.setCurrentPrice(totalPrice);
        context.setSeasonCoefficient(effectiveCoefficient);
        context.setSeasonName(getSeasonName(context.getCheckInDate().getMonthValue()));

        log.debug("季节定价: seasonCoefficient={}, totalPrice={}", effectiveCoefficient, totalPrice);
        return context;
    }

    public static BigDecimal getSeasonCoefficient(int month) {
        return switch (month) {
            case 3, 4, 5 -> SPRING_COEFFICIENT;
            case 6, 7, 8 -> SUMMER_COEFFICIENT;
            case 9, 10, 11 -> AUTUMN_COEFFICIENT;
            case 12, 1, 2 -> WINTER_COEFFICIENT;
            default -> BigDecimal.ONE;
        };
    }

    public static String getSeasonName(int month) {
        return switch (month) {
            case 3, 4, 5 -> "春季";
            case 6, 7, 8 -> "夏季";
            case 9, 10, 11 -> "秋季";
            case 12, 1, 2 -> "冬季";
            default -> "未知";
        };
    }
}
