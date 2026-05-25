package com.example.demo.attendance;

import com.example.demo.dto.ActiveWorkerDTO;
import com.example.demo.dto.ClockInRequest;
import com.example.demo.dto.ClockOutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceLog> clockIn(@RequestBody ClockInRequest request) {
        AttendanceLog attendance = attendanceService.clockIn(request.getWorkerId(), request.getSiteId());
        return new ResponseEntity<>(attendance, HttpStatus.CREATED);
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceLog> clockOut(@RequestBody ClockOutRequest request) {
        AttendanceLog attendance = attendanceService.clockOut(request.getWorkerId());
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveWorkerDTO>> getActiveWorkers() {
        return ResponseEntity.ok(attendanceService.getActiveWorkers());
    }

    @GetMapping("/log")
    public ResponseEntity<Page<AttendanceLog>> getAttendanceLog(
            @RequestParam Long workerId,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AttendanceLog> logs = attendanceService.getAttendanceHistory(workerId, from, to, page, size);
        return ResponseEntity.ok(logs);
    }
}