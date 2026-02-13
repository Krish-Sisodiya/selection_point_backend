package com.selection_point.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========= BASIC =========
    private String name;
    private String studentClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    private String schoolName;

    private String coursePackage;
    private String referCode;

    private String mobile;
    private String address;
    @ManyToOne
    @JoinColumn(name = "batch_id")
    @JsonIgnoreProperties("students")
    private Batch batch;

    private boolean active = true;

    private Double feePaid = 0.0;

    // ========= FEES =========
    @Column(nullable = false)
    private double feeAmount;

    private double totalFee;

    @Column(nullable = false)
    private double discountAmount = 0.0;

    @Column(nullable = false)
    private boolean offerApplied = false;

    private String appliedOfferCode;

    private boolean paymentDone = false;

    @Column(nullable = false)
    private double registrationFee = 300;

    private boolean registrationFeePaid;

    private double mainFee;
    private boolean mainFeePaid = false;

    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnoreProperties("student")
    private List<AttendanceRecord> attendanceRecords;




    public enum StudentStatus {
        ACTIVE,
        INACTIVE,
        DROPPED
    }

    public enum AdmissionStatus {
        PENDING,
        APPROVED
    }

    // ========= STATUS =========
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus studentStatus = StudentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdmissionStatus admissionStatus = AdmissionStatus.PENDING;

    // ========= DATE =========
    @Column(nullable = false)
    private LocalDate admissionDate = LocalDate.now();
}
