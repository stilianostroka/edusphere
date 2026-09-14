package com.edusphere.controller;

import com.edusphere.dto.ParentMeetingRequest;
import com.edusphere.dto.ParentMeetingResponse;
import com.edusphere.entity.ParentMeeting;
import com.edusphere.service.ParentMeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parent-meetings")
public class ParentMeetingController {

    private final ParentMeetingService parentMeetingService;
    public ParentMeetingController(ParentMeetingService parentMeetingService) {
        this.parentMeetingService = parentMeetingService;
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ParentMeetingResponse>> getAllByClass(@PathVariable Long classId){
        return ResponseEntity.ok(parentMeetingService.getBySchoolClass(classId).stream().map(this::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<ParentMeetingResponse> record(@RequestBody ParentMeetingRequest request){
        ParentMeeting parentMeeting = parentMeetingService.recordMeeting(request.teacherId(),request.classId(),
                request.meetingTime(),request.topic());
        return ResponseEntity.ok(toResponse(parentMeeting));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentMeetingResponse> update(@PathVariable Long id, @RequestBody ParentMeetingRequest request){
        ParentMeeting parentMeeting = parentMeetingService.editMeeting(id, request.teacherId(), request.meetingTime(), request.topic());
        return ResponseEntity.ok(toResponse(parentMeeting));
    }

    @DeleteMapping("/{parentMeetingId}")
    public ResponseEntity<Void> delete(@PathVariable Long parentMeetingId, @RequestParam Long teacherId){
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


