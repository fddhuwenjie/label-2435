package com.hotel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hotel.entity.SysRoom;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房间视图对象
 */
@Data
public class RoomVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String roomNumber;
    private Integer roomType;
    private String roomTypeName;
    private BigDecimal basePrice;
    private BigDecimal seasonCoefficient;
    private Integer status;
    private String statusName;

    public static RoomVO fromEntity(SysRoom room) {
        RoomVO vo = new RoomVO();
        vo.setId(room.getId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setRoomType(room.getRoomType());
        vo.setRoomTypeName(room.getRoomType() == 1 ? "单人间" : "双人间");
        vo.setBasePrice(room.getBasePrice());
        vo.setSeasonCoefficient(room.getSeasonCoefficient());
        vo.setStatus(room.getStatus());
        vo.setStatusName(getStatusName(room.getStatus()));
        return vo;
    }

    private static String getStatusName(Integer status) {
        return switch (status) {
            case 0 -> "空闲";
            case 1 -> "已预订";
            case 2 -> "已入住";
            case 3 -> "维护";
            default -> "未知";
        };
    }
}
