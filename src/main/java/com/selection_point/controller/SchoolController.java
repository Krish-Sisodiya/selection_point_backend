package com.selection_point.controller;

import com.selection_point.dto.SchoolScatterDTO;
import com.selection_point.entity.School;
import com.selection_point.repository.SchoolRepository;
import com.selection_point.repository.StudentRepository;
import com.selection_point.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/schools")
@CrossOrigin(origins = "http://localhost:5173")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    StudentRepository studentRepository;

    // ✅ FIXED
    @PostMapping
    public School addSchool(@RequestBody School school) {
        return schoolService.registerSchool(school);
    }

    @GetMapping
    public List<School> getSchools() {
        return schoolService.getAllSchools();
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public School update(
            @PathVariable Long id,
            @RequestBody School school
    ) {
        return schoolService.updateSchool(id, school);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        schoolService.deleteSchool(id);
    }


    // 📊 SCATTER GRAPH DATA API
    @GetMapping("/stats/scatter")
    public List<SchoolScatterDTO> scatterData() {
        return studentRepository.getStudentsPerSchoolScatter();
    }


    @GetMapping("/stats")
    public Map<String, Object> stats() {

        long totalSchools = schoolRepository.count();
        long totalStudents = studentRepository.count();

        return Map.of(
                "totalSchools", totalSchools,
                "activeSchools", totalSchools, // temporary
                "totalStudents", totalStudents,
                "today", 0
        );
    }




}
