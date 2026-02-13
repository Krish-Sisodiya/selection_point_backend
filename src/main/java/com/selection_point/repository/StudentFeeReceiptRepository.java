package com.selection_point.repository;

import com.selection_point.entity.StudentFeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentFeeReceiptRepository
        extends JpaRepository<StudentFeeReceipt, Long> {

    List<StudentFeeReceipt> findByStudentId(Long studentId);

    List<StudentFeeReceipt> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    // 🔥 TOTAL PAID AMOUNT (IMPORTANT)
    @Query("""
        SELECT COALESCE(SUM(r.amount), 0)
        FROM StudentFeeReceipt r
        WHERE r.studentId = :studentId
    """)
    Double getTotalPaidAmount(@Param("studentId") Long studentId);

    // 📄 DATE WISE RECEIPTS
    List<StudentFeeReceipt>
    findByStudentIdOrderByPaymentDateDesc(Long studentId);
}
