package com.example.demo.event;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class OvertimeSettledEvent {
    private final Long workerId;
    private final String month;
    private final BigDecimal totalAmount;
    private final int entryCount;

    public OvertimeSettledEvent(Long workerId, String month, BigDecimal totalAmount, int entryCount) {
        this.workerId = workerId;
        this.month = month;
        this.totalAmount = totalAmount;
        this.entryCount = entryCount;
    }
}