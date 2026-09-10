package com.edusphere.repository;

import com.edusphere.entity.Teacher;
import com.edusphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {
    Optional<Teacher> findByUser(User user);
}
