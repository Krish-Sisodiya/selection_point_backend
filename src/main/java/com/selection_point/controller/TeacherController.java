package com.selection_point.controller;

import com.selection_point.dto.TeacherDTO;
import com.selection_point.dto.TeacherSalaryDTO;
import com.selection_point.entity.Teacher;
import com.selection_point.entity.TeacherSalary;
import com.selection_point.mapper.TeacherMapper;
import com.selection_point.repository.TeacherRepository;
import com.selection_point.repository.TeacherSalaryRepository;
import com.selection_point.service.PdfService;
import com.selection_point.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.nio.file.Files;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/admin/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherSalaryRepository salaryRepository;

    @Autowired
    private PdfService pdfService;

    // ✅ ALL TEACHERS
    @GetMapping
    public List<TeacherDTO> all() {
        return teacherService.getAll()
                .stream()
                .map(TeacherMapper::toDTO)
                .toList();
    }

    // ✅ CREATE
    @PostMapping
    public TeacherDTO create(@RequestBody Teacher teacher) {
        return TeacherMapper.toDTO(teacherService.save(teacher));
    }

    // ✅ GET BY ID (SAFE)
    @GetMapping("/{id}")
    public TeacherDTO get(@PathVariable Long id) {
        return TeacherMapper.toDTO(teacherService.getById(id));
    }

    // ✅ DEACTIVATE
    @PutMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Long id) {
        teacherService.deactivate(id);
    }

    // ✅ STATS
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        long total = teacherRepository.count();
        long active = teacherRepository.countByActiveTrue();
        long today = teacherRepository.countByCreatedDate(LocalDate.now());

        return Map.of(
                "total", total,
                "active", active,
                "inactive", total - active,
                "today", today
        );
    }

    // ✅ APPROVE
    @PutMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        teacherService.approve(id);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public TeacherDTO update(
            @PathVariable Long id,
            @RequestBody Teacher teacher
    ) {
        return TeacherMapper.toDTO(
                teacherService.update(id, teacher)
        );
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }

    // 💰 PAY SALARY
    @PostMapping("/{id}/salary")
    public TeacherSalaryDTO paySalary(
            @PathVariable Long id,
            @RequestBody TeacherSalary salary
    ) {
        return TeacherMapper.toSalaryDTO(
                teacherService.paySalary(id, salary)
        );
    }

    // 💰 SALARY HISTORY
    @GetMapping("/{id}/salaries")
    public List<TeacherSalaryDTO> salaryHistory(@PathVariable Long id) {
        return salaryRepository.findByTeacherId(id)
                .stream()
                .map(TeacherMapper::toSalaryDTO)
                .toList();
    }

    @PostMapping("/salary/receipt/upload")
    public String uploadReceipt(@RequestParam MultipartFile file) throws Exception {

        String path = "src/main/resources/static/receipts/";
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        File dest = new File(path + file.getOriginalFilename());
        file.transferTo(dest);

        return "/receipts/" + file.getName();
    }

  /*  @GetMapping("/salary/{salaryId}/receipt")
    public ResponseEntity<byte[]> getSalaryReceipt(@PathVariable Long salaryId)
            throws Exception {

        TeacherSalary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        String filePath = "src/main/resources/static" + salary.getReceiptUrl();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("PDF file missing");
        }

        byte[] pdfBytes = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=salary_" + salaryId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }*/

    @GetMapping("/salary/{salaryId}/receipt")
    public ResponseEntity<byte[]> viewReceipt(@PathVariable Long salaryId) {

        byte[] pdf = teacherService.getSalaryReceiptPdf(salaryId);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header(
                        "Content-Disposition",
                        "inline; filename=salary_" + salaryId + ".pdf"
                )
                .body(pdf);
    }


    @GetMapping("/names")
    public List<String> teacherNames() {
        return teacherRepository.getTeacherNames();
    }



}
