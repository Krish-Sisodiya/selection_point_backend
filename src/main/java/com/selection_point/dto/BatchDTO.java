package com.selection_point.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchDTO {
    private Long id;
    private String batchName;
    private String coursePackage;
    private int capacity;
    private int currentStrength;
}

