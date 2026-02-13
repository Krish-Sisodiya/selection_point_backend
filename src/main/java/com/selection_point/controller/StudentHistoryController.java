package com.selection_point.controller;

import com.selection_point.dto.StudentHistoryStatsDto;
import com.selection_point.service.StudentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/student-history")
@RequiredArgsConstructor
public class StudentHistoryController {

    private final StudentHistoryService service;

    @GetMapping("/stats")
    public StudentHistoryStatsDto stats() {
        return service.getStats();
    }
}

