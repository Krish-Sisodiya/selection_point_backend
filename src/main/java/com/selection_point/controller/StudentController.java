package com.selection_point.controller;

import com.selection_point.dto.StudentDTO;
import com.selection_point.entity.DeletedStudent;
import com.selection_point.entity.Student;
import com.selection_point.mapper.StudentMapper;
import com.selection_point.repository.DeletedStudentRepository;
import com.selection_point.repository.StudentRepository;
import com.selection_point.service.PdfService;
import com.selection_point.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DeletedStudentRepository deletedStudentRepository;

    @Autowired
    private PdfService pdfService;

    // ✅ GET ALL
    @GetMapping
    public List<StudentDTO> getAll() {
        return studentService.getAllStudents()
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public StudentDTO getById(@PathVariable Long id) {
        return StudentMapper.toDTO(
                studentService.getStudentById(id)
        );
    }

    // ✅ ADD
    @PostMapping
    public Student save(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public Student update(
            @PathVariable Long id,
            @RequestBody Student updatedStudent
    ) {
        return studentService.updateStudent(id, updatedStudent);
    }

    // ✅ DELETE STUDENT (ARCHIVE + SOFT DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Principal principal
    ) {
        String admin = principal.getName();
        studentService.deleteStudent(id, reason, admin);
        return ResponseEntity.ok().build();
    }



    // ✅ APPROVE FEES
    @PutMapping("/{id}/approve")
    public StudentDTO approve(@PathVariable Long id) {
        return StudentMapper.toDTO(
                studentService.approveFee(id)
        );
    }

    // ✅ STATS (ONLY ONE)
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return studentService.stats();
    }


    // Deleted Student Apis
    @GetMapping("/deleted")
    public List<DeletedStudent> getDeletedStudents() {
        return deletedStudentRepository.findAll();
    }

    @PutMapping("/restore/{deletedId}")
    public void restoreStudent(@PathVariable Long deletedId) {
        studentService.restoreDeletedStudent(deletedId);
    }


    @DeleteMapping("/permanent-delete/{deletedId}")
    public ResponseEntity<?> permanentDelete(@PathVariable Long deletedId) {
        studentService.permanentDeleteStudent(deletedId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/pay-register")
    public ResponseEntity<?> payAndRegister(
            @RequestBody Student student
    ) {
        // 🔥 registration fee paid flag
        student.setRegistrationFeePaid(true);

        // 🔥 complete registration after payment
        Student saved = studentService.registerAfterPayment(student);

        // ✅ return student id (frontend ke liye useful)
        return ResponseEntity.ok(saved.getId());
    }


    /* 🔥 GET REGISTRATION UPI */
    @GetMapping("/fees/registration/upi")
    public Map<String, String> registrationUpi(
            @RequestParam double amount
    ) {
        return Map.of(
                "upiUrl",
                pdfService.generateUpiUrl(amount)
        );
    }

    /* 🔥 SEND UPI STRING */
    @GetMapping("/fees/upi")
    public Map<String, String> getUpiDetails(
            @RequestParam double amount
    ) {
        return Map.of(
                "upiUrl", pdfService.generateUpiUrl(amount)
        );
    }

    /* 🔥 PAYMENT DONE + PDF */
    @PostMapping("/fees/{id}/complete")
    public ResponseEntity<?> completeFeePayment(
            @PathVariable Long id,
            @RequestParam double amount
    ) {
        studentService.completeMainFeePayment(id, amount);
        return ResponseEntity.ok("Fee paid & receipt saved");
    }


    @PutMapping("/{id}/total-fee")
    public ResponseEntity<?> updateTotalFee(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body
    ) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        s.setTotalFee(body.get("totalFee"));
        studentRepository.save(s);

        return ResponseEntity.ok().build();
    }



}
