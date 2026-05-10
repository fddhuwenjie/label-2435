package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Constants;
import com.hotel.common.PageResult;
import com.hotel.dto.ReservationQueryRequest;
import com.hotel.dto.ReservationRequest;
import com.hotel.entity.SysDepositRecord;
import com.hotel.entity.SysReservation;
import com.hotel.entity.SysRoom;
import com.hotel.entity.SysUser;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.SysReservationMapper;
import com.hotel.util.PriceCalculator;
import com.hotel.util.SecurityUtils;
import com.hotel.vo.PriceCalculationVO;
import com.hotel.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预订服务
 */
@Service
@RequiredArgsConstructor
public class SysReservationService extends ServiceImpl<SysReservationMapper, SysReservation> {

    private final SysRoomService roomService;
    private final SysUserService userService;
    private final SysDepositRecordService depositRecordService;
    private final PriceCalculator priceCalculator;

    /**
     * 计算预订价格（预览）
     */
    public PriceCalculationVO calculatePrice(Long roomId, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate) {
        SysRoom room = roomService.getById(roomId);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        if (!checkInDate.isBefore(checkOutDate)) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }

        return priceCalculator.calculate(room, checkInDate, checkOutDate);
    }

    /**
     * 创建预订
     */
    @Transactional
    public SysReservation createReservation(ReservationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 验证房间
        SysRoom room = roomService.getById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        // 验证日期
        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }

        // 检查房间是否可用
        if (roomService.isRoomReserved(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new BusinessException("该房间在所选日期范围内已被预订");
        }

        // 计算价格
        PriceCalculationVO priceVO = priceCalculator.calculate(room, request.getCheckInDate(), request.getCheckOutDate());

        // 创建预订
        SysReservation reservation = new SysReservation();
        reservation.setUserId(userId);
        reservation.setRoomId(request.getRoomId());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setReserveDays(priceVO.getReserveDays());
        reservation.setTotalPrice(priceVO.getTotalPrice());
        reservation.setDeposit(priceVO.getDeposit());
        reservation.setDepositStatus(Constants.DepositStatus.UNPAID);
        reservation.setReserveTime(LocalDateTime.now());
        reservation.setStatus(Constants.ReservationStatus.PENDING);

        save(reservation);
        return reservation;
    }

    /**
     * 支付定金
     */
    @Transactional
    public void payDeposit(Long reservationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysReservation reservation = getById(reservationId);

        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (!reservation.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new BusinessException("无权操作此预订");
        }
        if (reservation.getDepositStatus() != Constants.DepositStatus.UNPAID) {
            throw new BusinessException("定金已支付或已处理");
        }
        if (reservation.getStatus() != Constants.ReservationStatus.PENDING) {
            throw new BusinessException("预订状态不正确");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 更新定金状态和支付时间
        reservation.setDepositStatus(Constants.DepositStatus.PAID);
        reservation.setDepositPaidTime(now);
        updateById(reservation);

        // 更新房间状态为已预订
        roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.RESERVED);

        // 记录定金支付
        depositRecordService.createRecord(reservationId, reservation.getDeposit(), 
                Constants.DepositOperateType.PAY, "支付定金");
    }

    /**
     * 取消预订
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysReservation reservation = getById(reservationId);

        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (!reservation.getUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new BusinessException("无权操作此预订");
        }
        if (reservation.getStatus() != Constants.ReservationStatus.PENDING) {
            throw new BusinessException("只能取消待入住的预订");
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setCancelTime(now);
        reservation.setStatus(Constants.ReservationStatus.CANCELLED);

        // 判断是否在6小时内取消（从支付定金时间开始计算，精确到分钟）
        if (reservation.getDepositStatus() == Constants.DepositStatus.PAID) {
            LocalDateTime paidTime = reservation.getDepositPaidTime();
            if (paidTime != null) {
                // 使用分钟精确判断：6小时 = 360分钟
                long minutesSincePaid = Duration.between(paidTime, now).toMinutes();
                long cancelFreeMinutes = Constants.CANCEL_FREE_HOURS * 60L;
                
                if (minutesSincePaid < cancelFreeMinutes) {
                    // 严格6小时内取消（不含6小时整），全额退款
                    reservation.setDepositStatus(Constants.DepositStatus.REFUNDED);
                    depositRecordService.createRecord(reservationId, reservation.getDeposit(),
                            Constants.DepositOperateType.REFUND, "6小时内取消，全额退款");
                } else {
                    // 达到或超过6小时，定金不退
                    reservation.setDepositStatus(Constants.DepositStatus.FORFEITED);
                    depositRecordService.createRecord(reservationId, reservation.getDeposit(),
                            Constants.DepositOperateType.FORFEIT, "超过6小时取消，定金扣罚");
                }
            } else {
                // 兼容旧数据：如果没有支付时间记录，按预订时间计算
                long minutesSinceReserve = Duration.between(reservation.getReserveTime(), now).toMinutes();
                long cancelFreeMinutes = Constants.CANCEL_FREE_HOURS * 60L;
                
                if (minutesSinceReserve < cancelFreeMinutes) {
                    reservation.setDepositStatus(Constants.DepositStatus.REFUNDED);
                    depositRecordService.createRecord(reservationId, reservation.getDeposit(),
                            Constants.DepositOperateType.REFUND, "6小时内取消，全额退款");
                } else {
                    reservation.setDepositStatus(Constants.DepositStatus.FORFEITED);
                    depositRecordService.createRecord(reservationId, reservation.getDeposit(),
                            Constants.DepositOperateType.FORFEIT, "超过6小时取消，定金扣罚");
                }
            }
        }

        updateById(reservation);

        // 恢复房间状态为空闲
        roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.FREE);
    }

    /**
     * 强制取消预订（管理员）
     */
    @Transactional
    public void forceCancelReservation(Long reservationId, String remark) {
        SysReservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }

        reservation.setCancelTime(LocalDateTime.now());
        reservation.setStatus(Constants.ReservationStatus.CANCELLED);

        // 如果已支付定金，进行退款
        if (reservation.getDepositStatus() == Constants.DepositStatus.PAID) {
            reservation.setDepositStatus(Constants.DepositStatus.REFUNDED);
            depositRecordService.createRecord(reservationId, reservation.getDeposit(),
                    Constants.DepositOperateType.REFUND, "管理员强制取消：" + remark);
        }

        updateById(reservation);

        // 恢复房间状态为空闲
        roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.FREE);
    }

    /**
     * 办理入住（管理员）
     */
    @Transactional
    public void checkIn(Long reservationId) {
        SysReservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (reservation.getStatus() != Constants.ReservationStatus.PENDING) {
            throw new BusinessException("只能对待入住的预订办理入住");
        }

        reservation.setStatus(Constants.ReservationStatus.COMPLETED);
        updateById(reservation);

        // 更新房间状态为已入住
        roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.OCCUPIED);
    }

    /**
     * 标记违约（管理员）
     */
    @Transactional
    public void markBreach(Long reservationId) {
        SysReservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (reservation.getStatus() != Constants.ReservationStatus.PENDING) {
            throw new BusinessException("只能对待入住的预订标记违约");
        }

        reservation.setStatus(Constants.ReservationStatus.BREACHED);

        // 如果已支付定金，没收定金
        if (reservation.getDepositStatus() == Constants.DepositStatus.PAID) {
            reservation.setDepositStatus(Constants.DepositStatus.FORFEITED);
            depositRecordService.createRecord(reservationId, reservation.getDeposit(),
                    Constants.DepositOperateType.FORFEIT, "客户违约，定金没收");
        }

        updateById(reservation);

        // 恢复房间状态为空闲
        roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.FREE);
    }

    /**
     * 分页查询预订（用户）
     */
    public PageResult<ReservationVO> pageMyReservations(Integer current, Integer size, Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<SysReservation> page = new Page<>(current, size);
        LambdaQueryWrapper<SysReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysReservation::getUserId, userId);
        if (status != null) {
            wrapper.eq(SysReservation::getStatus, status);
        }
        wrapper.orderByDesc(SysReservation::getReserveTime);

        Page<SysReservation> result = page(page, wrapper);
        List<ReservationVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), records);
    }

    /**
     * 分页查询预订（管理员）
     */
    public PageResult<ReservationVO> pageReservations(ReservationQueryRequest request) {
        Page<SysReservation> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysReservation> wrapper = new LambdaQueryWrapper<>();

        if (request.getUserId() != null) {
            wrapper.eq(SysReservation::getUserId, request.getUserId());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysReservation::getStatus, request.getStatus());
        }
        if (request.getStartTime() != null) {
            // 开始日期的 00:00:00
            wrapper.ge(SysReservation::getReserveTime, request.getStartTime().atStartOfDay());
        }
        if (request.getEndTime() != null) {
            // 结束日期的次日 00:00:00（不包含）
            wrapper.lt(SysReservation::getReserveTime, request.getEndTime().plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(SysReservation::getReserveTime);

        Page<SysReservation> result = page(page, wrapper);
        List<ReservationVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), records);
    }

    /**
     * 查询指定时间范围内的预订
     */
    public List<SysReservation> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectByTimeRange(startTime, endTime);
    }

    /**
     * 转换为VO
     */
    private ReservationVO toVO(SysReservation reservation) {
        SysUser user = userService.getById(reservation.getUserId());
        SysRoom room = roomService.getById(reservation.getRoomId());
        reservation.setUser(user);
        reservation.setRoom(room);
        return ReservationVO.fromEntity(reservation);
    }

    /**
     * 获取预订详情
     */
    public ReservationVO getReservationDetail(Long id) {
        SysReservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        return toVO(reservation);
    }
}
