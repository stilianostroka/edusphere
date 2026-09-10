package com.edusphere.repository;

import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Subject;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment, Long> {
    boolean existsBySchoolClassAndSubject(SchoolClass schoolClass, Subject subject);
    Optional<TeachingAssignment> findBySchoolClassAndSubject(SchoolClass schoolClass, Subject subject);
    List<TeachingAssignment> findBySchoolClass(SchoolClass schoolClass);
    List<TeachingAssignment> findByTeacher(Teacher teacher);
}
