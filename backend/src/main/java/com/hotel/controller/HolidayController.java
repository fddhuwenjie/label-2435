package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.HolidayCreateRequest;
import com.hotel.dto.HolidayQueryRequest;
import com.hotel.dto.HolidayUpdateRequest;
import com.hotel.entity.SysHoliday;
import com.hotel.service.SysHolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "节假日管理", description = "节假日配置管理接口")
@RestController
@RequestMapping("/admin/holidays")
@RequiredArgsConstructor
@Validated
public class HolidayController {

    private final SysHolidayService holidayService;

    @Operation(summary = "分页查询节假日配置")
    @GetMapping
    public Result<PageResult<SysHoliday>> listHolidays(HolidayQueryRequest request) {
        PageResult<SysHoliday> result = holidayService.pageHolidays(request);
        return Result.success(result);
    }

    @Operation(summary = "获取节假日详情")
    @GetMapping("/{id}")
    public Result<SysHoliday> getHolidayDetail(@PathVariable Long id) {
        SysHoliday holiday = holidayService.getById(id);
        return Result.success(holiday);
    }

    @Operation(summary = "创建节假日配置")
    @PostMapping
    public Result<SysHoliday> createHoliday(@Valid @RequestBody HolidayCreateRequest request) {
        SysHoliday holiday = holidayService.createHoliday(request);
        return Result.success("创建成功", holiday);
    }

    @Operation(summary = "更新节假日配置")
    @PutMapping("/{id}")
    public Result<Void> updateHoliday(@PathVariable Long id, @Valid @RequestBody HolidayUpdateRequest request) {
        holidayService.updateHoliday(id, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除节假日配置")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return Result.success("删除成功", null);
    }
}
