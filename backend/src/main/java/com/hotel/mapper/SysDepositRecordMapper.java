package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.SysDepositRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 定金记录Mapper
 */
@Mapper
public interface SysDepositRecordMapper extends BaseMapper<SysDepositRecord> {

    /**
     * 统计指定时间范围内的收支
     */
    @Select("SELECT operate_type as operateType, SUM(amount) as totalAmount " +
            "FROM sys_deposit_record " +
            "WHERE operate_time >= #{startTime} AND operate_time < #{endTime} " +
            "GROUP BY operate_type")
    List<Map<String, Object>> sumByOperateType(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);
}
