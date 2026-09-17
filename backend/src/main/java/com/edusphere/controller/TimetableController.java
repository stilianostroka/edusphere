package com.edusphere.controller;

import com.edusphere.dto.TimetableSlotRequest;
import com.edusphere.dto.TimetableSlotResponse;
import com.edusphere.entity.TimetableSlot;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;
    private final CurrentUserProvider currentUserProvider;

    public TimetableController(TimetableService timetableService, CurrentUserProvider currentUserProvider) {
        this.timetableService = timetableService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimetableSlotResponse> create(@RequestBody TimetableSlotRequest request) {
        Long adminUserId = currentUserProvider.getCurrentUserId();
        TimetableSlot slot = timetableService.createTimeSlot(
                adminUserId, request.teachingAssignmentId(),
                request.dayOfWeek(), request.startTime(), request.endTime()
        );
        return ResponseEntity.ok(toResponse(slot));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimetableSlotResponse> reschedule(@PathVariable Long id, @RequestBody TimetableSlotRequest request) {
        Long adminUserId = currentUserProvider.getCurrentUserId();
        TimetableSlot slot = timetableService.rescheduleSlot(
                adminUserId, id, request.dayOfWeek(), request.startTime(), request.endTime()
        );
        return ResponseEntity.ok(toResponse(slot));
    }

    @GetMapping("/teaching-assignment/{teachingAssignmentId}")
    public ResponseEntity<List<TimetableSlotResponse>> getByTeachingAssignment(@PathVariable Long teachingAssignmentId) {
        return ResponseEntity.ok(
                timetableService.getByTeachingAssignment(teachingAssignmentId).stream().map(this::toResponse).toList()
        );
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TimetableSlotResponse>> getByTeacher(@PathVariable Long teacherId, @RequestParam(required = false) Long academicYearId) {
        List<TimetableSlot> slots = timetableService.getByTeacher(teacherId);
        if (academicYearId != null) {
            slots = slots.stream()
                    .filter(s -> s.getTeachingAssignment().getSchoolClass().getAcademicYear().getId().equals(academicYearId)).toList();
        }
        return ResponseEntity.ok(slots.stream().map(this::toResponse).toList());
    }

    private TimetableSlotResponse toResponse(TimetableSlot slot) {
        return new TimetableSlotResponse(
                slot.getId(),
                slot.getTeachingAssignment().getId(),
                slot.getTeachingAssignment().getSchoolClass().getClassName(),
                slot.getTeachingAssignment().getSubject().getSubjectName(),
                slot.getTeachingAssignment().getTeacher().getFirstName() + " " + slot.getTeachingAssignment().getTeacher().getLastName(),
                slot.getDayOfWeek(),
                slot.getStartTime(),
                slot.getEndTime()
        );
    }
}
