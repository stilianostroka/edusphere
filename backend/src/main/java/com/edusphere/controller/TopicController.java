package com.edusphere.controller;

import com.edusphere.dto.TopicRequest;
import com.edusphere.dto.TopicResponse;
import com.edusphere.entity.Topic;
import com.edusphere.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }


    @GetMapping("/teaching-assignment/{teachingAssignmentId}")
    public ResponseEntity<List<TopicResponse>> getByTeachingAssignment(@PathVariable Long teachingAssignmentId) {
        List<TopicResponse> response = topicService.getAll(teachingAssignmentId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teaching-assignment/{teachingAssignmentId}/range")
    public ResponseEntity<List<TopicResponse>> getByDateRange(
            @PathVariable Long teachingAssignmentId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        List<TopicResponse> response = topicService.getByTeachingAssignmentAndDateRange(teachingAssignmentId, start, end).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TopicResponse> create(@RequestBody TopicRequest request) {
        Topic topic = topicService.createTopic(
                request.teachingAssignmentId(), request.topicName(), request.description(), request.date()
        );
        return ResponseEntity.ok(toResponse(topic));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TopicResponse> update(@PathVariable Long id, @RequestBody TopicRequest request) {
        topicService.updateTopic(id, request.topicName(), request.description(), request.date());
        return ResponseEntity.ok(toResponse(topicService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    private TopicResponse toResponse(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getTeachingAssignment().getId(),
                topic.getTeachingAssignment().getSchoolClass().getClassName(),
                topic.getTeachingAssignment().getSubject().getSubjectName(),
                topic.getDate(),
                topic.getName(),
                topic.getDescription(),
                topic.getCreatedAt()
        );
    }
}
