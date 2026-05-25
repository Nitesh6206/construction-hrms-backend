package com.example.demo.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OvertimeEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOvertimeSettled(OvertimeSettledEvent event) {
        try {
            // Simulate SMS sending
            log.info("✅ SMS SENT to Worker ID: {} | Month: {} | Amount: ₹{} | Entries: {}",
                    event.getWorkerId(), event.getMonth(), event.getTotalAmount(), event.getEntryCount());

            // In real project, call SMS gateway here (Twilio, MSG91, etc.)
            Thread.sleep(500); // Simulate network delay

        } catch (Exception e) {
            log.error("Failed to send SMS for worker {}: {}", event.getWorkerId(), e.getMessage());
            // Don't throw exception - settlement should not fail if SMS fails
        }
    }
}