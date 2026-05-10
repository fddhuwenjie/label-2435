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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysHolidayService extends ServiceImpl<SysHolidayMapper, SysHoliday> {

    public PageResult<SysHoliday> pageHolidays(HolidayQueryRequest request) {
        Page<SysHoliday> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();

        if (request.getStartDate() != null) {
            wrapper.ge(SysHoliday::getDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(SysHoliday::getDate, request.getEndDate());
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            wrapper.like(SysHoliday::getName, request.getName());
        }
        wrapper.orderByAsc(SysHoliday::getDate);

        Page<SysHoliday> result = page(page, wrapper);
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
    }

    @Transactional
    public SysHoliday createHoliday(HolidayCreateRequest request) {
        long count = count(new LambdaQueryWrapper<SysHoliday>().eq(SysHoliday::getDate, request.getDate()));
        if (count > 0) {
            throw new BusinessException("该日期已配置为节假日");
        }

        SysHoliday holiday = new SysHoliday();
        holiday.setDate(request.getDate());
        holiday.setName(request.getName());
        holiday.setCoefficient(request.getCoefficient());
        save(holiday);

        log.info("创建节假日配置: date={}, name={}, coefficient={}", request.getDate(), request.getName(), request.getCoefficient());
        return holiday;
    }

    @Transactional
    public void updateHoliday(Long id, HolidayUpdateRequest request) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日配置不存在");
        }

        if (request.getDate() != null) {
            long count = count(new LambdaQueryWrapper<SysHoliday>()
                    .eq(SysHoliday::getDate, request.getDate())
                    .ne(SysHoliday::getId, id));
            if (count > 0) {
                throw new BusinessException("该日期已配置为节假日");
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
        log.info("更新节假日配置: id={}", id);
    }

    @Transactional
    public void deleteHoliday(Long id) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new BusinessException("节假日配置不存在");
        }
        removeById(id);
        log.info("删除节假日配置: id={}, date={}", id, holiday.getDate());
    }

    public List<SysHoliday> getHolidaysByDates(List<java.time.LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<SysHoliday>().in(SysHoliday::getDate, dates));
    }
}
