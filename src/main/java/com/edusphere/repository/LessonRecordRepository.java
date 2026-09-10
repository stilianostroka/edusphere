package com.edusphere.repository;

import com.edusphere.entity.LessonRecord;
import com.edusphere.entity.Student;
import com.edusphere.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LessonRecordRepository extends JpaRepository<LessonRecord, Long> {
    List<LessonRecord> findByStudentAndTopic_TeachingAssignmentAndTopic_DateBetweenAndGradeIsNotNull(
            Student student,
            TeachingAssignment teachingAssignment,
            LocalDate start,
            LocalDate end
    );

    List<LessonRecord> findByStudentAndAbsentTrue(Student student);
    long countByStudentAndAbsentTrue(Student student);

    List<LessonRecord> findByStudentAndAbsentTrueAndJustifiedFalse(Student student);
}


