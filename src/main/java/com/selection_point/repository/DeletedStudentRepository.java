package com.selection_point.repository;

import com.selection_point.entity.DeletedStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedStudentRepository
        extends JpaRepository<DeletedStudent, Long> {
}

