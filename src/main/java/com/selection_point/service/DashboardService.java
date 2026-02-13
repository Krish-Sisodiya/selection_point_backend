package com.selection_point.service;

import com.selection_point.dto.DashboardResponse;
import com.selection_point.entity.User;
import com.selection_point.repository.StudentRepository;
import com.selection_point.repository.TeacherRepository;
import com.selection_point.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public DashboardResponse getDashboardData(String email) {

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        DashboardResponse res = new DashboardResponse();

        // ================= ADMIN INFO =================
        res.setAdminName(admin.getName());
        res.setAdminEmail(admin.getEmail());

        // ================= COUNTS =================
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        double totalRevenue = studentRepository.getTotalRevenue();

        // Admissions (this month)
        long monthlyAdmissions =
                studentRepository.countAdmissionsAfter(
                        LocalDate.now().withDayOfMonth(1)
                );

        res.setTotalStudents(totalStudents);
        res.setTotalTeachers(totalTeachers);
        res.setTotalRevenue(totalRevenue);
        res.setTotalAdmissions(monthlyAdmissions);

        // ================= CHART DATA =================
        res.setWeeklyAdmissions(List.of(
                Map.of("name", "Mon", "value", 30),
                Map.of("name", "Tue", "value", 45),
                Map.of("name", "Wed", "value", 60),
                Map.of("name", "Thu", "value", 40),
                Map.of("name", "Fri", "value", 80),
                Map.of("name", "Sat", "value", 55)
        ));

        return res;
    }
}
