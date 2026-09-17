package com.edusphere.controller;

import com.edusphere.dto.ParentMeetingRequest;
import com.edusphere.dto.ParentMeetingResponse;
import com.edusphere.entity.ParentMeeting;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.ParentMeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/parent-meetings")
public class ParentMeetingController {

    private final ParentMeetingService parentMeetingService;
    private final CurrentUserProvider currentUserProvider;
    public ParentMeetingController(ParentMeetingService parentMeetingService, CurrentUserProvider currentUserProvider) {
        this.parentMeetingService = parentMeetingService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ParentMeetingResponse>> getAllByClass(@PathVariable Long classId){
        return ResponseEntity.ok(parentMeetingService.getBySchoolClass(classId).stream().map(this::toResponse).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ParentMeetingResponse> record(@RequestBody ParentMeetingRequest request){
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ParentMeeting parentMeeting = parentMeetingService.recordMeeting(teacherId, request.classId(), request.meetingTime(), request.topic());
        return ResponseEntity.ok(toResponse(parentMeeting));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ParentMeetingResponse> update(@PathVariable Long id, @RequestBody ParentMeetingRequest request){
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ParentMeeting parentMeeting = parentMeetingService.editMeeting(id, teacherId, request.meetingTime(), request.topic());
        return ResponseEntity.ok(toResponse(parentMeeting));
    }

    @DeleteMapping("/{parentMeetingId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable Long parentMeetingId){
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        parentMeetingService.deleteMeeting(parentMeetingId, teacherId);
        return ResponseEntity.noContent().build();
    }

    private ParentMeetingResponse toResponse(ParentMeeting parentMeeting){
        return new ParentMeetingResponse(parentMeeting.getId(),
                parentMeeting.getSchoolClass().getId(),
                parentMeeting.getRecordedBy().getId(),
                parentMeeting.getMeetingDateTime(),
                parentMeeting.getTopicsDiscussed());
    }
}

