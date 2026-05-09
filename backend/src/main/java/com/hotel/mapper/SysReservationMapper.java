package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.SysReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预订Mapper
 */
@Mapper
public interface SysReservationMapper extends BaseMapper<SysReservation> {

    /**
     * 查询预订详情（包含用户和房间信息）
     */
    @Select("SELECT r.*, u.username, u.real_name as userRealName, u.phone as userPhone, " +
            "rm.room_number, rm.room_type, rm.base_price " +
            "FROM sys_reservation r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN sys_room rm ON r.room_id = rm.id " +
            "WHERE r.id = #{id}")
    SysReservation selectDetailById(@Param("id") Long id);

    /**
     * 查询指定时间范围内的预订
     */
    @Select("SELECT r.*, u.username, u.real_name as userRealName, u.phone as userPhone, " +
            "rm.room_number, rm.room_type, rm.base_price " +
            "FROM sys_reservation r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN sys_room rm ON r.room_id = rm.id " +
            "WHERE r.reserve_time >= #{startTime} AND r.reserve_time < #{endTime} " +
            "ORDER BY r.reserve_time DESC")
    List<SysReservation> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 检查房间在指定日期范围内是否已被预订
     * 只检查待入住(0)状态的预订，已取消(1)、已完成(2)、已违约(3)的不算冲突
     */
    @Select("SELECT COUNT(*) FROM sys_reservation " +
            "WHERE room_id = #{roomId} " +
            "AND status = 0 " +
            "AND ((check_in_date <= #{checkInDate} AND check_out_date > #{checkInDate}) " +
            "OR (check_in_date < #{checkOutDate} AND check_out_date >= #{checkOutDate}) " +
            "OR (check_in_date >= #{checkInDate} AND check_out_date <= #{checkOutDate}))")
    int countConflict(@Param("roomId") Long roomId, 
                      @Param("checkInDate") LocalDate checkInDate, 
                      @Param("checkOutDate") LocalDate checkOutDate);
}
