package com.edusphere.service;

import com.edusphere.entity.Gender;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Student;
import com.edusphere.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getStudent(Long id){
        return studentRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("No student found with this id."));
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public List<Student> getBySchoolClass(SchoolClass schoolClass) {
        return studentRepository.findAllBySchoolClass(schoolClass);
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

    public Student updateStudent(Student student, String firstName, String lastName, LocalDate dateOfBirth,
                                 String personalId, String gender, String address) {
        if (!student.getPersonalId().equals(personalId) && studentRepository.existsByPersonalId(personalId)) {
            throw new IllegalArgumentException("A student with personal id " + personalId + " already exists");
        }

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(dateOfBirth);
        student.setPersonalId(personalId);
        student.setGender(Gender.valueOf(gender.toUpperCase()));
        student.setAddress(address);

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


