package com.hotel.util;

import com.hotel.common.Constants;
import com.hotel.entity.SysRoom;
import com.hotel.pricing.*;
import com.hotel.service.SysHolidayService;
import com.hotel.vo.PriceCalculationVO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PriceCalculator {

    private final List<PricingStrategy> strategies;

    public PriceCalculator(SysHolidayService holidayService) {
        this.strategies = new ArrayList<>();
        this.strategies.add(new SeasonPricingStrategy());
        this.strategies.add(new TimePeriodPricingStrategy());
        this.strategies.add(new HolidayPricingStrategy(holidayService));
        this.strategies.add(new LongStayDiscountStrategy());
        this.strategies.add(new EarlyBirdDiscountStrategy());
    }

    public PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        return calculate(room, checkInDate, checkOutDate, LocalDate.now());
    }

    public PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate, LocalDate bookingDate) {
        int days = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        PricingContext context = PricingContext.builder()
                .room(room)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .bookingDate(bookingDate)
                .reserveDays(days)
                .basePrice(room.getBasePrice())
                .currentPrice(BigDecimal.ZERO)
                .build();

        for (PricingStrategy strategy : strategies) {
            context = strategy.apply(context);
        }

        return toVO(room, context);
    }

    private PriceCalculationVO toVO(SysRoom room, PricingContext context) {
        PriceCalculationVO vo = new PriceCalculationVO();
        vo.setRoomId(room.getId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setCheckInDate(context.getCheckInDate());
        vo.setCheckOutDate(context.getCheckOutDate());
        vo.setReserveDays(context.getReserveDays());
        vo.setBasePrice(context.getBasePrice());
        vo.setSeasonCoefficient(context.getSeasonCoefficient());
        vo.setSeasonName(context.getSeasonName());
        vo.setHoliday(context.isHoliday());
        vo.setHolidayCoefficient(context.getHolidayCoefficient());
        vo.setHolidayName(context.getHolidayName());
        vo.setLongStayDiscount(context.getLongStayDiscount());
        vo.setEarlyBirdDiscount(context.getEarlyBirdDiscount());
        vo.setTotalPrice(context.getCurrentPrice().setScale(2, RoundingMode.HALF_UP));

        BigDecimal deposit = context.getCurrentPrice()
                .multiply(Constants.DEPOSIT_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        vo.setDeposit(deposit);

        return vo;
    }

    public static BigDecimal getEffectiveSeasonCoefficient(BigDecimal roomCoefficient, int month) {
        if (roomCoefficient != null) {
            return roomCoefficient;
        }
        return SeasonPricingStrategy.getSeasonCoefficient(month);
    }

    public static BigDecimal getSeasonCoefficient(int month) {
        return SeasonPricingStrategy.getSeasonCoefficient(month);
    }

    public static String getSeasonName(int month) {
        return SeasonPricingStrategy.getSeasonName(month);
    }
}
