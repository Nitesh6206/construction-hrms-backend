package com.example.demo.attendance;

import com.example.demo.dto.ActiveWorkerDTO;
import com.example.demo.overtime.OvertimeEntry;
import com.example.demo.overtime.OvertimeService;
import com.example.demo.site.Site;
import com.example.demo.site.SiteRepository;
import com.example.demo.worker.Worker;
import com.example.demo.worker.WorkerRepository;
import com.example.demo.attendance.exception.AlreadyClockedInException;
import com.example.demo.attendance.exception.NotClockedInException;
import com.example.demo.worker.exception.WorkerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final WorkerRepository workerRepository;
    private final SiteRepository siteRepository;
    private final ActiveWorkerService activeWorkerService;
    private final OvertimeService overtimeService;   // Injected for overtime logic

    @Transactional
    public AttendanceLog clockIn(Long workerId, Long siteId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkerNotFoundException("Worker not found with id: " + workerId));

        if (!worker.isActive()) {
            throw new RuntimeException("Worker is not active");
        }

        // Check if already clocked in
        if (attendanceRepository.findByWorkerIdAndClockOutTimeIsNull(workerId).isPresent()) {
            throw new AlreadyClockedInException("Worker is already clocked in at another site");
        }

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + siteId));

        AttendanceLog log = new AttendanceLog();
        log.setWorker(worker);
        log.setSite(site);
        log.setClockInTime(LocalDateTime.now().minusMonths(1).minusHours(19));

        AttendanceLog savedLog = attendanceRepository.save(log);

        // Add to Redis Active Workers
        activeWorkerService.clockIn(workerId, siteId, log.getClockInTime());

        return savedLog;
    }

    @Transactional
    public AttendanceLog clockOut(Long workerId) {
        // Find active attendance record
        AttendanceLog log = attendanceRepository.findByWorkerIdAndClockOutTimeIsNull(workerId)
                .orElseThrow(() -> new NotClockedInException("Worker is not currently clocked in"));

        LocalDateTime now = LocalDateTime.now().minusMonths(1);
        log.setClockOutTime(now);

        // Calculate total hours
        long minutes = ChronoUnit.MINUTES.between(log.getClockInTime(), now);
        double totalHours = minutes / 60.0;

        log.setTotalHours(totalHours);
        double overtimeHours = Math.max(0, totalHours - 8);
        log.setOvertimeHours(overtimeHours);

        // Flag if shift > 16 hours
        if (totalHours > 16) {
            log.setFlagged(true);
        }

        // Save attendance first
        AttendanceLog savedLog = attendanceRepository.save(log);

        // Create Overtime Entry if applicable (with monthly cap & rate calculation)
        if (overtimeHours > 0) {
            OvertimeEntry overtimeEntry = overtimeService.createOvertimeEntry(savedLog);
            // You can log or handle the overtime entry if needed
        }

        // Remove from Active Workers in Redis
        activeWorkerService.clockOut(workerId);

        return savedLog;
    }

    public List<ActiveWorkerDTO> getActiveWorkers() {
        return activeWorkerService.getActiveWorkers();
    }

    public Page<AttendanceLog> getAttendanceHistory(Long workerId, String from, String to, int page, int size) {
        LocalDateTime fromDate = LocalDateTime.parse(from + "T00:00:00");
        LocalDateTime toDate = LocalDateTime.parse(to + "T23:59:59");
        Pageable pageable = PageRequest.of(page, size);

        return attendanceRepository.findByWorkerIdAndClockInTimeBetween(
                workerId, fromDate, toDate, pageable);
    }
}