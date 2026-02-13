package com.selection_point.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name = "deleted_students")
@Data
public class DeletedStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originalStudentId;

    private String name;
    private String studentClass;
    private String mobile;
    private String address;

    private String schoolName;
    private String batchName;

    private double feeAmount;
    private double registrationFee;
    private double mainFee;

    private LocalDate admissionDate;

    private String deletedBy;
    private String deleteReason;
    private LocalDate deletedDate;
}
