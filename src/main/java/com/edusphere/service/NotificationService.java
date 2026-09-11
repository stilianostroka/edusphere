package com.edusphere.service;

import com.edusphere.entity.Notification;
import com.edusphere.entity.Parent;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.entity.Teacher;
import com.edusphere.repository.NotificationRepository;
import com.edusphere.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;

    public NotificationService(NotificationRepository notificationRepository, StudentRepository studentRepository) {
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
    }

    public Notification sendToStudent(Teacher sentBy, Student targetStudent, String message) {
        Notification notification = Notification.toParentsOfStudent(sentBy, targetStudent, message);
        notificationRepository.save(notification);
        return notification;
    }

    public Notification sendToClass(Teacher sentBy, SchoolClass targetClass, String message) {
        Notification notification = Notification.toAllParentsOfClass(sentBy, targetClass, message);
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> getSentHistory(Teacher teacher) {
        return notificationRepository.findAllBySentBy(teacher);
    }

    public List<Notification> getInboxForParent(Parent parent) {
        List<Student> children = studentRepository.findByParents(parent);

        List<Notification> inbox = new ArrayList<>();
        for (Student child : children) {
            inbox.addAll(notificationRepository.findAllByTargetStudent(child));
            if (child.getSchoolClass() != null) {
                inbox.addAll(notificationRepository.findAllByTargetClass(child.getSchoolClass()));
            }
        }
        return inbox;
    }
}