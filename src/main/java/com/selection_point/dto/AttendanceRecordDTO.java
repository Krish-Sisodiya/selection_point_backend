package com.selection_point.dto;

import lombok.Data;

@Data
public class AttendanceRecordDTO {

    private Long studentId;
    private String status;

    // 🔥 THIS CONSTRUCTOR IS REQUIRED
    public AttendanceRecordDTO(Long studentId, String status) {
        this.studentId = studentId;
        this.status = status;
    }
}
