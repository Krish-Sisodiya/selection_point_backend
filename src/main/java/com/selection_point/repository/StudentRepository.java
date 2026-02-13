package com.selection_point.repository;

import com.selection_point.dto.SchoolScatterDTO;
import com.selection_point.entity.School;
import com.selection_point.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // ========= REVENUE =========
    // 💰 Total revenue (fees − discount)
    @Query("""
    select COALESCE(
        sum(
            COALESCE(s.registrationFee,0) +
            COALESCE(s.mainFee,0) -
            COALESCE(s.discountAmount,0)
        ), 0)
    from Student s
    where s.paymentDone = true
""")
    Double getTotalRevenue();


    // ========= ADMISSIONS =========
    @Query("SELECT COUNT(s) FROM Student s WHERE s.admissionDate >= :startDate")
    Long countAdmissionsAfter(@Param("startDate") LocalDate startDate);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.admissionDate = CURRENT_DATE")
    long countTodayAdmissions();

    // ========= FEES =========
    long countByPaymentDone(boolean paymentDone);

    long countByMainFeePaid(boolean mainFeePaid);

    // ========= STATUS =========

    @Query("select count(s) from Student s where s.admissionStatus = :status")
    long countByAdmissionStatus(@Param("status") Student.AdmissionStatus status);


    List<Student> findByBatchId(Long batchId);

    @Query(""" 
            select new com.selection_point.dto.SchoolScatterDTO( s.school.id, count(s.id),s.school.schoolName ) from Student s where s.school is not null group by s.school.id, s.school.schoolName """)
    List<SchoolScatterDTO> getStudentsPerSchoolScatter();

    // History For Students
// 🟢 Active students
    @Query("select count(s) from Student s")
    long countActiveStudents();

    // Deleted students
    long count(); // DeletedStudentRepository


    // 📦 Total admissions
    @Query("select count(s) from Student s")
    long totalAdmissions();

   // Student registration ke time wale schools
    @Query("select count(distinct s.school.id) from Student s where s.school is not null")
    long totalSchools();

    // 💰 Monthly revenue (BAR GRAPH)
    @Query("""
    select MONTH(s.admissionDate),
           COALESCE(
             sum(
               COALESCE(s.registrationFee,0) +
               COALESCE(s.mainFee,0) -
               COALESCE(s.discountAmount,0)
             ), 0)
    from Student s
    where s.paymentDone = true
      and s.admissionDate is not null
    group by MONTH(s.admissionDate)
    order by MONTH(s.admissionDate)
""")
    List<Object[]> monthlyRevenue();


    // 📊 Monthly admissions (BAR GRAPH)
    @Query("""
        select MONTH(s.admissionDate), count(s)
        from Student s
        group by MONTH(s.admissionDate)
        order by MONTH(s.admissionDate)
    """)
    List<Object[]> monthlyAdmissions();

    @Query("""
    select s from Student s
    left join fetch s.school
    left join fetch s.batch
    where s.id = :id
""")
    Optional<Student> findByIdWithSchoolAndBatch(@Param("id") Long id);


    @Query("""
    select count(s)
    from Student s
    where s.studentStatus <> 'DROPPED'
""")
    long totalActiveStudents();

    List<Student> findByStudentStatusNot(Student.StudentStatus status);


    @Query("""
    select distinct s.school.schoolName
    from Student s
    where lower(s.school.schoolName) like lower(concat('%', :q, '%'))
""")
    List<String> searchSchoolNames(@Param("q") String q);




}
