package com.edusphere.service;

import com.edusphere.entity.LessonRecord;
import com.edusphere.entity.Student;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.Topic;
import com.edusphere.repository.LessonRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class LessonRecordService implements EditWindow {

    private final LessonRecordRepository lessonRecordRepository;

    public LessonRecordService(LessonRecordRepository lessonRecordRepository) {
        this.lessonRecordRepository = lessonRecordRepository;
    }

    public LessonRecord gradeStudent(Topic topic, Student student, Integer grade) {
        if (lessonRecordRepository.findByStudentAndTopic(student, topic).isPresent()) {
            throw new IllegalStateException(
                    "A record already exists for this student and topic; use updateGrade or markAbsent instead"
            );
        }
        LessonRecord lessonRecord = LessonRecord.forGrade(student, topic, grade);
        lessonRecordRepository.save(lessonRecord);
        return lessonRecord;
    }

    public LessonRecord markStudentAbsent(Topic topic, Student student) {
        if (lessonRecordRepository.findByStudentAndTopic(student, topic).isPresent()) {
            throw new IllegalStateException(
                    "A record already exists for this student and topic; use updateGrade or markAbsent instead"
            );
        }
        LessonRecord lessonRecord = LessonRecord.forAbsence(student, topic);
        lessonRecordRepository.save(lessonRecord);
        return lessonRecord;
    }

    public void updateGrade(LessonRecord lessonRecord, Integer newGrade) {
        if (!isWithinEditWindow(lessonRecord.getCreatedAt())) {
            throw new IllegalStateException(
                    "The 24-hour edit window has passed for this record; submit a modification request instead"
            );
        }
        lessonRecord.updateGrade(newGrade);
        lessonRecordRepository.save(lessonRecord);
    }

    public void markAbsent(LessonRecord lessonRecord) {
        if (!isWithinEditWindow(lessonRecord.getCreatedAt())) {
            throw new IllegalStateException(
                    "The 24-hour edit window has passed for this record; submit a modification request instead"
            );
        }
        lessonRecord.markAbsent();
        lessonRecordRepository.save(lessonRecord);
    }

    public void justifyAbsence(LessonRecord lessonRecord, Teacher supervisor, String note) {
        lessonRecord.justify(supervisor, note);
        lessonRecordRepository.save(lessonRecord);
    }
}