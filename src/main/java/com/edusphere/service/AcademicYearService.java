package com.edusphere.service;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.GradingPeriod;
import com.edusphere.repository.AcademicYearRepository;
import com.edusphere.repository.GradingPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final GradingPeriodRepository gradingPeriodRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository,
                                   GradingPeriodRepository gradingPeriodRepository) {
        this.academicYearRepository = academicYearRepository;
        this.gradingPeriodRepository = gradingPeriodRepository;
    }

    public AcademicYear getById(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No academic year found with id " + id));
    }

    public Optional<AcademicYear> getByLabel(String label){
        return academicYearRepository.findByLabel(label);
    }
    public List<AcademicYear> getAll() {
        return academicYearRepository.findAll();
    }

    public AcademicYear getActive(){
        return academicYearRepository.findByActive(true)
                .orElseThrow(() ->  new IllegalArgumentException("No active academic year currently."));
    }

    public List<GradingPeriod> getGradingPeriods(AcademicYear year) {
        return gradingPeriodRepository.findByAcademicYear(year);
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

    public void setCurrentYear(AcademicYear newCurrentYear) {
        academicYearRepository.findByActive(true).ifPresent(oldCurrent -> {
            oldCurrent.setActive(false);
            academicYearRepository.save(oldCurrent);
        });

        newCurrentYear.setActive(true);
        academicYearRepository.save(newCurrentYear);
    }

    public void deactivate(AcademicYear academicYear){
        academicYearRepository.findByActive(true).ifPresent(oldCurrent ->{
            oldCurrent.setActive(false);
            academicYearRepository.save(oldCurrent);
        });
    }


}


