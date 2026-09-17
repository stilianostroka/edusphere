package com.edusphere.repository;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.GradingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradingPeriodRepository extends JpaRepository<GradingPeriod, Long> {
    List<GradingPeriod> findByAcademicYear(AcademicYear academicYear);
    Optional<GradingPeriod> findByAcademicYearAndSequenceNumber(AcademicYear academicYear, Integer sequenceNumber);
}
