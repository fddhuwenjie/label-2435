package com.hotel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 预订查询请求
 */
@Data
public class ReservationQueryRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 预订状态
     */
    private Integer status;

    /**
     * 开始日期
     */
    private LocalDate startTime;

    /**
     * 结束日期
     */
    private LocalDate endTime;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
