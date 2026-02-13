package com.selection_point.service;

import com.selection_point.dto.AttendanceRecordDTO;
import com.selection_point.dto.AttendanceResponse;
import com.selection_point.entity.*;
import com.selection_point.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceRecordRepository recordRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ✅ GET OR CREATE ATTENDANCE
    public Attendance getAttendance(Long batchId, LocalDate date) {

        return attendanceRepository
                .findByBatchIdAndDate(batchId, date)
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setBatch(batchRepository.findById(batchId).orElseThrow());
                    a.setDate(date);
                    return attendanceRepository.save(a);
                });
    }

    // ✅ SAVE / UPDATE ATTENDANCE
    public void saveAttendance(
            Long batchId,
            LocalDate date,
            List<AttendanceRecord> records
    ) {
        Attendance attendance = getAttendance(batchId, date);

        recordRepository.deleteAll(attendance.getRecords());

        for (AttendanceRecord r : records) {
            r.setAttendance(attendance);
            recordRepository.save(r);
        }
    }

    // ✅ DTO
    public AttendanceResponse getAttendanceDTO(Long batchId, LocalDate date) {

        Attendance attendance = getAttendance(batchId, date);

        List<AttendanceRecordDTO> records = attendance.getRecords()
                .stream()
                .map(r -> new AttendanceRecordDTO(
                        r.getStudent().getId(),
                        r.getStatus().name()
                ))
                .toList();

        AttendanceResponse res = new AttendanceResponse();
        res.setAttendanceId(attendance.getId());
        res.setBatchId(batchId);
        res.setDate(date.toString());
        res.setRecords(records);

        return res;
    }

}
