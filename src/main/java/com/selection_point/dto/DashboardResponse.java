package com.selection_point.dto;

import java.util.List;
import java.util.Map;

public class DashboardResponse {

    private String adminName;
    private String adminEmail;

    private long totalStudents;
    private long totalTeachers;
    private double totalRevenue;
    private long totalAdmissions;

    private List<Map<String, Object>> weeklyAdmissions;

    // ===== GETTERS & SETTERS =====

    public String getAdminName() {
        return adminName;
    }
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public long getTotalStudents() {
        return totalStudents;
    }
    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalTeachers() {
        return totalTeachers;
    }
    public void setTotalTeachers(long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalAdmissions() {
        return totalAdmissions;
    }
    public void setTotalAdmissions(long totalAdmissions) {
        this.totalAdmissions = totalAdmissions;
    }

    public List<Map<String, Object>> getWeeklyAdmissions() {
        return weeklyAdmissions;
    }
    public void setWeeklyAdmissions(List<Map<String, Object>> weeklyAdmissions) {
        this.weeklyAdmissions = weeklyAdmissions;
    }
}
