package com.edusphere.repository;

import com.edusphere.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {
    List<Student> findAllBySchoolClass(SchoolClass schoolClass);
    long countBySchoolClass(SchoolClass schoolClass);
    List<Student> findByParents(Parent parent);
    List<Student> findByStatus(StudentStatus status);
    boolean existsByPersonalId(String personalId);
}
