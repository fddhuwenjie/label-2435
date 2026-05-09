package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.SysRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 房间Mapper
 */
@Mapper
public interface SysRoomMapper extends BaseMapper<SysRoom> {

    /**
     * 按类型和状态统计房间数量
     */
    @Select("SELECT room_type as roomType, status, COUNT(*) as count FROM sys_room GROUP BY room_type, status")
    List<Map<String, Object>> countByTypeAndStatus();
}
