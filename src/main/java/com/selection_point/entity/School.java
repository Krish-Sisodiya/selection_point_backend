package com.selection_point.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "schools")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_name")
    private String schoolName;

    private String address;
    private String teacherName;
    private String teacherMobile;

    // 🔥 VERY IMPORTANT
    @Column(name = "offer_code", unique = true)
    private String offerCode;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDate createdDate = LocalDate.now();



    // getters setters
}
