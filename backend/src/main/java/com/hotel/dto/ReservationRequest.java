package com.hotel.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 预订请求
 */
@Data
public class ReservationRequest {

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "入住日期不能为空")
    @FutureOrPresent(message = "入住日期不能是过去的日期")
    private LocalDate checkInDate;

    @NotNull(message = "退房日期不能为空")
    @FutureOrPresent(message = "退房日期不能是过去的日期")
    private LocalDate checkOutDate;
}
