package com.selection_point.controller;

import com.selection_point.dto.AttendanceResponse;
import com.selection_point.entity.AttendanceRecord;
import com.selection_point.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // ✅ GET attendance (DTO RESPONSE)
    @GetMapping
    public AttendanceResponse get(
            @RequestParam Long batchId,
            @RequestParam String date
    ) {
        return attendanceService.getAttendanceDTO(
                batchId,
                LocalDate.parse(date)
        );
    }

    // ✅ SAVE / UPDATE attendance
    @PostMapping
    public void save(
            @RequestParam Long batchId,
            @RequestParam String date,
            @RequestBody List<AttendanceRecord> records
    ) {
        attendanceService.saveAttendance(
                batchId,
                LocalDate.parse(date),
                records
        );
    }
}
