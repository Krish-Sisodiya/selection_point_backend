package com.selection_point.mapper;

import com.selection_point.dto.StudentDTO;
import com.selection_point.entity.Student;

public class StudentMapper {

    public static StudentDTO toDTO(Student s) {

        StudentDTO dto = new StudentDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setStudentClass(s.getStudentClass());
        dto.setMobile(s.getMobile());
        dto.setCoursePackage(s.getCoursePackage());

        // ✅ BATCH NAME
        if (s.getBatch() != null) {
            dto.setBatchName(s.getBatch().getBatchName());
        }

        // ✅ SCHOOL NAME (FIXED)
        if (s.getSchool() != null) {
            dto.setSchoolName(s.getSchoolName()); // 🔥 IMPORTANT
        }

        // ✅ ADDRESS (THIS WAS MISSING ❗)
        dto.setAddress(s.getAddress());

        // ✅ STATUS
        dto.setAdmissionStatus(s.getAdmissionStatus().name());
        dto.setStudentStatus(s.getStudentStatus().name());

        // ✅ FEES
        dto.setMainFeePaid(s.isMainFeePaid());

        return dto;
    }
}
