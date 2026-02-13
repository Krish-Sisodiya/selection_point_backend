package com.selection_point.service;

import com.selection_point.dto.MonthlyAmountDTO;
import com.selection_point.dto.MonthlyCountDTO;
import com.selection_point.dto.StudentHistoryStatsDto;
import com.selection_point.repository.BatchRepository;
import com.selection_point.repository.DeletedStudentRepository;
import com.selection_point.repository.SchoolRepository;
import com.selection_point.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class StudentHistoryService {

    private final StudentRepository studentRepo;
    private final DeletedStudentRepository deletedRepo;
    private final BatchRepository batchRepo;

    public StudentHistoryStatsDto getStats() {

        StudentHistoryStatsDto dto = new StudentHistoryStatsDto();

        // ================= STUDENT COUNTS =================
        long activeStudents = studentRepo.count();
        long deletedStudents = deletedRepo.count();
        long totalAdmissions = activeStudents + deletedStudents;

        dto.setActiveStudents(activeStudents);
        dto.setDeletedStudents(deletedStudents);
        dto.setTotalStudents(totalAdmissions);
        dto.setTotalAdmissions(totalAdmissions);

        // ================= OTHER COUNTS =================
        dto.setTotalSchools(studentRepo.totalSchools());
        dto.setTotalBatches(batchRepo.totalBatches());

        // ================= REVENUE =================
        double totalRevenue =
                Optional.ofNullable(studentRepo.getTotalRevenue()).orElse(0.0);
        dto.setTotalRevenue(totalRevenue);

        // 🔥 AVERAGE REVENUE (MISSING FIX)
        dto.setAvgRevenue(
                activeStudents > 0 ? totalRevenue / activeStudents : 0.0
        );

        // ================= MONTHLY ADMISSIONS =================
        List<MonthlyCountDTO> admissionList = new ArrayList<>();
        for (Object[] o : studentRepo.monthlyAdmissions()) {

            int month = ((Number) o[0]).intValue();
            long count = ((Number) o[1]).longValue();

            admissionList.add(
                    new MonthlyCountDTO(getMonthName(month), count)
            );
        }
        dto.setAdmissionGraph(admissionList);

        // ================= MONTHLY REVENUE =================
        List<MonthlyAmountDTO> revenueList = new ArrayList<>();
        for (Object[] o : studentRepo.monthlyRevenue()) {

            int month = ((Number) o[0]).intValue();
            double amount = ((Number) o[1]).doubleValue();

            revenueList.add(
                    new MonthlyAmountDTO(getMonthName(month), amount)
            );
        }
        dto.setRevenueGraph(revenueList);

        return dto;
    }


    // 🔥 CLEAN MONTH NAME
    private String getMonthName(int month) {
        return java.time.Month.of(month)
                .name()
                .substring(0, 3); // JAN FEB MAR
    }

}
