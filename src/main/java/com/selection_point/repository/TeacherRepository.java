package com.selection_point.repository;

import com.selection_point.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    long countByActiveTrue();

    long countByStatus(Teacher.Status status);

    long countByCreatedDate(LocalDate date);

    @Query("select t.name from Teacher t where t.active = true")
    List<String> getTeacherNames();


}
