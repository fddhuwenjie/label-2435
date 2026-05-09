package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.PageResult;
import com.hotel.entity.SysDepositRecord;
import com.hotel.mapper.SysDepositRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 定金记录服务
 */
@Service
@RequiredArgsConstructor
public class SysDepositRecordService extends ServiceImpl<SysDepositRecordMapper, SysDepositRecord> {

    /**
     * 创建定金记录
     */
    @Transactional
    public void createRecord(Long reservationId, BigDecimal amount, Integer operateType, String remark) {
        SysDepositRecord record = new SysDepositRecord();
        record.setReservationId(reservationId);
        record.setAmount(amount);
        record.setOperateType(operateType);
        record.setOperateTime(LocalDateTime.now());
        record.setRemark(remark);
        save(record);
    }

    /**
     * 分页查询定金记录
     */
    public PageResult<SysDepositRecord> pageRecords(Integer current, Integer size, 
                                                     Long reservationId, Integer operateType,
                                                     LocalDateTime startTime, LocalDateTime endTime) {
        Page<SysDepositRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<SysDepositRecord> wrapper = new LambdaQueryWrapper<>();

        if (reservationId != null) {
            wrapper.eq(SysDepositRecord::getReservationId, reservationId);
        }
        if (operateType != null) {
            wrapper.eq(SysDepositRecord::getOperateType, operateType);
        }
        if (startTime != null) {
            wrapper.ge(SysDepositRecord::getOperateTime, startTime);
        }
        if (endTime != null) {
            wrapper.lt(SysDepositRecord::getOperateTime, endTime);
        }
        wrapper.orderByDesc(SysDepositRecord::getOperateTime);

        Page<SysDepositRecord> result = page(page, wrapper);
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    /**
     * 统计指定时间范围内的收支
     */
    public List<Map<String, Object>> sumByOperateType(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.sumByOperateType(startTime, endTime);
    }

    /**
     * 查询指定时间范围内的记录
     */
    public List<SysDepositRecord> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<SysDepositRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(SysDepositRecord::getOperateTime, startTime);
        wrapper.lt(SysDepositRecord::getOperateTime, endTime);
        wrapper.orderByDesc(SysDepositRecord::getOperateTime);
        return list(wrapper);
    }
}
