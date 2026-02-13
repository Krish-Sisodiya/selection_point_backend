package com.selection_point.service;

import com.selection_point.entity.School;
import com.selection_point.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepo;

    @Autowired
    private SmsService smsService;

    // ✅ REGISTER SCHOOL
    public School registerSchool(School school) {

        String offerCode = generateOfferCode(school.getSchoolName());
        school.setOfferCode(offerCode);

        School saved = schoolRepo.save(school);

        // 📩 SMS TO TEACHER
        String msg =
                "Hello " + saved.getTeacherName() + ",\n\n" +
                        "Your school is registered with Selection Point.\n" +
                        "Offer Code: " + offerCode + "\n\n" +
                        "Use this code for student admissions.";

        smsService.sendSms(saved.getTeacherMobile(), msg);

        return saved;
    }

    // ✅ GET ALL SCHOOLS
    public List<School> getAllSchools() {
        return schoolRepo.findAll();
    }

    // 🔐 OFFER CODE GENERATOR
    private String generateOfferCode(String schoolName) {
        return schoolName.substring(0, 3).toUpperCase()
                + (System.currentTimeMillis() % 10000);
    }

    // ✅ GET BY ID
    public School getById(Long id) {
        return schoolRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("School not found"));
    }

    // ✅ UPDATE
    public School updateSchool(Long id, School updated) {
        School school = getById(id);

        school.setSchoolName(updated.getSchoolName());
        school.setAddress(updated.getAddress());
        school.setTeacherName(updated.getTeacherName());
        school.setTeacherMobile(updated.getTeacherMobile());
        school.setActive(updated.isActive());

        // ❌ offerCode update allowed nahi
        return schoolRepo.save(school);
    }

    // ✅ DELETE
    public void deleteSchool(Long id) {
        if (!schoolRepo.existsById(id)) {
            throw new RuntimeException("School not found");
        }
        schoolRepo.deleteById(id);
    }
}
