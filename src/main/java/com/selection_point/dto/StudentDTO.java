package com.selection_point.dto;

import lombok.Data;

@Data
public class StudentDTO {

    private Long id;
    private String name;
    private String studentClass;
    private String mobile;
    private String coursePackage;

    private String batchName;   // only name, no batch object
    private String schoolName;

    private String admissionStatus;
    private String studentStatus;
    private boolean mainFeePaid;
    private String address;
    private boolean registrationFeePaid;
}
