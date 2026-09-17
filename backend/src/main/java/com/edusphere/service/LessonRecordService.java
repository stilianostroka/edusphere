package com.edusphere.service;

import com.edusphere.dto.ClassAbsenceEntry;
import com.edusphere.entity.*;
import com.edusphere.repository.LessonRecordRepository;
import org.springframework.stereotype.Service;
import com.edusphere.entity.StudentStatus;

import java.util.List;

@Service
public class LessonRecordService implements EditWindow {

    private final LessonRecordRepository lessonRecordRepository;
    private final TopicService topicService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final SchoolClassService schoolClassService;

    public LessonRecordService(LessonRecordRepository lessonRecordRepository, TopicService topicService,
                               StudentService studentService, TeacherService teacherService,
                               SchoolClassService schoolClassService) {
        this.lessonRecordRepository = lessonRecordRepository;
        this.topicService = topicService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.schoolClassService = schoolClassService;
    }

    public LessonRecord getById(Long id) {
        return lessonRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No lesson record found with id " + id));
    }

    public List<LessonRecord> getByTopic(Long topicId) {
        Topic topic = topicService.getById(topicId);
        return lessonRecordRepository.findByTopic(topic);
    }

    public List<LessonRecord> getByStudentAbsences(Long studentId) {
        Student student = studentService.getStudent(studentId);
        return lessonRecordRepository.findByStudentAndAbsentTrue(student);
    }

    public List<LessonRecord> getByStudentGrades(Long studentId) {
        Student student = studentService.getStudent(studentId);
        return lessonRecordRepository.findByStudentAndGradeIsNotNull(student);
    }

    public LessonRecord gradeStudent(Long topicId, Long studentId, Integer grade) {
        Topic topic = topicService.getById(topicId);
        Student student = studentService.getStudent(studentId);
        requireActiveStudent(student);
        if (lessonRecordRepository.findByStudentAndTopic(student, topic).isPresent()) {
            throw new IllegalStateException(
                    "A record already exists for this student and topic. Use updateGrade or markAbsent instead."
            );
        }
        LessonRecord lessonRecord = LessonRecord.forGrade(student, topic, grade);
        lessonRecordRepository.save(lessonRecord);
        return lessonRecord;
    }

    public LessonRecord markStudentAbsent(Long topicId, Long studentId) {
        Topic topic = topicService.getById(topicId);
        Student student = studentService.getStudent(studentId);
        requireActiveStudent(student);
        if (lessonRecordRepository.findByStudentAndTopic(student, topic).isPresent()) {
            throw new IllegalStateException(
                    "A record already exists for this student and topic. Use updateGrade or markAbsent instead."
            );
        }
        LessonRecord lessonRecord = LessonRecord.forAbsence(student, topic);
        lessonRecordRepository.save(lessonRecord);
        return lessonRecord;
    }

    public void updateGrade(Long lessonRecordId, Integer newGrade) {
        LessonRecord lessonRecord = getById(lessonRecordId);
        requireActiveStudent(lessonRecord.getStudent());
        if (!isWithinEditWindow(lessonRecord.getCreatedAt())) {
            throw new IllegalStateException(
                    "The 24-hour edit window has passed for this record. Submit a modification request instead."
            );
        }
        lessonRecord.updateGrade(newGrade);
        lessonRecordRepository.save(lessonRecord);
    }

    public void markAbsent(Long lessonRecordId) {
        LessonRecord lessonRecord = getById(lessonRecordId);
        requireActiveStudent(lessonRecord.getStudent());
        if (!isWithinEditWindow(lessonRecord.getCreatedAt())) {
            throw new IllegalStateException(
                    "The 24-hour edit window has passed for this record. Submit a modification request instead."
            );
        }
        lessonRecord.markAbsent();
        lessonRecordRepository.save(lessonRecord);
    }

    public void deleteRecord(Long lessonRecordId) {
        LessonRecord lessonRecord = getById(lessonRecordId);
        if (!isWithinEditWindow(lessonRecord.getCreatedAt())) {
            throw new IllegalStateException(
                    "The 24-hour edit window has passed for this record. Submit a modification request instead."
            );
        }
        lessonRecordRepository.delete(lessonRecord);
    }

    public void justifyAbsence(Long lessonRecordId, Long supervisorTeacherId, String note) {
        LessonRecord lessonRecord = getById(lessonRecordId);
        Teacher supervisor = teacherService.getById(supervisorTeacherId);
        lessonRecord.justify(supervisor, note);
        lessonRecordRepository.save(lessonRecord);
    }

    public List<ClassAbsenceEntry> getClassAbsences(Long classId) {
        SchoolClass schoolClass = schoolClassService.getById(classId);
        return lessonRecordRepository.findByStudent_SchoolClassAndAbsentTrue(schoolClass).stream()
                .map(r -> new ClassAbsenceEntry(
                        r.getId(),
                        r.getStudent().getId(),
                        r.getStudent().getFirstName(),
                        r.getStudent().getLastName(),
                        r.getTopic().getTeachingAssignment().getSubject().getSubjectName(),
                        r.getTopic().getName(),
                        r.getTopic().getDate(),
                        r.isJustified(),
                        r.getJustificationNote(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    private void requireActiveStudent(Student student) {
        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new IllegalStateException("Only active students can be graded or marked absent.");
        }
    }
}