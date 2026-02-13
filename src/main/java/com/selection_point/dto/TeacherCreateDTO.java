package com.selection_point.dto;

import lombok.Data;

@Data
public class TeacherCreateDTO {
    private String name;
    private String mobile;
    private String email;
    private String subject;
    private String qualification;
    private Long batchId;
}

