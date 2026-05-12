package com.hotel.strategy;

import com.hotel.entity.SysHoliday;
import com.hotel.entity.SysRoom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class PriceContext {

    private SysRoom room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime bookingTime;
    private Map<LocalDate, SysHoliday> holidayMap;
    private int reserveDays;
    private BigDecimal basePrice;
    private BigDecimal dayPrice;
    private BigDecimal totalPrice;
    private BigDecimal seasonCoefficient;
    private BigDecimal weekdayWeekendRate;
    private BigDecimal holidayRate;
    private BigDecimal consecutiveStayDiscount;
    private BigDecimal earlyBirdDiscount;

    public PriceContext(SysRoom room, LocalDate checkInDate, LocalDate checkOutDate,
                        LocalDateTime bookingTime, Map<LocalDate, SysHoliday> holidayMap) {
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingTime = bookingTime;
        this.holidayMap = holidayMap;
        this.basePrice = room.getBasePrice();
        this.totalPrice = BigDecimal.ZERO;
        this.seasonCoefficient = BigDecimal.ONE;
        this.weekdayWeekendRate = BigDecimal.ONE;
        this.holidayRate = BigDecimal.ONE;
        this.consecutiveStayDiscount = BigDecimal.ONE;
        this.earlyBirdDiscount = BigDecimal.ONE;
    }

    public SysRoom getRoom() { return room; }
    public void setRoom(SysRoom room) { this.room = room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
    public Map<LocalDate, SysHoliday> getHolidayMap() { return holidayMap; }
    public void setHolidayMap(Map<LocalDate, SysHoliday> holidayMap) { this.holidayMap = holidayMap; }
    public int getReserveDays() { return reserveDays; }
    public void setReserveDays(int reserveDays) { this.reserveDays = reserveDays; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getDayPrice() { return dayPrice; }
    public void setDayPrice(BigDecimal dayPrice) { this.dayPrice = dayPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public BigDecimal getSeasonCoefficient() { return seasonCoefficient; }
    public void setSeasonCoefficient(BigDecimal seasonCoefficient) { this.seasonCoefficient = seasonCoefficient; }
    public BigDecimal getWeekdayWeekendRate() { return weekdayWeekendRate; }
    public void setWeekdayWeekendRate(BigDecimal weekdayWeekendRate) { this.weekdayWeekendRate = weekdayWeekendRate; }
    public BigDecimal getHolidayRate() { return holidayRate; }
    public void setHolidayRate(BigDecimal holidayRate) { this.holidayRate = holidayRate; }
    public BigDecimal getConsecutiveStayDiscount() { return consecutiveStayDiscount; }
    public void setConsecutiveStayDiscount(BigDecimal consecutiveStayDiscount) { this.consecutiveStayDiscount = consecutiveStayDiscount; }
    public BigDecimal getEarlyBirdDiscount() { return earlyBirdDiscount; }
    public void setEarlyBirdDiscount(BigDecimal earlyBirdDiscount) { this.earlyBirdDiscount = earlyBirdDiscount; }
}
