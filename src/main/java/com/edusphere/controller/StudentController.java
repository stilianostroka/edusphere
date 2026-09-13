package com.edusphere.controller;

import com.edusphere.dto.EnrollStudentRequest;
import com.edusphere.dto.StudentRequest;
import com.edusphere.dto.StudentResponse;
import com.edusphere.dto.StudentUpdateRequest;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.service.SchoolClassService;
import com.edusphere.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final SchoolClassService schoolClassService;

    public StudentController(StudentService studentService, SchoolClassService schoolClassService) {
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(studentService.getStudent(id)));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        return ResponseEntity.ok(studentService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<StudentResponse>> getByClass(@PathVariable Long classId) {
        SchoolClass schoolClass = schoolClassService.getById(classId);
        return ResponseEntity.ok(studentService.getBySchoolClass(schoolClass).stream().map(this::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest request) {
        Student student = studentService.createStudent(request.firstName(), request.lastName(), request.dateOfBirth(), request.personalId());
        return ResponseEntity.ok(toResponse(student));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<StudentResponse> enroll(@PathVariable Long id, @RequestBody EnrollStudentRequest request) {
        Student student = studentService.getStudent(id);
        SchoolClass schoolClass = schoolClassService.getById(request.classId());
        Student enrolled = studentService.enrollInClass(student, schoolClass);
        return ResponseEntity.ok(toResponse(enrolled));
    }

        @PutMapping("/{id}")
        public ResponseEntity<StudentResponse> update (@PathVariable Long id, @RequestBody StudentUpdateRequest request)
        {
            Student student = studentService.getStudent(id);
            Student updated = studentService.updateStudent(
                    student, request.firstName(), request.lastName(), request.dateOfBirth(),
                    request.personalId(), request.gender(), request.address()
            );
            return ResponseEntity.ok(toResponse(updated));
        }

        private StudentResponse toResponse (Student student){
            SchoolClass schoolClass = student.getSchoolClass();
            return new StudentResponse(
                    student.getId(),
                    schoolClass != null ? schoolClass.getId() : null,
                    schoolClass != null ? schoolClass.getClassName() : null,
                    student.getFirstName(),
                    student.getLastName(),
                    student.getStatus().name()
            );
        }
    }