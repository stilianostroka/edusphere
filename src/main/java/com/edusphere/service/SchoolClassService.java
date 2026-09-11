package com.edusphere.service;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Teacher;
import com.edusphere.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClassService(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    public SchoolClass createSchoolClass(AcademicYear academicYear,
                                         String className, String classYear, Integer maxStudents){
        if(schoolClassRepository.existsByAcademicYearAndClassName(academicYear,className)){
            throw new IllegalArgumentException("This class "+className+" already exists for this academic year.");
        }

        SchoolClass schoolClass = new SchoolClass(academicYear,className,classYear,maxStudents);
        schoolClassRepository.save(schoolClass);

        return schoolClass;
    }

    public SchoolClass assignSupervisor(AcademicYear academicYear, SchoolClass schoolClass, Teacher teacher){
        if(schoolClassRepository.existsByAcademicYearAndSupervisorTeacher(academicYear,teacher)){
            throw new IllegalArgumentException("Teacher: " + teacher.getFirstName()
                    + teacher.getLastName() +
                    "already supervises a class in this academic year!");
        }
        schoolClass.setSupervisorTeacher(teacher);
        schoolClassRepository.save(schoolClass);

        return schoolClass;
    }


}


