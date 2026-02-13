package com.selection_point.mapper;

import com.selection_point.dto.BatchDTO;
import com.selection_point.entity.Batch;

import java.util.stream.Collectors;

public class BatchMapper {

    public static BatchDTO toDTO(Batch batch) {
        BatchDTO dto = new BatchDTO();
        dto.setId(batch.getId());
        dto.setBatchName(batch.getBatchName());
        dto.setCoursePackage(batch.getCoursePackage());
        dto.setCapacity(batch.getCapacity());
        dto.setCurrentStrength(batch.getCurrentStrength());
        return dto;
    }

}
