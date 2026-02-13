package com.selection_point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AdminSettings {

    @Id
    private Long id = 1L;

    private String upiId;   // admin@upi
    private String instituteName = "Selection Point";
}

