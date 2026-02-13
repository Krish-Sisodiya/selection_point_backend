package com.selection_point.controller;

import com.selection_point.dto.BatchDTO;
import com.selection_point.dto.StudentDTO;
import com.selection_point.entity.Batch;
import com.selection_point.entity.Student;
import com.selection_point.mapper.BatchMapper;
import com.selection_point.mapper.StudentMapper;
import com.selection_point.repository.BatchRepository;
import com.selection_point.repository.StudentRepository;
import com.selection_point.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/batches")
public class BatchController {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<BatchDTO> all() {
        return batchRepository.findAllOrdered()
                .stream()
                .map(BatchMapper::toDTO)
                .toList();
    }


    @GetMapping("/{id}")
    public BatchDTO getBatch(@PathVariable Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        return BatchMapper.toDTO(batch);
    }


    @GetMapping("/{id}/students")
    public List<StudentDTO> getBatchStudents(@PathVariable Long id) {
        return studentRepository.findByBatchId(id)
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
    }


    @PutMapping("/students/{studentId}/remove")
    public void removeStudentFromBatch(@PathVariable Long studentId) {
        studentService.removeFromBatch(studentId);
    }

}

