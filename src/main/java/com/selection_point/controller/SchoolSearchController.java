package com.selection_point.controller;

import com.selection_point.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schools")
public class SchoolSearchController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/search")
    public List<String> searchSchools(@RequestParam String query) {
        return studentRepository.searchSchoolNames(query);
    }
}
