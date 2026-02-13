package com.selection_point.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeacherDTO {

    private Long id;
    private String name;
    private String mobile;
    private String email;
    private String subject;
    private String qualification;
    private boolean active;
    private String status;

    private List<BatchDTO> batches;

    // getters & setters
}
