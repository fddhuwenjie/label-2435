package com.hotel.util;

import com.hotel.common.Constants;
import com.hotel.entity.SysHoliday;
import com.hotel.entity.SysRoom;
import com.hotel.strategy.*;
import com.hotel.vo.PriceCalculationVO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class PriceCalculator {

    private static final Logger log = LoggerFactory.getLogger(PriceCalculator.class);

    @Autowired
    private SeasonPricingStrategy seasonPricingStrategy;
    @Autowired
    private WeekdayWeekendPricingStrategy weekdayWeekendPricingStrategy;
    @Autowired
    private HolidayPricingStrategy holidayPricingStrategy;
    @Autowired
    private ConsecutiveStayDiscountStrategy consecutiveStayDiscountStrategy;
    @Autowired
    private EarlyBirdDiscountStrategy earlyBirdDiscountStrategy;

    private List<PricingStrategy> strategies;

    @PostConstruct
    public void init() {
        strategies = new ArrayList<>();
        strategies.add(seasonPricingStrategy);
        strategies.add(weekdayWeekendPricingStrategy);
        strategies.add(holidayPricingStrategy);
        strategies.add(consecutiveStayDiscountStrategy);
        strategies.add(earlyBirdDiscountStrategy);
        strategies.sort(Comparator.comparingInt(PricingStrategy::getOrder));
        log.info("定价策略链初始化完成，共{}个策略", strategies.size());
    }

    private static final BigDecimal SPRING_COEFFICIENT = new BigDecimal("1.0");
    private static final BigDecimal SUMMER_COEFFICIENT = new BigDecimal("1.2");
    private static final BigDecimal AUTUMN_COEFFICIENT = new BigDecimal("1.0");
    private static final BigDecimal WINTER_COEFFICIENT = new BigDecimal("0.8");

    public PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        return calculate(room, checkInDate, checkOutDate, null, null);
    }

    public PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate,
                                        LocalDateTime bookingTime, Map<LocalDate, SysHoliday> holidayMap) {
        PriceContext context = new PriceContext(room, checkInDate, checkOutDate, bookingTime, holidayMap);

        for (PricingStrategy strategy : strategies) {
            strategy.apply(context);
        }

        PriceCalculationVO vo = new PriceCalculationVO();
        vo.setRoomId(room.getId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setCheckInDate(checkInDate);
        vo.setCheckOutDate(checkOutDate);
        vo.setReserveDays(context.getReserveDays());
        vo.setBasePrice(room.getBasePrice());
        vo.setSeasonCoefficient(context.getSeasonCoefficient());
        vo.setWeekdayWeekendRate(context.getWeekdayWeekendRate());
        vo.setHolidayRate(context.getHolidayRate());
        vo.setConsecutiveStayDiscount(context.getConsecutiveStayDiscount());
        vo.setEarlyBirdDiscount(context.getEarlyBirdDiscount());

        BigDecimal totalPrice = context.getTotalPrice().setScale(2, RoundingMode.HALF_UP);
        vo.setTotalPrice(totalPrice);

        BigDecimal deposit = totalPrice.multiply(BigDecimal.valueOf(Constants.DEPOSIT_RATE))
                .setScale(2, RoundingMode.HALF_UP);
        vo.setDeposit(deposit);

        log.info("价格计算完成: 房间={}, 入住={}, 退房={}, 天数={}, 总价={}, 定金={}",
                room.getRoomNumber(), checkInDate, checkOutDate, context.getReserveDays(),
                totalPrice, deposit);

        return vo;
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
