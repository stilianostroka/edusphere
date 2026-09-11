package com.edusphere.service;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.GradingPeriod;
import com.edusphere.repository.AcademicYearRepository;
import com.edusphere.repository.GradingPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final GradingPeriodRepository gradingPeriodRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository,
                                   GradingPeriodRepository gradingPeriodRepository) {
        this.academicYearRepository = academicYearRepository;
        this.gradingPeriodRepository = gradingPeriodRepository;
    }

    @Transactional
    public AcademicYear createAcademicYear(String label, LocalDate yearStart,
                                           LocalDate yearEnd, LocalDate period1End,
                                           LocalDate period2End){
        if(academicYearRepository.findByLabel(label).isPresent()){
            throw new IllegalArgumentException("An academic year with "+ label +" label already exists.");
        }

        AcademicYear academicYear = new AcademicYear(label,yearStart,yearEnd);
        academicYearRepository.save(academicYear);

        gradingPeriodRepository.save(new GradingPeriod(academicYear,1,yearStart,period1End));
        gradingPeriodRepository.save(new GradingPeriod(academicYear,2,period1End,period2End));
        gradingPeriodRepository.save(new GradingPeriod(academicYear,3,period2End,yearEnd));

        return academicYear;
    }
}


