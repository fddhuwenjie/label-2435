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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "节假日管理", description = "节假日配置管理接口")
@RestController
@RequestMapping("/admin/holidays")
@RequiredArgsConstructor
@Validated
public class HolidayController {

    private final SysHolidayService holidayService;

    @Operation(summary = "分页查询节假日")
    @GetMapping
    public Result<PageResult<SysHoliday>> listHolidays(HolidayQueryRequest request) {
        return Result.success(holidayService.pageHolidays(request));
    }

    @Operation(summary = "获取节假日详情")
    @GetMapping("/{id}")
    public Result<SysHoliday> getHoliday(@PathVariable Long id) {
        return Result.success(holidayService.getById(id));
    }

    @Operation(summary = "创建节假日")
    @PostMapping
    public Result<SysHoliday> createHoliday(@Valid @RequestBody HolidayCreateRequest request) {
        return Result.success("创建成功", holidayService.createHoliday(request));
    }

    @Operation(summary = "更新节假日")
    @PutMapping("/{id}")
    public Result<Void> updateHoliday(@PathVariable Long id, @RequestBody HolidayUpdateRequest request) {
        holidayService.updateHoliday(id, request);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除节假日")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return Result.success("删除成功", null);
    }
}
