package com.edusphere.service;

import com.edusphere.entity.Subject;
import com.edusphere.repository.SubjectRepository;
import com.edusphere.repository.TeachingAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeachingAssignmentRepository teachingAssignmentRepository;

    public SubjectService(SubjectRepository subjectRepository, TeachingAssignmentRepository teachingAssignmentRepository) {
        this.subjectRepository = subjectRepository;
        this.teachingAssignmentRepository = teachingAssignmentRepository;
    }

    public Subject createSubject(String subjectName, Integer totalHours, Integer programYear, String code){
        if(subjectRepository.existsByCode(code))
            throw new IllegalArgumentException("A subject with this code already exists.");

        Subject subject = new Subject(totalHours,subjectName,code,programYear);
        subjectRepository.save(subject);
        return subject;
    }

    public List<Subject> getAll(){
        return subjectRepository.findAll();
    }

    public Subject getById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No subject found with id " + id));
    }

    public Subject getByCode(String code){
        return subjectRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("No subject exists with this code."));
    }

    public List<Subject> getByProgramYear(Integer year){
        return subjectRepository.findByProgramYear(year);
    }

    public Subject updateSubject(Long id, String subjectName, String code, Integer totalHours, Integer programYear) {
        Subject subject = getById(id);
        if (!subject.getCode().equals(code) && subjectRepository.existsByCode(code)) {
            throw new IllegalArgumentException("A subject with this code already exists.");
        }
        subject.setSubjectName(subjectName);
        subject.setCode(code);
        subject.setTotalHours(totalHours);
        subject.setProgramYear(programYear);
        subjectRepository.save(subject);
        return subject;
    }

    public void deleteSubject(Long id) {
        Subject subject = getById(id);
        if (teachingAssignmentRepository.existsBySubject(subject)) {
            throw new IllegalStateException(
                    "Subject " + subject.getSubjectName() + " is used by a teaching assignment and cannot be deleted."
            );
        }
        subjectRepository.delete(subject);
    }
}