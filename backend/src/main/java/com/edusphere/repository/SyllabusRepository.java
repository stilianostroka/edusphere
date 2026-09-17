package com.edusphere.repository;

import com.edusphere.entity.Syllabus;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    Optional<Syllabus> findByTeachingAssignment(TeachingAssignment teachingAssignment);
    List<Syllabus> findByTeachingAssignment_Teacher(Teacher teacher);
}
