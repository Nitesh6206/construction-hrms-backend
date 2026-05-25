package com.example.demo.overtime;

import com.example.demo.attendance.AttendanceLog;
import com.example.demo.worker.Worker;
import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "overtime_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private AttendanceLog attendance;

    @Column(nullable = false)
    private LocalDate date;

    private Double overtimeHours;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status = SettlementStatus.PENDING;
}