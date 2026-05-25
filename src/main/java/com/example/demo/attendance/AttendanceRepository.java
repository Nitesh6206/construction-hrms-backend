package com.example.demo.attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {

    @EntityGraph(attributePaths = {"worker", "site"})
    Optional<AttendanceLog> findByWorkerIdAndClockOutTimeIsNull(Long workerId);

    @EntityGraph(attributePaths = {"worker", "site"})
    Page<AttendanceLog> findByWorkerIdAndClockInTimeBetween(
            Long workerId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}