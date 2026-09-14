package com.edusphere.controller;

import com.edusphere.dto.NotificationResponse;
import com.edusphere.dto.NotificationRequestToClass;
import com.edusphere.dto.NotificationRequestToStudent;
import com.edusphere.entity.Notification;
import com.edusphere.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/student")
    public ResponseEntity<NotificationResponse> sendToStudent(@RequestBody NotificationRequestToStudent request) {
        Notification notification = notificationService.sendToStudent(
                request.teacherId(), request.studentId(), request.message()
        );
        return ResponseEntity.ok(toResponse(notification));
    }

    @PostMapping("/class")
    public ResponseEntity<NotificationResponse> sendToClass(@RequestBody NotificationRequestToClass request) {
        Notification notification = notificationService.sendToClass(
                request.teacherId(), request.classId(), request.message()
        );
        return ResponseEntity.ok(toResponse(notification));
    }

    @GetMapping("/teacher/{teacherId}/sent")
    public ResponseEntity<List<NotificationResponse>> getSentHistory(@PathVariable Long teacherId) {
        return ResponseEntity.ok(notificationService.getSentHistory(teacherId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/parent/{parentId}/inbox")
    public ResponseEntity<List<NotificationResponse>> getInbox(@PathVariable Long parentId) {
        return ResponseEntity.ok(notificationService.getInboxForParent(parentId).stream().map(this::toResponse).toList());
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getSentBy().getId(),
                notification.getSentBy().getFirstName() + " " + notification.getSentBy().getLastName(),
                notification.isForStudent() ? notification.getTargetStudent().getId() : null,
                notification.isForStudent()
                        ? notification.getTargetStudent().getFirstName() + " " + notification.getTargetStudent().getLastName()
                        : null,
                notification.isForClass() ? notification.getTargetClass().getId() : null,
                notification.isForClass() ? notification.getTargetClass().getClassName() : null,
                notification.getMessage(),
                notification.getSentAt()
        );
    }
}