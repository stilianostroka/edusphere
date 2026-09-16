package com.edusphere.controller;

import com.edusphere.dto.*;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.security.CurrentUserProvider;
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
    private final CurrentUserProvider currentUserProvider;

    public StudentController(StudentService studentService, SchoolClassService schoolClassService, CurrentUserProvider currentUserProvider) {
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(studentService.getStudent(id)));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll(@RequestParam(required = false) Long academicYearId) {
        List<Student> students = academicYearId != null
                ? studentService.getByAcademicYear(academicYearId)
                : studentService.getAll();
        return ResponseEntity.ok(students.stream().map(this::toResponse).toList());
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
        Student enrolled = studentService.enrollInClass(id, request.classId());
        return ResponseEntity.ok(toResponse(enrolled));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody StudentStatusUpdateRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        studentService.updateStatus(id, teacherId, request.status());
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update (@PathVariable Long id, @RequestBody StudentUpdateRequest request) {
            Student updated = studentService.updateStudent(
                    id, request.firstName(), request.lastName(), request.dateOfBirth(),
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