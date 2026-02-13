package com.selection_point.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttendanceResponse {
    private Long attendanceId;
    private Long batchId;
    private String date;
    private List<AttendanceRecordDTO> records;
}
