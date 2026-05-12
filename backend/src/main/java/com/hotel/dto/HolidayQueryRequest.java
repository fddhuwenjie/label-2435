package com.hotel.dto;

import java.time.LocalDate;

public class HolidayQueryRequest {

    private Integer current = 1;
    private Integer size = 10;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;

    public Integer getCurrent() { return current; }
    public void setCurrent(Integer current) { this.current = current; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
