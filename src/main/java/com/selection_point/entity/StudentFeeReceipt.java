package com.selection_point.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_receipts")
@Data
public class StudentFeeReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private double amount;

    private String feeType; // REGISTRATION / MAIN_FEE

    private LocalDateTime createdAt;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] pdf;

    private LocalDate paymentDate;
    private String receiptNumber;

}
