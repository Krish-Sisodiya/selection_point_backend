package com.selection_point.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchName;          // e.g. FOUNDATION-1
    private String coursePackage;      // Foundation / Board Focus etc

    private int capacity = 30;
    private int currentStrength = 0;

    private String timeSlot;           // optional (Morning / Evening)

    @OneToMany(
            mappedBy = "batch",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Student> students = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties("batches")
    private Teacher teacher;


    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    public void addStudent(Student student) {
        students.add(student);
        student.setBatch(this);
        currentStrength++;
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.setBatch(null);
        if (currentStrength > 0) currentStrength--;
    }



}
