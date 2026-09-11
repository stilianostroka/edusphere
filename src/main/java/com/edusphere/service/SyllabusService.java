package com.edusphere.service;

import com.edusphere.entity.Syllabus;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.repository.SyllabusRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SyllabusService {

    private final SyllabusRepository syllabusRepository;

    public SyllabusService(SyllabusRepository syllabusRepository) {
        this.syllabusRepository = syllabusRepository;
    }

    public Syllabus uploadSyllabus(TeachingAssignment teachingAssignment, String fileUrl, String originalFileName, String contentType) {
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
}