package com.hotel.controller;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.ReservationRequest;
import com.hotel.dto.RoomQueryRequest;
import com.hotel.dto.UserUpdateRequest;
import com.hotel.entity.SysReservation;
import com.hotel.entity.SysUser;
import com.hotel.service.SysReservationService;
import com.hotel.service.SysRoomService;
import com.hotel.service.SysUserService;
import com.hotel.util.SecurityUtils;
import com.hotel.vo.PriceCalculationVO;
import com.hotel.vo.ReservationVO;
import com.hotel.vo.RoomVO;
import com.hotel.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 用户控制器
 */
@Tag(name = "用户功能", description = "普通用户功能接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService userService;
    private final SysRoomService roomService;
    private final SysReservationService reservationService;

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userService.getById(userId);
        return Result.success(UserVO.fromEntity(user));
    }

    /**
     * 更新当前用户信息
     */
    @Operation(summary = "更新当前用户信息")
    @PutMapping("/info")
    public Result<Void> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 普通用户不能修改角色
        request.setRole(null);
        userService.updateUser(userId, request);
        return Result.success("更新成功", null);
    }

    /**
     * 查询房间列表
     */
    @Operation(summary = "查询房间列表")
    @GetMapping("/rooms")
    public Result<PageResult<RoomVO>> listRooms(RoomQueryRequest request) {
        PageResult<RoomVO> result = roomService.pageRooms(request);
        return Result.success(result);
    }

    /**
     * 查询可用房间
     */
    @Operation(summary = "查询可用房间")
    @GetMapping("/rooms/available")
    public Result<PageResult<RoomVO>> listAvailableRooms(RoomQueryRequest request) {
        PageResult<RoomVO> result = roomService.pageAvailableRooms(request);
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
     * 计算预订价格
     */
    @Operation(summary = "计算预订价格")
    @GetMapping("/price/calculate")
    public Result<PriceCalculationVO> calculatePrice(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        PriceCalculationVO result = reservationService.calculatePrice(roomId, checkInDate, checkOutDate);
        return Result.success(result);
    }

    /**
     * 创建预订
     */
    @Operation(summary = "创建预订")
    @PostMapping("/reservations")
    public Result<ReservationVO> createReservation(@Valid @RequestBody ReservationRequest request) {
        SysReservation reservation = reservationService.createReservation(request);
        return Result.success("预订成功，请支付定金", reservationService.getReservationDetail(reservation.getId()));
    }

    /**
     * 支付定金
     */
    @Operation(summary = "支付定金")
    @PostMapping("/reservations/{id}/pay")
    public Result<Void> payDeposit(@PathVariable Long id) {
        reservationService.payDeposit(id);
        return Result.success("定金支付成功", null);
    }

    /**
     * 取消预订
     */
    @Operation(summary = "取消预订")
    @PostMapping("/reservations/{id}/cancel")
    public Result<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return Result.success("取消成功", null);
    }

    /**
     * 查询我的预订
     */
    @Operation(summary = "查询我的预订")
    @GetMapping("/reservations")
    public Result<PageResult<ReservationVO>> listMyReservations(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        PageResult<ReservationVO> result = reservationService.pageMyReservations(current, size, status);
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
}
