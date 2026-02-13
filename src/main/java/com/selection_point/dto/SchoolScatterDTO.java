package com.selection_point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchoolScatterDTO {

    private Long x;        // school id / index
    private Long y;        // total students
    private String name;   // school name
}
