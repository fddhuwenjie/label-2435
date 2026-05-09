package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.*;
import com.hotel.entity.SysDepositRecord;
import com.hotel.entity.SysUser;
import com.hotel.service.*;
import com.hotel.vo.ReservationVO;
import com.hotel.vo.RoomVO;
import com.hotel.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Tag(name = "管理员功能", description = "管理员功能接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SysUserService userService;
    private final SysRoomService roomService;
    private final SysReservationService reservationService;
    private final SysDepositRecordService depositRecordService;
    private final ExportService exportService;

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/users")
    public Result<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer role) {
        PageResult<UserVO> result = userService.pageUsers(current, size, username, phone, role);
        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/users/{id}")
    public Result<UserVO> getUserDetail(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.success(UserVO.fromEntity(user));
    }

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping("/users")
    public Result<UserVO> createUser(@Valid @RequestBody RegisterRequest request,
                                     @RequestParam(required = false) Integer role) {
        SysUser user = userService.createUser(request, role);
        return Result.success("创建成功", UserVO.fromEntity(user));
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    // ==================== 房间管理 ====================

    /**
     * 分页查询房间
     */
    @Operation(summary = "分页查询房间")
    @GetMapping("/rooms")
    public Result<PageResult<RoomVO>> listRooms(RoomQueryRequest request) {
        PageResult<RoomVO> result = roomService.pageRooms(request);
        return Result.success(result);
    }

    /**
     * 获取房间详情
     */
    @Operation(summary = "获取房间详情")
    @GetMapping("/rooms/{id}")
    public Result<RoomVO> getRoomDetail(@PathVariable Long id) {
        return Result.success(RoomVO.fromEntity(roomService.getById(id)));
    }

    /**
     * 更新房间
     */
    @Operation(summary = "更新房间")
    @PutMapping("/rooms/{id}")
    public Result<Void> updateRoom(@PathVariable Long id, @RequestBody RoomUpdateRequest request) {
        roomService.updateRoom(id, request);
        return Result.success("更新成功", null);
    }

    /**
     * 批量更新季节系数
     */
    @Operation(summary = "批量更新季节系数")
    @PutMapping("/rooms/season-coefficient")
    public Result<Void> batchUpdateSeasonCoefficient(
            @RequestParam(required = false) Integer roomType,
            @RequestParam BigDecimal coefficient) {
        roomService.batchUpdateSeasonCoefficient(roomType, coefficient);
        return Result.success("更新成功", null);
    }

    /**
     * 房间占用统计
     */
    @Operation(summary = "房间占用统计")
    @GetMapping("/rooms/statistics")
    public Result<List<Map<String, Object>>> getRoomStatistics() {
        return Result.success(roomService.countByTypeAndStatus());
    }

    // ==================== 预订管理 ====================

    /**
     * 分页查询预订
     */
    @Operation(summary = "分页查询预订")
    @GetMapping("/reservations")
    public Result<PageResult<ReservationVO>> listReservations(ReservationQueryRequest request) {
        PageResult<ReservationVO> result = reservationService.pageReservations(request);
        return Result.success(result);
    }

    /**
     * 获取预订详情
     */
    @Operation(summary = "获取预订详情")
    @GetMapping("/reservations/{id}")
    public Result<ReservationVO> getReservationDetail(@PathVariable Long id) {
        return Result.success(reservationService.getReservationDetail(id));
    }

    /**
     * 强制取消预订
     */
    @Operation(summary = "强制取消预订")
    @PostMapping("/reservations/{id}/force-cancel")
    public Result<Void> forceCancelReservation(@PathVariable Long id, @RequestParam String remark) {
        reservationService.forceCancelReservation(id, remark);
        return Result.success("取消成功", null);
    }

    /**
     * 办理入住
     */
    @Operation(summary = "办理入住")
    @PostMapping("/reservations/{id}/check-in")
    public Result<Void> checkIn(@PathVariable Long id) {
        reservationService.checkIn(id);
        return Result.success("入住办理成功", null);
    }

    /**
     * 标记违约
     */
    @Operation(summary = "标记违约")
    @PostMapping("/reservations/{id}/breach")
    public Result<Void> markBreach(@PathVariable Long id) {
        reservationService.markBreach(id);
        return Result.success("已标记为违约", null);
    }

    // ==================== 定金管理 ====================

    /**
     * 分页查询定金记录
     */
    @Operation(summary = "分页查询定金记录")
    @GetMapping("/deposits")
    public Result<PageResult<SysDepositRecord>> listDepositRecords(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long reservationId,
            @RequestParam(required = false) Integer operateType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endTime) {
        LocalDateTime startDateTime = startTime != null ? startTime.atStartOfDay() : null;
        LocalDateTime endDateTime = endTime != null ? endTime.plusDays(1).atStartOfDay() : null;
        PageResult<SysDepositRecord> result = depositRecordService.pageRecords(
                current, size, reservationId, operateType, startDateTime, endDateTime);
        return Result.success(result);
    }

    /**
     * 定金收支统计
     */
    @Operation(summary = "定金收支统计")
    @GetMapping("/deposits/statistics")
    public Result<List<Map<String, Object>>> getDepositStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endTime) {
        LocalDateTime startDateTime = startTime.atStartOfDay();
        LocalDateTime endDateTime = endTime.plusDays(1).atStartOfDay();
        return Result.success(depositRecordService.sumByOperateType(startDateTime, endDateTime));
    }

    // ==================== 导出功能 ====================

    /**
     * 导出预订清单
     */
    @Operation(summary = "导出预订清单")
    @GetMapping("/export/reservations")
    public void exportReservations(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endTime,
            HttpServletResponse response) {
        try {
            LocalDateTime start = startTime.atStartOfDay();
            LocalDateTime end = endTime.atTime(23, 59, 59);
            String filePath = exportService.exportReservations(start, end);
            downloadFile(filePath, response);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出定金统计
     */
    @Operation(summary = "导出定金统计")
    @GetMapping("/export/deposits")
    public void exportDepositStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endTime,
            HttpServletResponse response) {
        try {
            LocalDateTime start = startTime.atStartOfDay();
            LocalDateTime end = endTime.atTime(23, 59, 59);
            String filePath = exportService.exportDepositStatistics(start, end);
            downloadFile(filePath, response);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    private void downloadFile(String filePath, HttpServletResponse response) throws Exception {
        File file = new File(filePath);
        String fileName = file.getName();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", 
                "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }
}
