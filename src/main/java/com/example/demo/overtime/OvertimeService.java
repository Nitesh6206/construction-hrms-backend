package com.example.demo.overtime;

import com.example.demo.attendance.AttendanceLog;
import com.example.demo.dto.OvertimeSummaryResponse;
import com.example.demo.dto.OvertimeSettlementResponse;
import com.example.demo.event.OvertimeSettledEvent;
import com.example.demo.overtime.exception.SettlementException;
import com.example.demo.worker.Worker;
import com.example.demo.worker.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final WorkerRepository workerRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Calculate overtime amount with tiered rates
    public BigDecimal calculateOvertimeAmount(double otHours, BigDecimal dailyWage) {
        if (otHours <= 0) return BigDecimal.ZERO;

        double hourlyRate = dailyWage.doubleValue() / 8.0;

        if (otHours <= 2) {
            return BigDecimal.valueOf(otHours * 1.5 * hourlyRate);
        } else {
            double firstTwoHours = 2 * 1.5 * hourlyRate;
            double remainingHours = (otHours - 2) * 2 * hourlyRate;
            return BigDecimal.valueOf(firstTwoHours + remainingHours);
        }
    }

    // Full overtime logic with monthly cap (60 hours)
    @Transactional
    public OvertimeEntry createOvertimeEntry(AttendanceLog attendanceLog) {
        Worker worker = attendanceLog.getWorker();
        double otHours = attendanceLog.getOvertimeHours();

        if (otHours <= 0) return null;

        // Check monthly overtime cap (60 hours)
        LocalDate monthStart = attendanceLog.getClockInTime().toLocalDate().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        double alreadyUsedOt = overtimeRepository.findByWorkerIdAndDateBetweenAndStatus(
                        worker.getId(), monthStart, monthEnd, SettlementStatus.PENDING)
                .stream()
                .mapToDouble(OvertimeEntry::getOvertimeHours)
                .sum();

        double allowedOt = Math.min(otHours, 60 - alreadyUsedOt);

        if (allowedOt <= 0) {
            // Cap reached - still record attendance but no overtime payment
            return null;
        }

        OvertimeEntry entry = new OvertimeEntry();
        entry.setWorker(worker);
        entry.setAttendance(attendanceLog);
        entry.setDate(attendanceLog.getClockInTime().toLocalDate());
        entry.setOvertimeHours(allowedOt);
        entry.setAmount(calculateOvertimeAmount(allowedOt, worker.getDailyWage()));
        entry.setStatus(SettlementStatus.PENDING);

        return overtimeRepository.save(entry);
    }

    public OvertimeSummaryResponse getMonthlySummary(Long workerId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<OvertimeEntry> entries = overtimeRepository.findByWorkerIdAndDateBetweenAndStatus(
                workerId, start, end, SettlementStatus.PENDING);

        double totalHours = entries.stream().mapToDouble(OvertimeEntry::getOvertimeHours).sum();
        BigDecimal totalAmount = entries.stream()
                .map(OvertimeEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OvertimeSummaryResponse(totalHours, totalAmount, entries, month);
    }

    @Transactional
    public OvertimeSettlementResponse settleOvertime(Long workerId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate today = LocalDate.now();

        if (yearMonth.getYear() == today.getYear() && yearMonth.getMonth() == today.getMonth()) {
            throw new SettlementException("Cannot settle current month");
        }

        List<OvertimeEntry> entries = overtimeRepository.findByWorkerIdAndDateBetweenAndStatus(
                workerId, yearMonth.atDay(1), yearMonth.atEndOfMonth(), SettlementStatus.PENDING);

        if (entries.isEmpty()) {
            throw new SettlementException("No pending overtime found");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OvertimeEntry entry : entries) {
            entry.setStatus(SettlementStatus.SETTLED);
            totalAmount = totalAmount.add(entry.getAmount());
        }

        overtimeRepository.saveAll(entries);

        // Publish event AFTER transaction commits
        eventPublisher.publishEvent(
                new OvertimeSettledEvent(workerId, month, totalAmount, entries.size())
        );

        return new OvertimeSettlementResponse(totalAmount, entries.size(),
                "Overtime settled successfully for " + month);
    }
}