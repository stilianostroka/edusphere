package com.edusphere.repository;

import com.edusphere.entity.Parent;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student,Long> {
    List<Student> findAllBySchoolClass(SchoolClass schoolClass);
    List<Student> findByParents(Parent parent);
    List<Student> findByStatus(StudentStatus status);
}
