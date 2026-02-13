package com.selection_point.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class TeacherSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;


    private String month;           // 2025-01
    private double amount;

    private String upiId;
    private LocalDate paidDate;

    private boolean paid;

    // PDF PATH / URL
    private String receiptUrl;
    // Admin who paid
    private String paidBy;

}
