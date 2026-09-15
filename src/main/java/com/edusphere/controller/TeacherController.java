package com.edusphere.controller;

import com.edusphere.dto.TeacherRequest;
import com.edusphere.dto.TeacherResponse;
import com.edusphere.entity.Teacher;
import com.edusphere.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAll(){
        return ResponseEntity.ok(teacherService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacher(@PathVariable Long id){
        Teacher teacher = teacherService.getById(id);
        return ResponseEntity.ok(toResponse(teacher));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody TeacherRequest request){
        Teacher teacher = teacherService.createTeacher(request.email(), request.password(), request.name(), request.surname(), request.gender());
        return ResponseEntity.ok(toResponse(teacher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable Long id, @RequestBody TeacherRequest request){
        Teacher teacher = teacherService.updateTeacher(id, request.email(), request.name(), request.surname());

        return ResponseEntity.ok(toResponse(teacher));
    }

    private TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getUser().getEmail()
        );
    }
}


