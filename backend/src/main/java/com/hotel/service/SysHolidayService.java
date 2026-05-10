package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.PageResult;
import com.hotel.dto.HolidayCreateRequest;
import com.hotel.dto.HolidayQueryRequest;
import com.hotel.dto.HolidayUpdateRequest;
import com.hotel.entity.SysHoliday;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.SysHolidayMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysHolidayService extends ServiceImpl<SysHolidayMapper, SysHoliday> {

    private final SysHolidayMapper holidayMapper;

    public PageResult<SysHoliday> pageHolidays(HolidayQueryRequest request) {
        Page<SysHoliday> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();

        if (request.getStartDate() != null) {
            wrapper.ge(SysHoliday::getDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(SysHoliday::getDate, request.getEndDate());
        }
        wrapper.orderByAsc(SysHoliday::getDate);

        Page<SysHoliday> result = page(page, wrapper);
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    public List<SysHoliday> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return holidayMapper.findByDateRange(startDate, endDate);
    }

    public SysHoliday findByDate(LocalDate date) {
        return holidayMapper.findByDate(date);
    }

    @Transactional
    public SysHoliday createHoliday(HolidayCreateRequest request) {
        SysHoliday existing = findByDate(request.getDate());
        if (existing != null) {
            throw new BusinessException("该日期已配置节假日");
        }

        SysHoliday holiday = new SysHoliday();
        holiday.setDate(request.getDate());
        holiday.setName(request.getName());
        holiday.setCoefficient(request.getCoefficient());
        save(holiday);
        return holiday;
    }

    @Transactional
    public void updateHoliday(Long id, HolidayUpdateRequest request) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日不存在");
        }

        if (request.getDate() != null && !request.getDate().equals(holiday.getDate())) {
            SysHoliday existing = findByDate(request.getDate());
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException("该日期已配置节假日");
            }
            holiday.setDate(request.getDate());
        }

        if (request.getName() != null) {
            holiday.setName(request.getName());
        }
        if (request.getCoefficient() != null) {
            holiday.setCoefficient(request.getCoefficient());
        }

        updateById(holiday);
    }

    @Transactional
    public void deleteHoliday(Long id) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日不存在");
        }
        removeById(id);
    }
}
