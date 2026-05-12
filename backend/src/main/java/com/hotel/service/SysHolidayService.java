package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.PageResult;
import com.hotel.dto.HolidayCreateRequest;
import com.hotel.dto.HolidayQueryRequest;
import com.hotel.dto.HolidayUpdateRequest;
import com.hotel.entity.SysHoliday;
import com.hotel.mapper.SysHolidayMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysHolidayService extends ServiceImpl<SysHolidayMapper, SysHoliday> {

    private static final Logger log = LoggerFactory.getLogger(SysHolidayService.class);

    @Transactional(rollbackFor = Exception.class)
    public SysHoliday create(HolidayCreateRequest request) {
        SysHoliday existing = lambdaQuery()
                .eq(SysHoliday::getDate, request.getDate())
                .one();
        if (existing != null) {
            throw new RuntimeException("该日期已配置节假日");
        }

        SysHoliday holiday = new SysHoliday();
        BeanUtils.copyProperties(request, holiday);
        save(holiday);
        log.info("创建节假日成功: {} - {}", holiday.getDate(), holiday.getName());
        return holiday;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, HolidayUpdateRequest request) {
        SysHoliday holiday = getById(id);
        if (holiday == null) {
            throw new RuntimeException("节假日不存在");
        }

        if (request.getDate() != null && !request.getDate().equals(holiday.getDate())) {
            SysHoliday existing = lambdaQuery()
                    .eq(SysHoliday::getDate, request.getDate())
                    .ne(SysHoliday::getId, id)
                    .one();
            if (existing != null) {
                throw new RuntimeException("该日期已配置节假日");
            }
        }

        if (request.getDate() != null) {
            holiday.setDate(request.getDate());
        }
        if (request.getName() != null) {
            holiday.setName(request.getName());
        }
        if (request.getCoefficient() != null) {
            holiday.setCoefficient(request.getCoefficient());
        }
        updateById(holiday);
        log.info("更新节假日成功: id={}", id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
        log.info("删除节假日成功: id={}", id);
    }

    public SysHoliday getHolidayById(Long id) {
        return getById(id);
    }

    public PageResult<SysHoliday> query(HolidayQueryRequest request) {
        Page<SysHoliday> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<SysHoliday> wrapper = new LambdaQueryWrapper<>();
        if (request.getStartDate() != null) {
            wrapper.ge(SysHoliday::getDate, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            wrapper.le(SysHoliday::getDate, request.getEndDate());
        }
        if (request.getName() != null && !request.getName().isEmpty()) {
            wrapper.like(SysHoliday::getName, request.getName());
        }
        wrapper.orderByDesc(SysHoliday::getDate);

        Page<SysHoliday> result = page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    public Map<LocalDate, SysHoliday> getHolidayMap(LocalDate startDate, LocalDate endDate) {
        List<SysHoliday> holidays = lambdaQuery()
                .between(SysHoliday::getDate, startDate, endDate)
                .list();
        return holidays.stream()
                .collect(Collectors.toMap(SysHoliday::getDate, h -> h));
    }
}
