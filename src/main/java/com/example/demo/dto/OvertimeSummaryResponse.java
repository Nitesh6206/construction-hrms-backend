package com.example.demo.dto;

import com.example.demo.overtime.OvertimeEntry;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeSummaryResponse {
    private double totalOvertimeHours;
    private BigDecimal totalAmount;
    private List<OvertimeEntry> entries;
    private String month;
}