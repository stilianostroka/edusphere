package com.edusphere.service;

import com.edusphere.entity.LessonRecord;
import com.edusphere.entity.Student;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.Topic;
import com.edusphere.repository.LessonRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonRecordService implements EditWindow {

    private final LessonRecordRepository lessonRecordRepository;
    private final TopicService topicService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public LessonRecordService(LessonRecordRepository lessonRecordRepository, TopicService topicService,
                               StudentService studentService, TeacherService teacherService) {
        this.lessonRecordRepository = lessonRecordRepository;
        this.topicService = topicService;
        this.studentService = studentService;
        this.teacherService = teacherService;
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

    public LessonRecord gradeStudent(Long topicId, Long studentId, Integer grade) {
        Topic topic = topicService.getById(topicId);
        Student student = studentService.getStudent(studentId);
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
        if (!isWithinEditWindow(lessonRecord.getCreatedAt())) {
            throw new IllegalStateException(
                    "The 24-hour edit window has passed for this record. Submit a modification request instead."
            );
        }
        lessonRecord.markAbsent();
        lessonRecordRepository.save(lessonRecord);
    }

    public void justifyAbsence(Long lessonRecordId, Long supervisorTeacherId, String note) {
        LessonRecord lessonRecord = getById(lessonRecordId);
        Teacher supervisor = teacherService.getById(supervisorTeacherId);
        lessonRecord.justify(supervisor, note);
        lessonRecordRepository.save(lessonRecord);
    }
}