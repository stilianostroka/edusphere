package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.FinalSubjectGradeRepository;
import com.edusphere.repository.GradingPeriodRepository;
import com.edusphere.repository.LessonRecordRepository;
import com.edusphere.repository.SemesterExamGradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GradingService {

    private final FinalSubjectGradeRepository finalSubjectGradeRepository;
    private final SemesterExamGradeRepository semesterExamGradeRepository;
    private final LessonRecordRepository lessonRecordRepository;
    private final GradingPeriodRepository gradingPeriodRepository;

    public GradingService(FinalSubjectGradeRepository finalSubjectGradeRepository,
                          SemesterExamGradeRepository semesterExamGradeRepository,
                          LessonRecordRepository lessonRecordRepository,
                          GradingPeriodRepository gradingPeriodRepository) {
        this.finalSubjectGradeRepository = finalSubjectGradeRepository;
        this.semesterExamGradeRepository = semesterExamGradeRepository;
        this.lessonRecordRepository = lessonRecordRepository;
        this.gradingPeriodRepository = gradingPeriodRepository;
    }

    public FinalSubjectGrade setProjectGrade(TeachingAssignment teachingAssignment,
                                             Student student, Integer projectGrade) {
        FinalSubjectGrade finalSubjectGrade = finalSubjectGradeRepository
                .findByStudentAndTeachingAssignment(student, teachingAssignment)
                .orElseGet(() -> new FinalSubjectGrade(student, teachingAssignment));
        finalSubjectGrade.setProjectGrade(projectGrade);
        finalSubjectGradeRepository.save(finalSubjectGrade);
        return finalSubjectGrade;
    }

    public SemesterExamGrade recordExamGrade(TeachingAssignment teachingAssignment,
                                             Student student, GradingPeriod gradingPeriod, Integer examGrade) {
        Optional<SemesterExamGrade> existing = semesterExamGradeRepository.
                findByStudentAndGradingPeriodAndTeachingAssignment(student, gradingPeriod, teachingAssignment);
        if (existing.isPresent()) {
            existing.get().setExamGrade(examGrade);
            semesterExamGradeRepository.save(existing.get());
            return existing.get();
        }
        SemesterExamGrade semesterExamGrade = new SemesterExamGrade(student,
                teachingAssignment, gradingPeriod, examGrade);
        semesterExamGradeRepository.save(semesterExamGrade);
        return semesterExamGrade;
    }

    public double calculatePeriodCeg(TeachingAssignment teachingAssignment,
                                     GradingPeriod gradingPeriod, Student student) {
        List<LessonRecord> records =
                lessonRecordRepository.
                        findByStudentAndTopic_TeachingAssignmentAndTopic_DateBetweenAndGradeIsNotNull
                        (student, teachingAssignment,
                                gradingPeriod.getStartDate(), gradingPeriod.getEndDate());
        if (records.isEmpty()) {
            return 0.0;
        }
        return records.stream()
                .mapToInt(LessonRecord::getGrade)
                .average()
                .orElseThrow();
    }

    public double calculateCceg(Student student, TeachingAssignment teachingAssignment) {
        AcademicYear academicYear = teachingAssignment.getSchoolClass().getAcademicYear();
        List<GradingPeriod> periods = gradingPeriodRepository.findByAcademicYear(academicYear);
        return periods.stream()
                .mapToDouble(period ->
                        calculatePeriodCeg(teachingAssignment,period,student))
                .average()
                .orElseThrow();
    }

    public double calculateCfe(Student student, TeachingAssignment teachingAssignment) {
        List<SemesterExamGrade> examGrades = semesterExamGradeRepository.
                findByStudentAndTeachingAssignment(student, teachingAssignment);
        if (examGrades.isEmpty()) {
            return 0.0;
        }
        return examGrades.stream()
                .mapToInt(SemesterExamGrade::getExamGrade)
                .average()
                .orElseThrow();
    }

    @Transactional
    public void submitFinalGrade(FinalSubjectGrade finalSubjectGrade) {
        Student student = finalSubjectGrade.getStudent();
        TeachingAssignment teachingAssignment = finalSubjectGrade.getTeachingAssignment();

        double rawCceg = calculateCceg(student, teachingAssignment);
        double rawCfe = calculateCfe(student, teachingAssignment);

        Integer cceg = (int) Math.round(rawCceg);
        Integer cfe = (int) Math.round(rawCfe);

        finalSubjectGrade.submit(cceg, cfe);
        finalSubjectGradeRepository.save(finalSubjectGrade);
    }

    public void approveFinalGrade(FinalSubjectGrade finalSubjectGrade, User user) {
        finalSubjectGrade.approve(user);
        finalSubjectGradeRepository.save(finalSubjectGrade);
    }

    public void rejectFinalGrade(FinalSubjectGrade finalSubjectGrade,User user) {
        finalSubjectGrade.reject(user);
        finalSubjectGradeRepository.save(finalSubjectGrade);
    }
}



