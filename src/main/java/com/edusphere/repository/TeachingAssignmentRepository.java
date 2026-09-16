package com.edusphere.repository;

import com.edusphere.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment, Long> {
    boolean existsBySchoolClassAndSubject(SchoolClass schoolClass, Subject subject);
    List<TeachingAssignment> findAllByTeacher_Id(Long teacherId);
    Optional<TeachingAssignment> findBySchoolClassAndSubject(SchoolClass schoolClass, Subject subject);
    List<TeachingAssignment> findBySchoolClass(SchoolClass schoolClass);
    List<TeachingAssignment> findByTeacher(Teacher teacher);
    List<TeachingAssignment> findBySchoolClass_AcademicYear(AcademicYear academicYear);
}
