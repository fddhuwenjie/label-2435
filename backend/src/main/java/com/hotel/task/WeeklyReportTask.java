package com.hotel.task;

import com.hotel.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * 周报任务
 * 每周一00:00自动生成上周（周一至周日）预订清单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportTask {

    private final ExportService exportService;

    /**
     * 每周一00:00执行
     */
    @Scheduled(cron = "0 0 0 * * MON")
    public void generateWeeklyReport() {
        log.info("开始生成周报...");

        try {
            // 计算上周的时间范围
            LocalDate today = LocalDate.now();
            LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastSunday = lastMonday.plusDays(6);

            LocalDateTime startTime = LocalDateTime.of(lastMonday, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(lastSunday, LocalTime.MAX);

            // 导出预订清单
            String filePath = exportService.exportReservations(startTime, endTime);
            log.info("周报生成成功: {}", filePath);

        } catch (Exception e) {
            log.error("周报生成失败", e);
        }
    }
}
