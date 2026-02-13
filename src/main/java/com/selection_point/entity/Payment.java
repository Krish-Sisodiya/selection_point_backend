package com.selection_point.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private double amount;
    private String status; // SUCCESS / FAILED
    private String mode;   // UPI / QR / CASH

    private LocalDateTime paymentTime = LocalDateTime.now();

    // getters & setters
}
