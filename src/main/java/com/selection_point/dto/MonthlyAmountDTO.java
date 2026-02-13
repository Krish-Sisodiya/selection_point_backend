package com.selection_point.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyAmountDTO {
    private String month;
    private double amount;
}
