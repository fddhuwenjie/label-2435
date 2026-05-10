package com.hotel.util;

import com.hotel.common.Constants;
import com.hotel.entity.SysHoliday;
import com.hotel.entity.SysRoom;
import com.hotel.util.price.*;
import com.hotel.vo.PriceCalculationVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriceCalculator {

    private static final BigDecimal SPRING_COEFFICIENT = new BigDecimal("1.0");
    private static final BigDecimal SUMMER_COEFFICIENT = new BigDecimal("1.2");
    private static final BigDecimal AUTUMN_COEFFICIENT = new BigDecimal("1.0");
    private static final BigDecimal WINTER_COEFFICIENT = new BigDecimal("0.8");

    private static final List<PricingStrategy> STRATEGIES = new ArrayList<>();

    static {
        STRATEGIES.add(new SeasonPricingStrategy());
        STRATEGIES.add(new TimePeriodPricingStrategy());
        STRATEGIES.add(new HolidayPricingStrategy());
        STRATEGIES.add(new StayDiscountStrategy());
        STRATEGIES.add(new EarlyBirdDiscountStrategy());
        STRATEGIES.sort(Comparator.comparingInt(PricingStrategy::getOrder));
    }

    public static PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        return calculate(room, checkInDate, checkOutDate, LocalDate.now(), null);
    }

    public static PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate,
                                               LocalDate bookingDate, List<SysHoliday> holidays) {
        PriceContext context = new PriceContext();
        context.setRoom(room);
        context.setCheckInDate(checkInDate);
        context.setCheckOutDate(checkOutDate);
        context.setBookingDate(bookingDate != null ? bookingDate : LocalDate.now());
        context.setHolidays(holidays);

        BigDecimal baseTotal = null;
        for (PricingStrategy strategy : STRATEGIES) {
            strategy.apply(context);
            if (strategy instanceof TimePeriodPricingStrategy && context.getTotalPrice() != null) {
                baseTotal = context.getTotalPrice();
            }
        }

        PriceCalculationVO vo = new PriceCalculationVO();
        vo.setRoomId(room.getId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setCheckInDate(checkInDate);
        vo.setCheckOutDate(checkOutDate);
        vo.setReserveDays(context.getReserveDays());
        vo.setBasePrice(room.getBasePrice());
        vo.setSeasonCoefficient(context.getSeasonCoefficient());

        BigDecimal baseTotalForDisplay = baseTotal != null
                ? baseTotal.setScale(2, RoundingMode.HALF_UP)
                : calculateBaseTotal(room, checkInDate, checkOutDate, context.getSeasonCoefficient());
        vo.setOriginalTotalPrice(baseTotalForDisplay);

        BigDecimal finalTotal = context.getTotalPrice() != null
                ? context.getTotalPrice().setScale(2, RoundingMode.HALF_UP)
                : baseTotalForDisplay;
        vo.setTotalPrice(finalTotal);

        vo.setStayDiscount(calculateStayDiscountAmount(baseTotalForDisplay, context.getReserveDays()));
        vo.setEarlyBirdDiscount(calculateEarlyBirdDiscountAmount(baseTotalForDisplay, bookingDate, checkInDate));

        BigDecimal deposit = finalTotal.multiply(BigDecimal.valueOf(Constants.DEPOSIT_RATE))
                .setScale(2, RoundingMode.HALF_UP);
        vo.setDeposit(deposit);

        return vo;
    }

    private static BigDecimal calculateBaseTotal(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate,
                                                  BigDecimal seasonCoefficient) {
        BigDecimal total = BigDecimal.ZERO;
        LocalDate current = checkInDate;
        BigDecimal basePrice = room.getBasePrice();

        while (current.isBefore(checkOutDate)) {
            BigDecimal dayPrice = basePrice.multiply(seasonCoefficient);

            java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
            double timeRate;
            if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
                timeRate = Constants.WEEKDAY_RATE;
            } else {
                timeRate = Constants.WEEKEND_RATE;
            }
            dayPrice = dayPrice.multiply(BigDecimal.valueOf(timeRate));

            total = total.add(dayPrice);
            current = current.plusDays(1);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateStayDiscountAmount(BigDecimal baseTotal, int days) {
        BigDecimal rate = BigDecimal.ONE;

        if (days >= Constants.StayDiscount.MIN_DAYS_7) {
            rate = BigDecimal.valueOf(Constants.StayDiscount.RATE_7_DAYS);
        } else if (days >= Constants.StayDiscount.MIN_DAYS_3) {
            rate = BigDecimal.valueOf(Constants.StayDiscount.RATE_3_DAYS);
        }

        if (rate.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return baseTotal.multiply(BigDecimal.ONE.subtract(rate)).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateEarlyBirdDiscountAmount(BigDecimal baseTotal, LocalDate bookingDate,
                                                                LocalDate checkInDate) {
        LocalDate effectiveBookingDate = bookingDate != null ? bookingDate : LocalDate.now();
        long daysInAdvance = java.time.temporal.ChronoUnit.DAYS.between(effectiveBookingDate, checkInDate);
        BigDecimal rate = BigDecimal.ONE;

        if (daysInAdvance >= Constants.EarlyBirdDiscount.MIN_DAYS_30) {
            rate = BigDecimal.valueOf(Constants.EarlyBirdDiscount.RATE_30_DAYS);
        } else if (daysInAdvance >= Constants.EarlyBirdDiscount.MIN_DAYS_14) {
            rate = BigDecimal.valueOf(Constants.EarlyBirdDiscount.RATE_14_DAYS);
        }

        if (rate.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return baseTotal.multiply(BigDecimal.ONE.subtract(rate)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getEffectiveSeasonCoefficient(BigDecimal roomCoefficient, int month) {
        if (roomCoefficient != null) {
            return roomCoefficient;
        }
        return getSeasonCoefficient(month);
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
