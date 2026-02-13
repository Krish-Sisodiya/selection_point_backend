package com.selection_point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyCountDTO {
    private String month;
    private long count;
}
