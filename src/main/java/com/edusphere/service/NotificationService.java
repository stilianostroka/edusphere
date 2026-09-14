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
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final ParentService parentService;

    public NotificationService(NotificationRepository notificationRepository, StudentRepository studentRepository, TeacherService teacherService, StudentService studentService, SchoolClassService schoolClassService, ParentService parentService) {
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
        this.parentService = parentService;
    }

    public Notification sendToStudent(Long sentById, Long targetStudentId, String message) {
        Teacher sentBy = teacherService.getById(sentById);
        Student targetStudent = studentService.getStudent(targetStudentId);
        Notification notification = Notification.toParentsOfStudent(sentBy, targetStudent, message);
        notificationRepository.save(notification);
        return notification;
    }

    public Notification sendToClass(Long sentById, Long targetClassId, String message) {
        Teacher sentBy = teacherService.getById(sentById);
        SchoolClass targetClass = schoolClassService.getById(targetClassId);
        Notification notification = Notification.toAllParentsOfClass(sentBy, targetClass, message);
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> getSentHistory(Long teacherId) {
        Teacher teacher = teacherService.getById(teacherId);
        return notificationRepository.findAllBySentBy(teacher);
    }

    public List<Notification> getInboxForParent(Long parentId) {
        Parent parent = parentService.getById(parentId);
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