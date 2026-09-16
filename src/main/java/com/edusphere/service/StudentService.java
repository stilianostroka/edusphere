package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.SchoolClassRepository;
import com.edusphere.repository.StudentRepository;
import com.edusphere.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolClassService schoolClassService;
    private final TeacherRepository teacherRepository;
    private final AcademicYearService academicYearService;

    public StudentService(StudentRepository studentRepository, SchoolClassRepository schoolClassRepository, SchoolClassService schoolClassService, TeacherRepository teacherRepository, AcademicYearService academicYearService) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.schoolClassService = schoolClassService;
        this.teacherRepository = teacherRepository;
        this.academicYearService = academicYearService;
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

    public List<Student> getByAcademicYear(Long academicYearId) {
        AcademicYear academicYear = academicYearService.getById(academicYearId);
        return studentRepository.findAllBySchoolClass_AcademicYear(academicYear);
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

    public Student updateStudent(Long studentId, String firstName, String lastName, LocalDate dateOfBirth,
                                 String personalId, String gender, String address) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new IllegalArgumentException("No student found with this ID."));

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

    public Student enrollInClass(Long studentId, Long schoolClassId){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new IllegalArgumentException("No student found with this ID."));
        SchoolClass schoolClass = schoolClassService.getById(studentId);

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

    public void updateStatus(Long studentId, Long teacherId, String status){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No student found with this ID."));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("No teacher found with this ID."));

        student.updateStatus(teacher, StudentStatus.valueOf(status.toUpperCase()));
        studentRepository.save(student);
    }
}


