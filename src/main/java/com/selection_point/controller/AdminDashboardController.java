package com.selection_point.controller;

import com.selection_point.dto.DashboardResponse;
import com.selection_point.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(Authentication auth) {
        String email = auth.getName(); // JWT se
        return dashboardService.getDashboardData(email);
    }
}

