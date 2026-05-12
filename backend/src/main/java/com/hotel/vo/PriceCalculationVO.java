package com.hotel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceCalculationVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long roomId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer reserveDays;
    private BigDecimal basePrice;
    private BigDecimal seasonCoefficient;
    private BigDecimal totalPrice;
    private BigDecimal deposit;
    private BigDecimal weekdayWeekendRate;
    private BigDecimal holidayRate;
    private BigDecimal consecutiveStayDiscount;
    private BigDecimal earlyBirdDiscount;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getReserveDays() { return reserveDays; }
    public void setReserveDays(Integer reserveDays) { this.reserveDays = reserveDays; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getSeasonCoefficient() { return seasonCoefficient; }
    public void setSeasonCoefficient(BigDecimal seasonCoefficient) { this.seasonCoefficient = seasonCoefficient; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }
    public BigDecimal getWeekdayWeekendRate() { return weekdayWeekendRate; }
    public void setWeekdayWeekendRate(BigDecimal weekdayWeekendRate) { this.weekdayWeekendRate = weekdayWeekendRate; }
    public BigDecimal getHolidayRate() { return holidayRate; }
    public void setHolidayRate(BigDecimal holidayRate) { this.holidayRate = holidayRate; }
    public BigDecimal getConsecutiveStayDiscount() { return consecutiveStayDiscount; }
    public void setConsecutiveStayDiscount(BigDecimal consecutiveStayDiscount) { this.consecutiveStayDiscount = consecutiveStayDiscount; }
    public BigDecimal getEarlyBirdDiscount() { return earlyBirdDiscount; }
    public void setEarlyBirdDiscount(BigDecimal earlyBirdDiscount) { this.earlyBirdDiscount = earlyBirdDiscount; }
}
