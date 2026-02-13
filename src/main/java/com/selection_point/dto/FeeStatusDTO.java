package com.selection_point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeStatusDTO {

    private Long id;
    private String name;
    private String studentClass;
    private String mobile;

    private double totalFee;
    private double paidAmount;
    private double pendingAmount;
    private String status; // PAID / PARTIAL / PENDING
}

