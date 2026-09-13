package com.edusphere.service;

import com.edusphere.entity.Subject;
import com.edusphere.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
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
}


