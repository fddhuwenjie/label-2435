package com.hotel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 房间查询请求
 */
@Data
public class RoomQueryRequest {

    /**
     * 房间类型：1=单人间，2=双人间
     */
    private Integer roomType;

    /**
     * 房间状态
     */
    private Integer status;

    /**
     * 入住日期（查询可用房间）
     */
    private LocalDate checkInDate;

    /**
     * 退房日期（查询可用房间）
     */
    private LocalDate checkOutDate;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
