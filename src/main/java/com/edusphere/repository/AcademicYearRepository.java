package com.edusphere.repository;

import com.edusphere.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear,Long> {
    Optional<AcademicYear> findByLabel(String label);
}
