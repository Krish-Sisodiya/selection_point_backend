package com.selection_point.controller;

import com.selection_point.dto.FeeStatusDTO;
import com.selection_point.entity.StudentFeeReceipt;
import com.selection_point.repository.StudentFeeReceiptRepository;
import com.selection_point.service.FeeStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class FeeStatusController {

    private final FeeStatusService feeStatusService;
    private final StudentFeeReceiptRepository receiptRepository;

    public FeeStatusController(
            FeeStatusService feeStatusService,
            StudentFeeReceiptRepository receiptRepository
    ) {
        this.feeStatusService = feeStatusService;
        this.receiptRepository = receiptRepository;
    }

    // 🔥 ALL STUDENTS FEE STATUS
    @GetMapping("/fees/status")
    public List<FeeStatusDTO> getFeeStatus() {
        return feeStatusService.getAllFeeStatus();
    }

    // 🔥 STUDENT RECEIPTS (DATE WISE)
    @GetMapping("/fees/student/{studentId}")
    public List<StudentFeeReceipt> getStudentReceipts(
            @PathVariable Long studentId
    ) {
        return receiptRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    // 🔥 VIEW RECEIPT PDF
    @GetMapping("/fees/receipt/{receiptId}")
    public ResponseEntity<byte[]> viewReceipt(
            @PathVariable Long receiptId
    ) {

        StudentFeeReceipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() ->
                        new RuntimeException("Receipt not found"));

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header(
                        "Content-Disposition",
                        "inline; filename=" +
                                receipt.getReceiptNumber() + ".pdf"
                )
                .body(receipt.getPdf());
    }
}
