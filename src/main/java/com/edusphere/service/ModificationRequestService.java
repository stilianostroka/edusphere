package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.LessonRecordRepository;
import com.edusphere.repository.ModificationRequestRepository;
import com.edusphere.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
public class ModificationRequestService implements EditWindow {

    private final ModificationRequestRepository modificationRequestRepository;
    private final TopicRepository topicRepository;
    private final LessonRecordRepository lessonRecordRepository;
    public ModificationRequestService(ModificationRequestRepository modificationRequestRepository,
                                      TopicRepository topicRepository,
                                      LessonRecordRepository lessonRecordRepository) {
        this.modificationRequestRepository = modificationRequestRepository;
        this.topicRepository = topicRepository;
        this.lessonRecordRepository = lessonRecordRepository;
    }

    public ModificationRequest requestTopicChange(Teacher requestedBy, Topic topic,
                                                  String topicName, String newDescription,
                                                  String explanation ){
        if(isWithinEditWindow(topic.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeTopicChange(requestedBy, topic,
                topicName, newDescription, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public ModificationRequest requestGradeChange(Teacher requestedBy, LessonRecord lessonRecord,
                                                  Integer proposedGrade, String explanation){
        if(isWithinEditWindow(lessonRecord.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeGradeChange(requestedBy, lessonRecord,
                proposedGrade, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public ModificationRequest requestAbsenceChange(Teacher requestedBy, LessonRecord lessonRecord,
                                                  String explanation){
        if(isWithinEditWindow(lessonRecord.getCreatedAt()))
            throw new IllegalStateException("You can edit it directly as 24-hour window is active yet.");

        ModificationRequest request = ModificationRequest.proposeAbsenceChange(requestedBy,
                lessonRecord, explanation);

        modificationRequestRepository.save(request);

        return request;
    }

    public void approveRequest(ModificationRequest modificationRequest, User user){
        modificationRequest.approve(user);
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
        }
    }

    public void rejectRequest(ModificationRequest modificationRequest, User user){
        modificationRequest.reject(user);
        modificationRequestRepository.save(modificationRequest);
    }
}


