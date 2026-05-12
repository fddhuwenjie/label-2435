package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.HolidayCreateRequest;
import com.hotel.dto.HolidayQueryRequest;
import com.hotel.dto.HolidayUpdateRequest;
import com.hotel.entity.SysHoliday;
import com.hotel.service.SysHolidayService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/holiday")
@Validated
public class HolidayController {

    private static final Logger log = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    private SysHolidayService sysHolidayService;

    @PostMapping
    public Result<SysHoliday> create(@Valid @RequestBody HolidayCreateRequest request) {
        log.info("创建节假日: {}", request);
        SysHoliday holiday = sysHolidayService.create(request);
        return Result.success(holiday);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody HolidayUpdateRequest request) {
        log.info("更新节假日: id={}, {}", id, request);
        sysHolidayService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除节假日: id={}", id);
        sysHolidayService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SysHoliday> getById(@PathVariable Long id) {
        SysHoliday holiday = sysHolidayService.getHolidayById(id);
        return Result.success(holiday);
    }

    @GetMapping("/list")
    public Result<PageResult<SysHoliday>> list(@Valid HolidayQueryRequest request) {
        PageResult<SysHoliday> result = sysHolidayService.query(request);
        return Result.success(result);
    }
}
