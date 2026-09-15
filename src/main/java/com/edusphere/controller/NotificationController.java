package com.edusphere.controller;

import com.edusphere.dto.NotificationResponse;
import com.edusphere.dto.NotificationRequestToClass;
import com.edusphere.dto.NotificationRequestToStudent;
import com.edusphere.entity.Notification;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;

    public NotificationController(NotificationService notificationService, CurrentUserProvider currentUserProvider) {
        this.notificationService = notificationService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/student")
    public ResponseEntity<NotificationResponse> sendToStudent(@RequestBody NotificationRequestToStudent request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        Notification notification = notificationService.sendToStudent(teacherId, request.studentId(), request.message());
        return ResponseEntity.ok(toResponse(notification));
    }

    @PostMapping("/class")
    public ResponseEntity<NotificationResponse> sendToClass(@RequestBody NotificationRequestToClass request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        Notification notification = notificationService.sendToClass(teacherId, request.classId(), request.message());
        return ResponseEntity.ok(toResponse(notification));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<NotificationResponse>> getSentHistory() {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        return ResponseEntity.ok(notificationService.getSentHistory(teacherId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<NotificationResponse>> getInbox() {
        Long parentId = currentUserProvider.getCurrentParentId();
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