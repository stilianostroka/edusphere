package com.edusphere.controller;

import com.edusphere.dto.AssignSupervisorRequest;
import com.edusphere.dto.SchoolClassRequest;
import com.edusphere.dto.SchoolClassResponse;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Teacher;
import com.edusphere.service.SchoolClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @GetMapping
    public ResponseEntity<List<SchoolClassResponse>> getAllClasses(){
       return ResponseEntity.ok(schoolClassService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{classId}")
    public ResponseEntity<SchoolClassResponse> getSchoolClass(@PathVariable Long classId){
        SchoolClass schoolClass = schoolClassService.getById(classId);

        return ResponseEntity.ok(toResponse(schoolClass));
    }


    @PostMapping("/create")
    public ResponseEntity<SchoolClassResponse> create(@RequestBody SchoolClassRequest request){
        SchoolClass schoolClass = schoolClassService.createSchoolClass(request.academicYearId(), request.className(),
                request.classYear(), request.maxStudents());

        return ResponseEntity.ok(toResponse(schoolClass));
    }

    @PutMapping("/{classId}/assign-supervisor")
    public ResponseEntity<SchoolClassResponse> assignSupervisor(@PathVariable Long classId,
                                                                @RequestBody AssignSupervisorRequest request){
        SchoolClass updated = schoolClassService.assignSupervisor(classId, request.teacherId());

        return ResponseEntity.ok(toResponse(updated));
    }

    private SchoolClassResponse toResponse(SchoolClass schoolClass) {
        Teacher supervisor = schoolClass.getSupervisorTeacher();
        Long supervisorId = supervisor != null ? supervisor.getId() : null;
        String supervisorName = supervisor != null ? supervisor.getFullName() : null;

        return new SchoolClassResponse(schoolClass.getId(), schoolClass.getClassName(),
                schoolClass.getClassYear(), schoolClass.getMaxStudents(), schoolClass.getAcademicYear().getAcademicYear(),
                supervisorId, supervisorName);
    }

}


