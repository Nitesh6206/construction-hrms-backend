package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeSettlementResponse {
    private BigDecimal totalSettledAmount;
    private int totalEntriesSettled;
    private String message;
}