package com.selection_point.repository;

import com.selection_point.dto.AttendanceRecordDTO;
import com.selection_point.entity.AttendanceRecord;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Long> {

    @Query("""
    SELECT new com.selection_point.dto.AttendanceRecordDTO(
        r.student.id,
        CAST(r.status AS string)
    )
    FROM AttendanceRecord r
    WHERE r.attendance.id = :attendanceId
    """)
    List<AttendanceRecordDTO> findDTOs(
            @Param("attendanceId") Long attendanceId
    );

    @Modifying
    @Transactional
    @Query("delete from AttendanceRecord a where a.student.id = :studentId")
    void deleteByStudentId(Long studentId);
}
