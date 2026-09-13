package com.edusphere.repository;

import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    Optional<Teacher> findByUser(User user);

    List<TeachingAssignment> findAllById(Long id);
}
