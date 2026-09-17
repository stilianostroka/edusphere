package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.LessonRecordRepository;
import com.edusphere.repository.ModificationRequestRepository;
import com.edusphere.repository.SemesterExamGradeRepository;
import com.edusphere.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModificationRequestService implements EditWindow {

    private final ModificationRequestRepository modificationRequestRepository;
    private final TopicRepository topicRepository;
    private final LessonRecordRepository lessonRecordRepository;
    private final TeacherService teacherService;
    private final TopicService topicService;
    private final LessonRecordService lessonRecordService;
    private final UserService userService;
    private final SemesterExamGradeRepository semesterExamGradeRepository;

    public ModificationRequestService(ModificationRequestRepository modificationRequestRepository,
                                      TopicRepository topicRepository,
                                      LessonRecordRepository lessonRecordRepository, TeacherService teacherService,
                                      TopicService topicService, LessonRecordService lessonRecordService,
                                      UserService userService, SemesterExamGradeRepository semesterExamGradeRepository) {
        this.modificationRequestRepository = modificationRequestRepository;
        this.topicRepository = topicRepository;
        this.lessonRecordRepository = lessonRecordRepository;
        this.teacherService = teacherService;
        this.topicService = topicService;
        this.lessonRecordService = lessonRecordService;
        this.userService = userService;
        this.semesterExamGradeRepository = semesterExamGradeRepository;
    }

    public ModificationRequest requestTopicChange(Long requestedById, Long topicId,
                                                  String topicName, String newDescription,
                                                  String explanation ){
        Teacher requestedBy = teacherService.getById(requestedById);
        Topic topic = topicService.getById(topicId);

        if(isWithinEditWindow(topic.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeTopicChange(requestedBy, topic,
                topicName, newDescription, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public ModificationRequest requestGradeChange(Long requestedById, Long lessonRecordId,
                                                  Integer proposedGrade, String explanation){
        Teacher requestedBy = teacherService.getById(requestedById);
        LessonRecord lessonRecord = lessonRecordService.getById(lessonRecordId);
        if(isWithinEditWindow(lessonRecord.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeGradeChange(requestedBy, lessonRecord,
                proposedGrade, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public ModificationRequest requestAbsenceChange(Long requestedById, Long lessonRecordId,
                                                  String explanation){
        Teacher requestedBy = teacherService.getById(requestedById);
        LessonRecord lessonRecord = lessonRecordService.getById(lessonRecordId);
        if(isWithinEditWindow(lessonRecord.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeAbsenceChange(requestedBy,
                lessonRecord, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public ModificationRequest requestExamGradeChange(Long teacherId, Long semesterExamGradeId,
                                                      Integer proposedExamGrade, String explanation){
        Teacher requestedBy = teacherService.getById(teacherId);
        SemesterExamGrade semesterExamGrade = semesterExamGradeRepository.findById(semesterExamGradeId)
                .orElseThrow(() -> new IllegalArgumentException("No exam grade found with id " + semesterExamGradeId));

        if(isWithinEditWindow(semesterExamGrade.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeExamGradeChange(requestedBy,
                semesterExamGrade, proposedExamGrade, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public ModificationRequest approveRequest(Long modificationRequestId, Long adminUserId){
        ModificationRequest modificationRequest = getById(modificationRequestId);
        User admin = userService.findById(adminUserId);

        modificationRequest.approve(admin);
        modificationRequestRepository.save(modificationRequest);

        if (modificationRequest.isForTopic()) {
            Topic topic = modificationRequest.getTopic();
            if (modificationRequest.getProposedTopicName() != null) {
                topic.setName(modificationRequest.getProposedTopicName());
            }
            if (modificationRequest.getProposedDescription() != null) {
                topic.setDescription(modificationRequest.getProposedDescription());
            }
            topicRepository.save(topic);
        } else if (modificationRequest.isForLessonRecord()) {
            LessonRecord lessonRecord = modificationRequest.getLessonRecord();
            if (modificationRequest.getProposedGrade() != null) {
                lessonRecord.updateGrade(modificationRequest.getProposedGrade());
            } else if (modificationRequest.isProposedAbsent()) {
                lessonRecord.markAbsent();
            }
            lessonRecordRepository.save(lessonRecord);
        } else if (modificationRequest.isForExamGrade()) {
            SemesterExamGrade semesterExamGrade = modificationRequest.getSemesterExamGrade();
            semesterExamGrade.setExamGrade(modificationRequest.getProposedExamGrade());
            semesterExamGradeRepository.save(semesterExamGrade);
        }

        return modificationRequest;
    }

    public ModificationRequest rejectRequest(Long modificationRequestId, Long adminUserId){
        ModificationRequest modificationRequest = getById(modificationRequestId);
        User admin = userService.findById(adminUserId);

        modificationRequest.reject(admin);
        modificationRequestRepository.save(modificationRequest);

        return modificationRequest;
    }

    public ModificationRequest getById(Long id){
        return modificationRequestRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("No modification request found with this id."));
    }

    public List<ModificationRequest> getPending() {
        return modificationRequestRepository.findAllByStatus(ModificationRequestStatus.PENDING);
    }

    public List<ModificationRequest> getByTeacher(Long teacherId) {
        Teacher teacher = teacherService.getById(teacherId);
        return modificationRequestRepository.findAllByRequestedBy(teacher);
    }
}


