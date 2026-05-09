package com.hotel.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.hotel.entity.SysDepositRecord;
import com.hotel.entity.SysReservation;
import com.hotel.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    @Value("${export.path:/app/exports}")
    private String exportPath;

    private final SysReservationService reservationService;
    private final SysDepositRecordService depositRecordService;

    /**
     * 导出预订清单
     */
    public String exportReservations(LocalDateTime startTime, LocalDateTime endTime) {
        List<SysReservation> reservations = reservationService.getByTimeRange(startTime, endTime);
        List<ReservationExportData> exportData = reservations.stream()
                .map(this::toExportData)
                .collect(Collectors.toList());

        String fileName = String.format("预订清单_%s_%s.xlsx",
                startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String filePath = exportPath + File.separator + fileName;

        // 确保目录存在
        File dir = new File(exportPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        EasyExcel.write(filePath, ReservationExportData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("预订清单")
                .doWrite(exportData);

        log.info("导出预订清单成功: {}", filePath);
        return filePath;
    }

    /**
     * 导出定金统计报表
     */
    public String exportDepositStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<SysDepositRecord> records = depositRecordService.getByTimeRange(startTime, endTime);
        List<DepositExportData> exportData = records.stream()
                .map(this::toDepositExportData)
                .collect(Collectors.toList());

        String fileName = String.format("定金统计_%s_%s.xlsx",
                startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String filePath = exportPath + File.separator + fileName;

        // 确保目录存在
        File dir = new File(exportPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        EasyExcel.write(filePath, DepositExportData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("定金统计")
                .doWrite(exportData);

        log.info("导出定金统计成功: {}", filePath);
        return filePath;
    }

    private ReservationExportData toExportData(SysReservation reservation) {
        ReservationExportData data = new ReservationExportData();
        data.setId(reservation.getId());
        data.setUserId(reservation.getUserId());
        data.setRoomId(reservation.getRoomId());
        data.setCheckInDate(reservation.getCheckInDate().toString());
        data.setCheckOutDate(reservation.getCheckOutDate().toString());
        data.setReserveDays(reservation.getReserveDays());
        data.setTotalPrice(reservation.getTotalPrice().toString());
        data.setDeposit(reservation.getDeposit().toString());
        data.setDepositStatus(getDepositStatusName(reservation.getDepositStatus()));
        data.setReserveTime(reservation.getReserveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        data.setStatus(getStatusName(reservation.getStatus()));
        return data;
    }

    private DepositExportData toDepositExportData(SysDepositRecord record) {
        DepositExportData data = new DepositExportData();
        data.setId(record.getId());
        data.setReservationId(record.getReservationId());
        data.setAmount(record.getAmount().toString());
        data.setOperateType(getOperateTypeName(record.getOperateType()));
        data.setOperateTime(record.getOperateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        data.setRemark(record.getRemark());
        return data;
    }

    private String getDepositStatusName(Integer status) {
        return switch (status) {
            case 0 -> "未支付";
            case 1 -> "已支付";
            case 2 -> "已退款";
            case 3 -> "已扣罚";
            default -> "未知";
        };
    }

    private String getStatusName(Integer status) {
        return switch (status) {
            case 0 -> "待入住";
            case 1 -> "已取消";
            case 2 -> "已完成";
            case 3 -> "已违约";
            default -> "未知";
        };
    }

    private String getOperateTypeName(Integer type) {
        return switch (type) {
            case 0 -> "支付";
            case 1 -> "退款";
            case 2 -> "扣罚";
            default -> "未知";
        };
    }

    /**
     * 预订导出数据
     */
    @lombok.Data
    public static class ReservationExportData {
        @com.alibaba.excel.annotation.ExcelProperty("预订ID")
        private Long id;
        @com.alibaba.excel.annotation.ExcelProperty("用户ID")
        private Long userId;
        @com.alibaba.excel.annotation.ExcelProperty("房间ID")
        private Long roomId;
        @com.alibaba.excel.annotation.ExcelProperty("入住日期")
        private String checkInDate;
        @com.alibaba.excel.annotation.ExcelProperty("退房日期")
        private String checkOutDate;
        @com.alibaba.excel.annotation.ExcelProperty("预订天数")
        private Integer reserveDays;
        @com.alibaba.excel.annotation.ExcelProperty("总价格")
        private String totalPrice;
        @com.alibaba.excel.annotation.ExcelProperty("定金")
        private String deposit;
        @com.alibaba.excel.annotation.ExcelProperty("定金状态")
        private String depositStatus;
        @com.alibaba.excel.annotation.ExcelProperty("预订时间")
        private String reserveTime;
        @com.alibaba.excel.annotation.ExcelProperty("预订状态")
        private String status;
    }

    /**
     * 定金导出数据
     */
    @lombok.Data
    public static class DepositExportData {
        @com.alibaba.excel.annotation.ExcelProperty("记录ID")
        private Long id;
        @com.alibaba.excel.annotation.ExcelProperty("预订ID")
        private Long reservationId;
        @com.alibaba.excel.annotation.ExcelProperty("金额")
        private String amount;
        @com.alibaba.excel.annotation.ExcelProperty("操作类型")
        private String operateType;
        @com.alibaba.excel.annotation.ExcelProperty("操作时间")
        private String operateTime;
        @com.alibaba.excel.annotation.ExcelProperty("备注")
        private String remark;
    }
}
