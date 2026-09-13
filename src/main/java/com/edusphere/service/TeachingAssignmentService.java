package com.edusphere.service;

import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Subject;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.repository.TeachingAssignmentRepository;
import com.edusphere.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingAssignmentService {

    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final TopicRepository topicRepository;
    private final SchoolClassService schoolClassService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;

    public TeachingAssignmentService(TeachingAssignmentRepository teachingAssignmentRepository, TopicRepository topicRepository, SchoolClassService schoolClassService, TeacherService teacherService, SubjectService subjectService) {
        this.teachingAssignmentRepository = teachingAssignmentRepository;
        this.topicRepository = topicRepository;
        this.schoolClassService = schoolClassService;
        this.teacherService = teacherService;
        this.subjectService = subjectService;
    }

    public TeachingAssignment createAssignment(Long schoolClassId, Long teacherId, Long subjectId){
        SchoolClass schoolClass = schoolClassService.getById(schoolClassId);
        Teacher teacher = teacherService.getById(teacherId);
        Subject subject = subjectService.getById(subjectId);

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

    public TeachingAssignment getById(Long id){
        return teachingAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No teaching assignment with this ID."));
    }
    public List<TeachingAssignment> getAll(){
        return teachingAssignmentRepository.findAll();
    }

    public List<TeachingAssignment> getByTeacher(Long id){
        return teachingAssignmentRepository.findAllByTeacher_Id(id);
    }
    public long calculateRemainingHours(TeachingAssignment teachingAssignment) {
        long totalHours = teachingAssignment.getSubject().getTotalHours();
        long hoursTaught = calculateHoursTaught(teachingAssignment);
        return totalHours - hoursTaught;
    }
}


