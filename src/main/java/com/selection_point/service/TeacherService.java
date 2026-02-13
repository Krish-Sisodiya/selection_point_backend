package com.selection_point.service;

import com.selection_point.entity.Teacher;
import com.selection_point.entity.TeacherSalary;
import com.selection_point.repository.TeacherRepository;
import com.selection_point.repository.TeacherSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TeacherSalaryRepository salaryRepository;

    @Autowired
    private PdfService pdfService;

    private static final String ADMIN_MOBILE = "7389812435";

    // Teacher Save / Register
    public Teacher save(Teacher teacher) {
        Teacher saved = teacherRepository.save(teacher);

        // 📩 SMS (optional)
        smsService.sendSms(
                saved.getMobile(),
                "Welcome to Selection Point!\nYou are registered as a teacher."
        );

        return saved;
    }


    // Teacher Get
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    public Teacher getById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public void deactivate(Long id) {
        Teacher t = getById(id);
        t.setActive(false);
        teacherRepository.save(t);
    }

    public long activeCount() {
        return teacherRepository.countByActiveTrue();
    }

    // Teacher Status
    public void approve(Long id) {
        Teacher t = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        t.setStatus(Teacher.Status.APPROVED);
        t.setActive(true);
        teacherRepository.save(t);

        // 📩 SMS
        smsService.sendSms(
                t.getMobile(),
                "🎉 You are approved as a Teacher at Selection Point!"
        );
    }

    // Teacher Update / Delete System
    public Teacher update(Long id, Teacher updated) {
        Teacher t = getById(id);
        t.setName(updated.getName());
        t.setMobile(updated.getMobile());
        t.setEmail(updated.getEmail());
        t.setSubject(updated.getSubject());
        t.setQualification(updated.getQualification());
        return teacherRepository.save(t);
    }

    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }


    // 💰 PAY SALARY
    public TeacherSalary paySalary(Long teacherId, TeacherSalary salary) {

        Teacher teacher = getById(teacherId);

        if (salaryRepository.existsByTeacherAndMonth(teacher, salary.getMonth())) {
            throw new RuntimeException("Salary already paid");
        }

        salary.setTeacher(teacher);
        salary.setPaid(true);
        salary.setPaidDate(LocalDate.now());
        salary.setPaidBy("ADMIN");

        TeacherSalary saved = salaryRepository.save(salary);

        salaryRepository.save(saved);

        return saved;
    }

    public byte[] getSalaryReceiptPdf(Long salaryId) {

        TeacherSalary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("Salary not found"));

        return pdfService.generateSalaryReceiptPdf(salary);
    }


    // 📊 STATS
    public Map<String, Object> stats() {
        return Map.of(
                "total", teacherRepository.count(),
                "approved", teacherRepository.countByStatus(Teacher.Status.APPROVED),
                "pending", teacherRepository.countByStatus(Teacher.Status.PENDING)
        );
    }


}
