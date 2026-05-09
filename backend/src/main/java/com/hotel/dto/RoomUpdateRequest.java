package com.hotel.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 房间更新请求
 */
@Data
public class RoomUpdateRequest {

    /**
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 季节系数
     */
    private BigDecimal seasonCoefficient;

    /**
     * 状态
     */
    private Integer status;
}
