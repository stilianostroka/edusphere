package com.edusphere.service;

import com.edusphere.dto.ClassGradeRosterEntry;
import com.edusphere.dto.PeriodGradeSummary;
import com.edusphere.entity.*;
import com.edusphere.repository.FinalSubjectGradeRepository;
import com.edusphere.repository.GradingPeriodRepository;
import com.edusphere.repository.LessonRecordRepository;
import com.edusphere.repository.SemesterExamGradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GradingService  implements EditWindow{

    private final FinalSubjectGradeRepository finalSubjectGradeRepository;
    private final SemesterExamGradeRepository semesterExamGradeRepository;
    private final LessonRecordRepository lessonRecordRepository;
    private final GradingPeriodRepository gradingPeriodRepository;
    private final StudentService studentService;
    private final TeachingAssignmentService teachingAssignmentService;
    private final UserService userService;

    public GradingService(FinalSubjectGradeRepository finalSubjectGradeRepository,
                          SemesterExamGradeRepository semesterExamGradeRepository,
                          LessonRecordRepository lessonRecordRepository,
                          GradingPeriodRepository gradingPeriodRepository,
                          StudentService studentService,
                          TeachingAssignmentService teachingAssignmentService,
                          UserService userService) {
        this.finalSubjectGradeRepository = finalSubjectGradeRepository;
        this.semesterExamGradeRepository = semesterExamGradeRepository;
        this.lessonRecordRepository = lessonRecordRepository;
        this.gradingPeriodRepository = gradingPeriodRepository;
        this.studentService = studentService;
        this.teachingAssignmentService = teachingAssignmentService;
        this.userService = userService;
    }

    public java.util.List<SemesterExamGrade> getExamGrades(Long studentId, Long teachingAssignmentId) {
        Student student = studentService.getStudent(studentId);
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        return semesterExamGradeRepository.findByStudentAndTeachingAssignment(student, teachingAssignment);
    }

    public FinalSubjectGrade getFinalGradeById(Long id) {
        return finalSubjectGradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No final grade found with id " + id));
    }

    public FinalSubjectGrade getFinalGrade(Long studentId, Long teachingAssignmentId) {
        Student student = studentService.getStudent(studentId);
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        return finalSubjectGradeRepository.findByStudentAndTeachingAssignment(student, teachingAssignment)
                .orElseThrow(() -> new IllegalArgumentException("No final grade exists yet for this student and subject"));
    }

    public FinalSubjectGrade setProjectGrade(Long teachingAssignmentId, Long studentId, Integer projectGrade) {
        Student student = studentService.getStudent(studentId);
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);

        FinalSubjectGrade finalSubjectGrade = finalSubjectGradeRepository
                .findByStudentAndTeachingAssignment(student, teachingAssignment)
                .orElseGet(() -> new FinalSubjectGrade(student, teachingAssignment));
        finalSubjectGrade.setProjectGrade(projectGrade);
        finalSubjectGradeRepository.save(finalSubjectGrade);
        return finalSubjectGrade;
    }

    public SemesterExamGrade recordExamGrade(Long teachingAssignmentId, Long studentId, Long gradingPeriodId, Integer examGrade) {
        Student student = studentService.getStudent(studentId);
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        GradingPeriod gradingPeriod = gradingPeriodRepository.findById(gradingPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("No grading period found with id " + gradingPeriodId));

        Optional<SemesterExamGrade> existing = semesterExamGradeRepository
                .findByStudentAndGradingPeriodAndTeachingAssignment(student, gradingPeriod, teachingAssignment);
        if (existing.isPresent()) {
            if (!isWithinEditWindow(existing.get().getCreatedAt())) {
                throw new IllegalStateException(
                        "The 24-hour edit window has passed for this exam grade; submit a modification request instead"
                );
                }
            existing.get().setExamGrade(examGrade);
            semesterExamGradeRepository.save(existing.get());
            return existing.get();
        }
        SemesterExamGrade semesterExamGrade = new SemesterExamGrade(student, teachingAssignment, gradingPeriod, examGrade);
        semesterExamGradeRepository.save(semesterExamGrade);
        return semesterExamGrade;
    }

    private double calculatePeriodCeg(TeachingAssignment teachingAssignment, GradingPeriod gradingPeriod, Student student) {
        List<LessonRecord> records = lessonRecordRepository
                .findByStudentAndTopic_TeachingAssignmentAndTopic_DateBetweenAndGradeIsNotNull(
                        student, teachingAssignment, gradingPeriod.getStartDate(), gradingPeriod.getEndDate());
        if (records.isEmpty()) {
            return 0.0;
        }
        return records.stream()
                .mapToInt(LessonRecord::getGrade)
                .average()
                .orElseThrow();
    }

    public double calculateCceg(Long studentId, Long teachingAssignmentId) {
        Student student = studentService.getStudent(studentId);
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        AcademicYear academicYear = teachingAssignment.getSchoolClass().getAcademicYear();
        List<GradingPeriod> periods = gradingPeriodRepository.findByAcademicYear(academicYear);
        return periods.stream()
                .mapToDouble(period -> calculatePeriodCeg(teachingAssignment, period, student))
                .average()
                .orElseThrow();
    }

    public double calculateCfe(Long studentId, Long teachingAssignmentId) {
        Student student = studentService.getStudent(studentId);
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        List<SemesterExamGrade> examGrades = semesterExamGradeRepository
                .findByStudentAndTeachingAssignment(student, teachingAssignment);
        if (examGrades.isEmpty()) {
            return 0.0;
        }
        return examGrades.stream()
                .mapToInt(SemesterExamGrade::getExamGrade)
                .average()
                .orElseThrow();
    }

    @Transactional
    public FinalSubjectGrade submitFinalGrade(Long finalSubjectGradeId) {
        FinalSubjectGrade finalSubjectGrade = getFinalGradeById(finalSubjectGradeId);
        Student student = finalSubjectGrade.getStudent();
        TeachingAssignment teachingAssignment = finalSubjectGrade.getTeachingAssignment();

        double rawCceg = calculateCceg(student.getId(), teachingAssignment.getId());
        double rawCfe = calculateCfe(student.getId(), teachingAssignment.getId());

        Integer cceg = (int) Math.round(rawCceg);
        Integer cfe = (int) Math.round(rawCfe);

        finalSubjectGrade.submit(cceg, cfe);
        finalSubjectGradeRepository.save(finalSubjectGrade);
        return finalSubjectGrade;
    }

    public FinalSubjectGrade approveFinalGrade(Long finalSubjectGradeId, Long adminUserId) {
        FinalSubjectGrade finalSubjectGrade = getFinalGradeById(finalSubjectGradeId);
        User admin = userService.findById(adminUserId);
        finalSubjectGrade.approve(admin);
        finalSubjectGradeRepository.save(finalSubjectGrade);
        return finalSubjectGrade;
    }

    public FinalSubjectGrade rejectFinalGrade(Long finalSubjectGradeId, Long adminUserId) {
        FinalSubjectGrade finalSubjectGrade = getFinalGradeById(finalSubjectGradeId);
        User admin = userService.findById(adminUserId);
        finalSubjectGrade.reject(admin);
        finalSubjectGradeRepository.save(finalSubjectGrade);
        return finalSubjectGrade;
    }

    public List<ClassGradeRosterEntry> getClassRoster(Long teachingAssignmentId) {
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        SchoolClass schoolClass = teachingAssignment.getSchoolClass();
        AcademicYear academicYear = schoolClass.getAcademicYear();
        List<GradingPeriod> periods = gradingPeriodRepository.findByAcademicYear(academicYear);
        List<Student> students = studentService.getBySchoolClass(schoolClass);

        List<ClassGradeRosterEntry> roster = new ArrayList<>();
        for (Student student : students) {
            List<PeriodGradeSummary> periodSummaries = new ArrayList<>();
            for (GradingPeriod period : periods) {
                double ceg = calculatePeriodCeg(teachingAssignment, period, student);
                Integer examGrade = semesterExamGradeRepository
                        .findByStudentAndGradingPeriodAndTeachingAssignment(student, period, teachingAssignment)
                        .map(SemesterExamGrade::getExamGrade)
                        .orElse(null);
                periodSummaries.add(new PeriodGradeSummary(period.getSequenceNumber(), ceg, examGrade));
            }

            Optional<FinalSubjectGrade> finalGrade = finalSubjectGradeRepository
                    .findByStudentAndTeachingAssignment(student, teachingAssignment);

            roster.add(new ClassGradeRosterEntry(
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    periodSummaries,
                    finalGrade.map(FinalSubjectGrade::getCceg).orElse(null),
                    finalGrade.map(FinalSubjectGrade::getCfe).orElse(null),
                    finalGrade.map(FinalSubjectGrade::getProjectGrade).orElse(null),
                    finalGrade.map(FinalSubjectGrade::getFinalGrade).orElse(null),
                    finalGrade.map(fg -> fg.getStatus().name()).orElse("NOT_STARTED")
            ));
        }
        return roster;
    }
}