package com.edusphere.repository;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.Diploma;
import com.edusphere.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DiplomaRepository extends JpaRepository<Diploma, Long> {
    Optional<Diploma> findByStudentAndAcademicYear(Student student, AcademicYear academicYear);
    List<Diploma> findByStudent(Student student);
}
