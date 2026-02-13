package com.selection_point.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String mobile;
    private String email;
    private String subject;
    private String qualification;

    // 💰 SALARIES
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TeacherSalary> salaries;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private boolean active = true;

    private LocalDate createdDate = LocalDate.now();

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate = LocalDate.now();

    // 🧠 AUTO SET DATES
    @PrePersist
    public void onCreate() {
        if (joiningDate == null) joiningDate = LocalDate.now();
        if (createdDate == null) createdDate = LocalDate.now();
    }

    // 🏫 ASSIGNED BATCHES (ONE TEACHER → MANY BATCHES)
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("teacher")
    private List<Batch> batches;


    public enum Status {
        PENDING,
        APPROVED
    }
}
