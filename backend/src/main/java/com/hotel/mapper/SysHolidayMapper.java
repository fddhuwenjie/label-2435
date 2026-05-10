package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.SysHoliday;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SysHolidayMapper extends BaseMapper<SysHoliday> {

    @Select("SELECT * FROM sys_holiday WHERE date BETWEEN #{startDate} AND #{endDate}")
    List<SysHoliday> findByDateRange(LocalDate startDate, LocalDate endDate);

    @Select("SELECT * FROM sys_holiday WHERE date = #{date} LIMIT 1")
    SysHoliday findByDate(LocalDate date);
}
