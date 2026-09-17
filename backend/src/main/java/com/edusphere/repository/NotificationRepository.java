package com.edusphere.repository;

import com.edusphere.entity.Notification;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByTargetStudent(Student targetStudent);
    List<Notification> findAllByTargetClass(SchoolClass targetClass);
    List<Notification> findAllBySentBy(Teacher sentBy);
}
