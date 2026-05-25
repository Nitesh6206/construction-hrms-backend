package com.example.demo.overtime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<OvertimeEntry, Long> {
    List<OvertimeEntry> findByWorkerIdAndDateBetweenAndStatus(
            Long workerId, LocalDate start, LocalDate end, SettlementStatus status);
}