package com.edusphere.repository;

import com.edusphere.entity.FinalGradeStatus;
import com.edusphere.entity.FinalSubjectGrade;
import com.edusphere.entity.Student;
import com.edusphere.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinalSubjectGradeRepository extends JpaRepository<FinalSubjectGrade, Long> {
    Optional<FinalSubjectGrade> findByStudentAndTeachingAssignment(Student student,
                                                                   TeachingAssignment teachingAssignment);
    List<FinalSubjectGrade> findAllByStatus(FinalGradeStatus status);
}
