package com.edusphere.repository;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass,Long> {
    List<SchoolClass> findByAcademicYear(AcademicYear academicYear);
    Optional<SchoolClass> findByAcademicYearAndClassName(AcademicYear academicYear, String className);
    boolean existsByAcademicYearAndClassName(AcademicYear academicYear, String className);
}


