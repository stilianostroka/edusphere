package com.edusphere.repository;

import com.edusphere.entity.LessonRecord;
import com.edusphere.entity.Student;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LessonRecordRepository extends JpaRepository<LessonRecord, Long> {
    List<LessonRecord> findByStudentAndTopic_TeachingAssignmentAndTopic_DateBetweenAndGradeIsNotNull(
            Student student,
            TeachingAssignment teachingAssignment,
            LocalDate start,
            LocalDate end
    );

    List<LessonRecord> findByStudentAndAbsentTrue(Student student);
    long countByStudentAndAbsentTrue(Student student);
    Optional<LessonRecord> findByStudentAndTopic(Student student, Topic topic);
    List<LessonRecord> findByStudentAndAbsentTrueAndJustifiedFalse(Student student);
}


