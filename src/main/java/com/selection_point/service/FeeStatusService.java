package com.selection_point.service;

import com.selection_point.dto.FeeStatusDTO;
import com.selection_point.entity.Student;
import com.selection_point.entity.StudentFeeReceipt;
import com.selection_point.repository.StudentFeeReceiptRepository;
import com.selection_point.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeeStatusService {

    private final StudentRepository studentRepository;
    private final StudentFeeReceiptRepository receiptRepository;

    public FeeStatusService(
            StudentRepository studentRepository,
            StudentFeeReceiptRepository receiptRepository
    ) {
        this.studentRepository = studentRepository;
        this.receiptRepository = receiptRepository;
    }

    public List<FeeStatusDTO> getAllFeeStatus() {

        return studentRepository.findAll().stream().map(student -> {

            double totalFee = student.getTotalFee();   // 👈 FIXED
            double paidAmount = receiptRepository
                    .findByStudentId(student.getId())
                    .stream()
                    .mapToDouble(StudentFeeReceipt::getAmount)
                    .sum();

            double pendingAmount = Math.max(totalFee - paidAmount, 0);

            String status;
            if (paidAmount == 0) {
                status = "PENDING";
            } else if (pendingAmount == 0) {
                status = "PAID";
            } else {
                status = "PARTIAL";
            }

            return new FeeStatusDTO(
                    student.getId(),
                    student.getName(),
                    student.getStudentClass(),
                    student.getMobile(),
                    totalFee,
                    paidAmount,
                    pendingAmount,
                    status
            );
        }).toList();
    }

}

