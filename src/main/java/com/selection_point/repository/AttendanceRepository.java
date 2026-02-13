package com.selection_point.repository;

import com.selection_point.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByBatchIdAndDate(Long batchId, LocalDate date);
}
