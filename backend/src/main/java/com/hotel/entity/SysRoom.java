package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间实体类
 */
@Data
@TableName("sys_room")
public class SysRoom {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 房间号（如：单001、双056）
     */
    private String roomNumber;

    /**
     * 房间类型：1=单人间，2=双人间
     */
    private Integer roomType;

    /**
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 季节系数
     */
    private BigDecimal seasonCoefficient;

    /**
     * 状态：0=空闲，1=已预订，2=已入住，3=维护
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
