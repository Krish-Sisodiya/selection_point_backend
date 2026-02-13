package com.selection_point.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherSalaryDTO {

    private Long id;
    private String month;
    private double amount;
    private LocalDate paidDate;
    @Column(name = "receipt_url")
    private String receiptUrl;


    // getters & setters
}
