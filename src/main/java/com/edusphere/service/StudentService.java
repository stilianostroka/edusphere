package com.edusphere.service;

import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(String firstName, String lastName,
                                 LocalDate dateOfBirth, String personalId){
        if(studentRepository.existsByPersonalId(personalId)){
            throw new IllegalArgumentException("Student: "+ firstName +" "+ lastName +" with id number"
                    + personalId +" already exists.");
        }
        Student student = new Student(firstName,lastName,dateOfBirth,personalId);

        studentRepository.save(student);

        return student;
    }

    public Student enrollInClass(Student student, SchoolClass schoolClass){
        if (student.getSchoolClass() != null && student.getSchoolClass().getId().equals(schoolClass.getId())) {
            throw new IllegalArgumentException("This student is already enrolled in this class!");
        }
        long currentEnrollment = studentRepository.countBySchoolClass(schoolClass);
        if (currentEnrollment >= schoolClass.getMaxStudents()) {
            throw new IllegalArgumentException("Class " + schoolClass.getClassName() + " is already at capacity");
        }

        student.setSchoolClass(schoolClass);
        studentRepository.save(student);
        return student;
    }
}


