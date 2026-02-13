package com.selection_point.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StudentHistoryStatsDto {

    private long totalStudents;
    private long activeStudents;
    private long deletedStudents;
    private long totalAdmissions;
    private long totalBatches;
    private long totalSchools;

    private double totalRevenue;
    private double avgRevenue;

    private Map<String, Long> monthlyAdmissions;
    private Map<String, Long> batchesBySchool;

    // 📊 Graph data
    private List<MonthlyCountDTO> admissionGraph;
    private List<MonthlyAmountDTO> revenueGraph;
}


