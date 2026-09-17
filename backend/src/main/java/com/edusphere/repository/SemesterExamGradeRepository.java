package com.edusphere.repository;

import com.edusphere.entity.GradingPeriod;
import com.edusphere.entity.SemesterExamGrade;
import com.edusphere.entity.Student;
import com.edusphere.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterExamGradeRepository extends JpaRepository<SemesterExamGrade, Long> {
    Optional<SemesterExamGrade> findByStudentAndGradingPeriodAndTeachingAssignment(Student student,
                                                                                   GradingPeriod gradingPeriod,
                                                                                   TeachingAssignment teachingAssignment);

    List<SemesterExamGrade> findByStudentAndTeachingAssignment(Student student, TeachingAssignment teachingAssignment);
}
