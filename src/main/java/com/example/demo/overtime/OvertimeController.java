package com.example.demo.overtime;

import com.example.demo.dto.OvertimeSettlementResponse;
import com.example.demo.dto.OvertimeSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    @GetMapping("/summary/{workerId}")
    public ResponseEntity<OvertimeSummaryResponse> getMonthlySummary(
            @PathVariable Long workerId,
            @RequestParam String month) {   // Format: YYYY-MM

        OvertimeSummaryResponse summary = overtimeService.getMonthlySummary(workerId, month);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/settle/{workerId}")
    public ResponseEntity<OvertimeSettlementResponse> settleOvertime(
            @PathVariable Long workerId,
            @RequestParam String month) {

        OvertimeSettlementResponse response = overtimeService.settleOvertime(workerId, month);
        return ResponseEntity.ok(response);
    }
}