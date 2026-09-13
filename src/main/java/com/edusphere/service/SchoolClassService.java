package com.edusphere.service;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Teacher;
import com.edusphere.repository.AcademicYearRepository;
import com.edusphere.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearService academicYearService;
    private final TeacherService teacherService;

    public SchoolClassService(SchoolClassRepository schoolClassRepository, AcademicYearRepository academicYearRepository, AcademicYearService academicYearService, TeacherService teacherService) {
        this.schoolClassRepository = schoolClassRepository;
        this.academicYearRepository = academicYearRepository;
        this.academicYearService = academicYearService;
        this.teacherService = teacherService;
    }

    public SchoolClass getById(Long id) {
        return schoolClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No class found with id " + id));
    }

    public List<SchoolClass> getAll(){
        return schoolClassRepository.findAll();
    }

    public SchoolClass createSchoolClass(Long academicYearId,
                                         String className, String classYear, Integer maxStudents){
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new IllegalArgumentException("No academic found with this ID."));

        if(schoolClassRepository.existsByAcademicYearAndClassName(academicYear,className)){
            throw new IllegalArgumentException("This class "+className+" already exists for this academic year.");
        }

        SchoolClass schoolClass = new SchoolClass(academicYear,className,classYear,maxStudents);
        schoolClassRepository.save(schoolClass);

        return schoolClass;
    }

    public SchoolClass assignSupervisor(Long schoolClassId, Long teacherId) {
        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId).orElseThrow(() -> new IllegalArgumentException("No class found with this ID."));
        AcademicYear academicYear = academicYearService.getById(schoolClass.getAcademicYear().getId());
        Teacher teacher = teacherService.getById(teacherId);
        if (schoolClassRepository.existsByAcademicYearAndSupervisorTeacher(academicYear, teacher)) {
            throw new IllegalArgumentException("Teacher: " + teacher.getFirstName() + " "
                    + teacher.getLastName() +
                    " already supervises a class in this academic year!");
        }
        schoolClass.setSupervisorTeacher(teacher);
        schoolClassRepository.save(schoolClass);

        return schoolClass;
    }
}


