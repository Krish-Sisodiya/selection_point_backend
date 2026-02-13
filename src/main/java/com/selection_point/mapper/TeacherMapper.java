package com.selection_point.mapper;

import com.selection_point.dto.*;
import com.selection_point.entity.Teacher;
import com.selection_point.entity.TeacherSalary;

import java.util.stream.Collectors;

public class TeacherMapper {

    public static TeacherDTO toDTO(Teacher t) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setMobile(t.getMobile());
        dto.setEmail(t.getEmail());
        dto.setSubject(t.getSubject());
        dto.setQualification(t.getQualification());
        dto.setActive(t.isActive());
        dto.setStatus(t.getStatus().name());

        if (t.getBatches() != null) {
            dto.setBatches(
                    t.getBatches().stream().map(b -> {
                        BatchDTO bd = new BatchDTO();
                        bd.setId(b.getId());
                        bd.setBatchName(b.getBatchName());
                        bd.setCurrentStrength(b.getCurrentStrength());
                        return bd;
                    }).collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static TeacherSalaryDTO toSalaryDTO(TeacherSalary s) {
        TeacherSalaryDTO dto = new TeacherSalaryDTO();
        dto.setId(s.getId());
        dto.setMonth(s.getMonth());
        dto.setAmount(s.getAmount());
        dto.setPaidDate(s.getPaidDate());
        dto.setReceiptUrl(s.getReceiptUrl());
        return dto;
    }
}
