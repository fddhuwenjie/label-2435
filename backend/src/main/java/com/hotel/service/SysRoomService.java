package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Constants;
import com.hotel.common.PageResult;
import com.hotel.dto.RoomQueryRequest;
import com.hotel.dto.RoomUpdateRequest;
import com.hotel.entity.SysRoom;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.SysReservationMapper;
import com.hotel.mapper.SysRoomMapper;
import com.hotel.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房间服务
 */
@Service
@RequiredArgsConstructor
public class SysRoomService extends ServiceImpl<SysRoomMapper, SysRoom> {

    private final SysReservationMapper reservationMapper;

    /**
     * 分页查询房间
     */
    public PageResult<RoomVO> pageRooms(RoomQueryRequest request) {
        Page<SysRoom> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysRoom> wrapper = new LambdaQueryWrapper<>();

        if (request.getRoomType() != null) {
            wrapper.eq(SysRoom::getRoomType, request.getRoomType());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysRoom::getStatus, request.getStatus());
        }
        wrapper.orderByAsc(SysRoom::getRoomNumber);

        Page<SysRoom> result = page(page, wrapper);
        List<RoomVO> records = result.getRecords().stream()
                .map(RoomVO::fromEntity)
                .collect(Collectors.toList());

        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), records);
    }

    /**
     * 查询可用房间（指定日期范围内未被预订的房间）
     */
    public PageResult<RoomVO> pageAvailableRooms(RoomQueryRequest request) {
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new BusinessException("请指定入住和退房日期");
        }
        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }

        // 先查询所有符合条件的房间
        LambdaQueryWrapper<SysRoom> wrapper = new LambdaQueryWrapper<>();
        if (request.getRoomType() != null) {
            wrapper.eq(SysRoom::getRoomType, request.getRoomType());
        }
        // 只查询空闲状态的房间
        wrapper.eq(SysRoom::getStatus, Constants.RoomStatus.FREE);
        wrapper.orderByAsc(SysRoom::getRoomNumber);

        List<SysRoom> allRooms = list(wrapper);

        // 过滤掉在指定日期范围内已被预订的房间
        List<SysRoom> availableRooms = allRooms.stream()
                .filter(room -> !isRoomReserved(room.getId(), request.getCheckInDate(), request.getCheckOutDate()))
                .collect(Collectors.toList());

        // 手动分页
        int total = availableRooms.size();
        int start = (request.getCurrent() - 1) * request.getSize();
        int end = Math.min(start + request.getSize(), total);

        List<RoomVO> records = availableRooms.subList(start, end).stream()
                .map(RoomVO::fromEntity)
                .collect(Collectors.toList());

        return new PageResult<>((long) request.getCurrent(), (long) request.getSize(), (long) total, records);
    }

    /**
     * 检查房间在指定日期范围内是否已被预订
     */
    public boolean isRoomReserved(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        return reservationMapper.countConflict(roomId, checkInDate, checkOutDate) > 0;
    }

    /**
     * 更新房间信息
     */
    @Transactional
    public void updateRoom(Long id, RoomUpdateRequest request) {
        SysRoom room = getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        if (request.getBasePrice() != null) {
            room.setBasePrice(request.getBasePrice());
        }
        if (request.getSeasonCoefficient() != null) {
            room.setSeasonCoefficient(request.getSeasonCoefficient());
        }
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        updateById(room);
    }

    /**
     * 批量更新季节系数
     */
    @Transactional
    public void batchUpdateSeasonCoefficient(Integer roomType, java.math.BigDecimal coefficient) {
        LambdaQueryWrapper<SysRoom> wrapper = new LambdaQueryWrapper<>();
        if (roomType != null) {
            wrapper.eq(SysRoom::getRoomType, roomType);
        }

        SysRoom updateRoom = new SysRoom();
        updateRoom.setSeasonCoefficient(coefficient);
        update(updateRoom, wrapper);
    }

    /**
     * 统计房间占用情况
     */
    public List<Map<String, Object>> countByTypeAndStatus() {
        return baseMapper.countByTypeAndStatus();
    }

    /**
     * 更新房间状态
     */
    @Transactional
    public void updateStatus(Long roomId, Integer status) {
        SysRoom room = getById(roomId);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        room.setStatus(status);
        updateById(room);
    }
}
