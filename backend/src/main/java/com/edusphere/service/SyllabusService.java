package com.edusphere.service;

import com.edusphere.entity.Syllabus;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.repository.SyllabusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SyllabusService {

    private final SyllabusRepository syllabusRepository;
    private final TeachingAssignmentService teachingAssignmentService;
    private final TeacherService teacherService;

    public SyllabusService(SyllabusRepository syllabusRepository, TeachingAssignmentService teachingAssignmentService, TeacherService teacherService) {
        this.syllabusRepository = syllabusRepository;
        this.teachingAssignmentService = teachingAssignmentService;
        this.teacherService = teacherService;
    }

    public Syllabus uploadSyllabus(Long teachingAssignmentId, String fileUrl, String originalFileName, String contentType) {
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        Optional<Syllabus> existing = syllabusRepository.findByTeachingAssignment(teachingAssignment);
        if (existing.isPresent()) {
            existing.get().replace(fileUrl, originalFileName, contentType);
            syllabusRepository.save(existing.get());
            return existing.get();
        }
        Syllabus syllabus = new Syllabus(teachingAssignment, fileUrl, originalFileName, contentType);
        syllabusRepository.save(syllabus);
        return syllabus;
    }

    public Syllabus getByTeachingAssignment(Long teachingAssignmentId) {
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        return syllabusRepository.findByTeachingAssignment(teachingAssignment)
                .orElseThrow(() -> new IllegalArgumentException("No syllabus uploaded yet for this course"));
    }

    public List<Syllabus> getByTeacher(Long teacherId) {
        Teacher teacher = teacherService.getById(teacherId);
        return syllabusRepository.findByTeachingAssignment_Teacher(teacher);
    }
}