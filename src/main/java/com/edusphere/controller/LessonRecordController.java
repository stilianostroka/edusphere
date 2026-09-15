package com.edusphere.controller;

import com.edusphere.dto.JustifyAbsenceRequest;
import com.edusphere.dto.LessonRecordRequest;
import com.edusphere.dto.LessonRecordResponse;
import com.edusphere.dto.MarkAbsentRequest;
import com.edusphere.dto.UpdateGradeRequest;
import com.edusphere.entity.LessonRecord;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.LessonRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-records")
public class LessonRecordController {

    private final LessonRecordService lessonRecordService;
    private final CurrentUserProvider currentUserProvider;


    public LessonRecordController(LessonRecordService lessonRecordService, CurrentUserProvider currentUserProvider) {
        this.lessonRecordService = lessonRecordService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<LessonRecordResponse>> getByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(lessonRecordService.getByTopic(topicId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/student/{studentId}/absences")
    public ResponseEntity<List<LessonRecordResponse>> getStudentAbsences(@PathVariable Long studentId) {
        return ResponseEntity.ok(lessonRecordService.getByStudentAbsences(studentId).stream().map(this::toResponse).toList());
    }

    @PostMapping("/grade")
    public ResponseEntity<LessonRecordResponse> gradeStudent(@RequestBody LessonRecordRequest request) {
        LessonRecord record = lessonRecordService.gradeStudent(request.topicId(), request.studentId(), request.grade());
        return ResponseEntity.ok(toResponse(record));
    }

    @PostMapping("/absent")
    public ResponseEntity<LessonRecordResponse> markStudentAbsent(@RequestBody MarkAbsentRequest request) {
        LessonRecord record = lessonRecordService.markStudentAbsent(request.topicId(), request.studentId());
        return ResponseEntity.ok(toResponse(record));
    }

    @PutMapping("/{id}/grade")
    public ResponseEntity<LessonRecordResponse> updateGrade(@PathVariable Long id, @RequestBody UpdateGradeRequest request) {
        lessonRecordService.updateGrade(id, request.grade());
        return ResponseEntity.ok(toResponse(lessonRecordService.getById(id)));
    }

    @PutMapping("/{id}/mark-absent")
    public ResponseEntity<LessonRecordResponse> markAbsent(@PathVariable Long id) {
        lessonRecordService.markAbsent(id);
        return ResponseEntity.ok(toResponse(lessonRecordService.getById(id)));
    }

    @PutMapping("/{id}/justify")
    public ResponseEntity<LessonRecordResponse> justifyAbsence(@PathVariable Long id, @RequestBody JustifyAbsenceRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        lessonRecordService.justifyAbsence(id, teacherId, request.note());
        return ResponseEntity.ok(toResponse(lessonRecordService.getById(id)));
    }

    private LessonRecordResponse toResponse(LessonRecord record) {
        return new LessonRecordResponse(
                record.getId(),
                record.getTopic().getId(),
                record.getStudent().getId(),
                record.getStudent().getFirstName(),
                record.getStudent().getLastName(),
                record.getGrade(),
                record.isAbsent(),
                record.isJustified(),
                record.getJustificationNote()
        );
    }
}