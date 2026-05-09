package com.hotel.util;

import com.hotel.common.Constants;
import com.hotel.entity.SysRoom;
import com.hotel.vo.PriceCalculationVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 价格计算工具类
 */
public class PriceCalculator {

    // 季节系数常量
    private static final BigDecimal SPRING_COEFFICIENT = new BigDecimal("1.0");  // 春季（3-5月）
    private static final BigDecimal SUMMER_COEFFICIENT = new BigDecimal("1.2");  // 夏季（6-8月）旺季
    private static final BigDecimal AUTUMN_COEFFICIENT = new BigDecimal("1.0");  // 秋季（9-11月）
    private static final BigDecimal WINTER_COEFFICIENT = new BigDecimal("0.8");  // 冬季（12-2月）淡季

    /**
     * 计算预订价格
     * 定价规则：
     * - 周一至周五：基础价 × 季节系数 × 0.5
     * - 周六周日：基础价 × 季节系数 × 1.0
     * - 季节系数优先使用房间配置值，未配置则根据月份自动计算
     */
    public static PriceCalculationVO calculate(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate) {
        PriceCalculationVO vo = new PriceCalculationVO();
        vo.setRoomId(room.getId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setCheckInDate(checkInDate);
        vo.setCheckOutDate(checkOutDate);
        vo.setBasePrice(room.getBasePrice());

        // 计算天数
        int days = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        vo.setReserveDays(days);

        // 计算总价（逐天累加）
        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDate currentDate = checkInDate;
        // 记录使用的季节系数（优先使用房间配置值，否则使用入住日期的季节系数）
        BigDecimal seasonCoefficient = room.getSeasonCoefficient();
        if (seasonCoefficient == null) {
            seasonCoefficient = getSeasonCoefficient(checkInDate.getMonthValue());
        }
        
        while (currentDate.isBefore(checkOutDate)) {
            // 获取当天的季节系数：优先使用房间配置值，否则根据当天月份确定
            BigDecimal daySeasonCoefficient = room.getSeasonCoefficient();
            if (daySeasonCoefficient == null) {
                daySeasonCoefficient = getSeasonCoefficient(currentDate.getMonthValue());
            }
            BigDecimal dayPrice = calculateDayPrice(room.getBasePrice(), currentDate, daySeasonCoefficient);
            totalPrice = totalPrice.add(dayPrice);
            currentDate = currentDate.plusDays(1);
        }

        vo.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        vo.setSeasonCoefficient(seasonCoefficient);

        // 计算定金（总费用 × 10%，四舍五入保留2位小数）
        BigDecimal deposit = totalPrice.multiply(BigDecimal.valueOf(Constants.DEPOSIT_RATE))
                .setScale(2, RoundingMode.HALF_UP);
        vo.setDeposit(deposit);

        return vo;
    }

    /**
     * 计算单日价格
     * @param basePrice 基础价格
     * @param date 日期
     * @param seasonCoefficient 季节系数
     */
    private static BigDecimal calculateDayPrice(BigDecimal basePrice, LocalDate date, BigDecimal seasonCoefficient) {
        // 获取工作日/周末系数
        // weekday: 1(Mon) - 5(Fri) → 0.5折扣
        // weekend: 6(Sat) - 7(Sun) → 全价
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        double timeRate;
        if (dayOfWeek.getValue() >= 1 && dayOfWeek.getValue() <= 5) {
            timeRate = Constants.WEEKDAY_RATE; // 0.5
        } else {
            timeRate = Constants.WEEKEND_RATE; // 1.0
        }

        return basePrice.multiply(seasonCoefficient)
                .multiply(BigDecimal.valueOf(timeRate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取实际使用的季节系数
     * 优先使用房间配置值，未配置（null）则根据月份自动计算
     */
    public static BigDecimal getEffectiveSeasonCoefficient(BigDecimal roomCoefficient, int month) {
        if (roomCoefficient != null) {
            return roomCoefficient;
        }
        return getSeasonCoefficient(month);
    }

    /**
     * 获取季节系数（根据月份自动区分春夏秋冬）
     * 春季（3-5月）：1.0
     * 夏季（6-8月）：1.2（旺季）
     * 秋季（9-11月）：1.0
     * 冬季（12-2月）：0.8（淡季）
     */
    public static BigDecimal getSeasonCoefficient(int month) {
        return switch (month) {
            case 3, 4, 5 -> SPRING_COEFFICIENT;   // 春季
            case 6, 7, 8 -> SUMMER_COEFFICIENT;   // 夏季（旺季）
            case 9, 10, 11 -> AUTUMN_COEFFICIENT; // 秋季
            case 12, 1, 2 -> WINTER_COEFFICIENT;  // 冬季（淡季）
            default -> BigDecimal.ONE;
        };
    }

    /**
     * 获取季节名称
     */
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
