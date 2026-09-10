package com.edusphere.repository;

import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByTeachingAssignment(TeachingAssignment teachingAssignment);
    long countByTeachingAssignment(TeachingAssignment teachingAssignment);
    List<Topic> findByTeachingAssignmentAndDateBetween(
            TeachingAssignment teachingAssignment,
            LocalDate start,
            LocalDate end
    );

}
