package com.edusphere.service;

import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Subject;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.repository.TeachingAssignmentRepository;
import com.edusphere.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
public class TeachingAssignmentService {

    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final TopicRepository topicRepository;

    public TeachingAssignmentService(TeachingAssignmentRepository teachingAssignmentRepository, TopicRepository topicRepository) {
        this.teachingAssignmentRepository = teachingAssignmentRepository;
        this.topicRepository = topicRepository;
    }

    public TeachingAssignment createAssignment(SchoolClass schoolClass, Teacher teacher, Subject subject){
        if(teachingAssignmentRepository.existsBySchoolClassAndSubject(schoolClass,subject)) {
            throw new IllegalArgumentException("Subject " + subject.getSubjectName() + " already has a teacher assigned for class " + schoolClass.getClassName()
            );
        }

            TeachingAssignment teachingAssignment = new TeachingAssignment(schoolClass,subject,teacher);
            teachingAssignmentRepository.save(teachingAssignment);
            return teachingAssignment;
    }

    public long calculateHoursTaught(TeachingAssignment teachingAssignment){
        return topicRepository.countByTeachingAssignment(teachingAssignment);
    }

    public long calculateRemainingHours(TeachingAssignment teachingAssignment) {
        long totalHours = teachingAssignment.getSubject().getTotalHours();
        long hoursTaught = calculateHoursTaught(teachingAssignment);
        return totalHours - hoursTaught;
    }
}


