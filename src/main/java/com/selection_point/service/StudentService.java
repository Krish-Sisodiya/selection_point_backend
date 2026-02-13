package com.selection_point.service;

import com.selection_point.entity.Batch;
import com.selection_point.entity.DeletedStudent;
import com.selection_point.entity.School;
import com.selection_point.entity.Student;
import com.selection_point.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private DeletedStudentRepository deletedStudentRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private StudentFeeReceiptRepository receiptRepository;

    // ================= SAVE / REGISTER =================
    @Transactional
    public Student saveStudent(Student student) {

        /* ================= OFFER APPLY ================= */
        if (student.getAppliedOfferCode() != null &&
                !student.getAppliedOfferCode().isBlank()) {

            String code = student.getAppliedOfferCode()
                    .trim()
                    .toUpperCase();

            School school = schoolRepository
                    .findByOfferCode(code)
                    .orElseThrow(() ->
                            new RuntimeException("Invalid Offer Code"));

            double discount = student.getFeeAmount() * 0.10;

            student.setDiscountAmount(discount);
            student.setFeeAmount(student.getFeeAmount() - discount);
            student.setOfferApplied(true);

            if (student.getSchool() == null) {
                student.setSchool(school);
            }
        }

        /* ================= DEFAULT STATES ================= */
        student.setPaymentDone(false);
        student.setMainFeePaid(false);
        student.setAdmissionStatus(Student.AdmissionStatus.PENDING);
        student.setStudentStatus(Student.StudentStatus.ACTIVE);

        /* ================= GET / CREATE BATCH ================= */
        Batch batch = batchService.getOrCreateBatch(
                student.getCoursePackage()
        );

        /* ================= BOTH SIDES SYNC ================= */
        student.setBatch(batch);
        batch.getStudents().add(student);

        /* ================= SAVE STUDENT ================= */
        Student savedStudent = studentRepository.save(student);

        /* ================= UPDATE STRENGTH ================= */
        batch.setCurrentStrength(
                batch.getCurrentStrength() + 1
        );

        batchRepository.saveAndFlush(batch); // 🔥 VERY IMPORTANT

        /* ================= SMS (FAIL SAFE) ================= */
        try {
            smsService.sendSms(
                    savedStudent.getMobile(),
                    "🎉 Welcome to Selection Point!\n" +
                            "Registration Successful\n" +
                            "Batch: " + batch.getBatchName() + "\n" +
                            "Payable Fee: ₹" + savedStudent.getFeeAmount()
            );
        } catch (Exception ignored) {}

        return savedStudent;
    }



    // ================= CRUD =================
    public List<Student> getAllStudents() {
        return studentRepository
                .findByStudentStatusNot(Student.StudentStatus.DROPPED);
    }


    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Student updateStudent(Long id, Student updated) {
        Student s = getStudentById(id);
        s.setName(updated.getName());
        s.setStudentClass(updated.getStudentClass());
        s.setMobile(updated.getMobile());
        s.setAddress(updated.getAddress());
        s.setCoursePackage(updated.getCoursePackage());
        return studentRepository.save(s);
    }

    // ================= Student Delete System =================
    @Transactional
    public void deleteStudent(Long id, String reason, String adminUsername) {

        Student student = studentRepository
                .findByIdWithSchoolAndBatch(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // ===============================
        // 1️⃣ ARCHIVE STUDENT DATA
        // ===============================
        DeletedStudent d = new DeletedStudent();

        d.setOriginalStudentId(student.getId());
        d.setName(student.getName());
        d.setStudentClass(student.getStudentClass());
        d.setMobile(student.getMobile());
        d.setAddress(student.getAddress());

        d.setSchoolName(student.getSchool() != null
                ? student.getSchool().getSchoolName()
                : "NA");

        d.setBatchName(student.getBatch() != null
                ? student.getBatch().getBatchName()
                : "NA");

        d.setFeeAmount(student.getFeeAmount());
        d.setRegistrationFee(student.getRegistrationFee());
        d.setMainFee(student.getMainFee());
        d.setAdmissionDate(student.getAdmissionDate());

        d.setDeletedBy(adminUsername);
        d.setDeleteReason(reason);
        d.setDeletedDate(LocalDate.now());

        deletedStudentRepository.save(d);

        // ===============================
        // 2️⃣ REMOVE FROM BATCH SAFELY
        // ===============================
        if (student.getBatch() != null) {
            Batch batch = student.getBatch();
            batch.setCurrentStrength(
                    Math.max(0, batch.getCurrentStrength() - 1)
            );
            student.setBatch(null);
            batchRepository.save(batch);
        }

        // ===============================
        // 3️⃣ CLEAN ATTENDANCE
        // ===============================
        attendanceRecordRepository.deleteByStudentId(student.getId());

        // ===============================
        // 4️⃣ 🔥 HARD DELETE (IMPORTANT)
        // ===============================
        studentRepository.delete(student); // ✅ REAL DELETE
    }




    // ================= ADMIN ACTIONS =================
    public Student approveFee(Long id) {
        Student s = getStudentById(id);
        s.setMainFeePaid(true);
        s.setAdmissionStatus(Student.AdmissionStatus.APPROVED);
        return studentRepository.save(s);
    }

    // ================= DASHBOARD STATS =================
    public Map<String, Object> stats() {
        return Map.of(
                "total", studentRepository.totalActiveStudents(), // ✅ exclude DROPPED
                "pending", studentRepository.countByAdmissionStatus(Student.AdmissionStatus.PENDING),
                "approved", studentRepository.countByAdmissionStatus(Student.AdmissionStatus.APPROVED),
                "feePending", studentRepository.countByMainFeePaid(false),
                "today", studentRepository.countTodayAdmissions()
        );
    }


    // ================= REMOVE FROM BATCH =================
    @Transactional
    public void removeFromBatch(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Batch batch = student.getBatch();

        if (batch != null) {

            // 🔐 safety check
            if (batch.getStudents() != null) {
                batch.getStudents().remove(student);
            }

            // owner side
            student.setBatch(null);

            // strength safety
            if (batch.getCurrentStrength() > 0) {
                batch.setCurrentStrength(
                        batch.getCurrentStrength() - 1
                );
            }

            batchRepository.saveAndFlush(batch);
        }

        studentRepository.save(student);
    }



    // Deleted Student System
    @Transactional
    public void restoreDeletedStudent(Long deletedId) {

        // 1️⃣ Get deleted student record
        DeletedStudent deleted = deletedStudentRepository.findById(deletedId)
                .orElseThrow(() -> new RuntimeException("Deleted student not found"));

        // 2️⃣ Find original student
        Student student = studentRepository.findById(deleted.getOriginalStudentId())
                .orElseThrow(() -> new RuntimeException("Original student not found"));

        // 3️⃣ Restore student state
        student.setStudentStatus(Student.StudentStatus.ACTIVE);
        student.setActive(true);

        // (Optional) restore fees / status
        student.setAdmissionStatus(Student.AdmissionStatus.PENDING);

        studentRepository.save(student);

        // 4️⃣ Remove from deleted_students table
        deletedStudentRepository.delete(deleted);
    }


    @Transactional
    public void permanentDeleteStudent(Long deletedId) {

        DeletedStudent deleted = deletedStudentRepository.findById(deletedId)
                .orElseThrow(() -> new RuntimeException("Deleted student not found"));

        deletedStudentRepository.delete(deleted); // ✅ ab count kam hoga
    }



    @Transactional
    public Student registerAfterPayment(Student student) {

        // 💰 payment flags
        student.setPaymentDone(true);
        student.setAdmissionStatus(Student.AdmissionStatus.APPROVED);
        student.setStudentStatus(Student.StudentStatus.ACTIVE);

        Student saved = studentRepository.save(student);

        // 📄 Generate PDF
        byte[] pdf = pdfService.generateStudentReceipt(saved);

        // 📩 Send SMS / WhatsApp
        try {
            smsService.sendSms(
                    saved.getMobile(),
                    "✅ Registration Successful!\n" +
                            "Selection Point\n" +
                            "Fee Paid: ₹" + saved.getRegistrationFee()
            );
        } catch (Exception e) {}

        return saved;


    }


    @Transactional
    public void completeMainFeePayment(Long studentId, double amount) {

        Student st = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // ✅ update student
        st.setMainFee(amount);
        st.setMainFeePaid(true);
        st.setPaymentDone(true);
        studentRepository.save(st);

        // ✅ generate & save receipt (salary-style)
        pdfService.generateAndSaveStudentFeeReceipt(
                st,
                amount,
                "MAIN_FEE"
        );

        // ✅ optional SMS
        try {
            smsService.sendSms(
                    st.getMobile(),
                    "✅ Main Fee Paid\nSelection Point\n₹" + amount
            );
        } catch (Exception ignored) {}
    }

    public double getTotalPaidFee(Long studentId) {
        return receiptRepository.getTotalPaidAmount(studentId);
    }




}
