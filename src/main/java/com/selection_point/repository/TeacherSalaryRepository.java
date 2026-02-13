package com.selection_point.repository;

import com.selection_point.entity.Teacher;
import com.selection_point.entity.TeacherSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


import java.util.List;

public interface TeacherSalaryRepository extends JpaRepository<TeacherSalary, Long> {

    List<TeacherSalary> findByTeacherId(Long teacherId);
    boolean existsByTeacherAndMonth(Teacher teacher, String month);

    Optional<TeacherSalary> findById(Long id);


}
