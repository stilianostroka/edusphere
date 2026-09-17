package com.edusphere.service;

import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.Topic;
import com.edusphere.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TopicService implements EditWindow {

    private final TopicRepository topicRepository;
    private final TeachingAssignmentService teachingAssignmentService;
    private static final int EDIT_WINDOW_HOURS = 24;

    public TopicService(TopicRepository topicRepository, TeachingAssignmentService teachingAssignmentService) {
        this.topicRepository = topicRepository;
        this.teachingAssignmentService = teachingAssignmentService;
    }

    public Topic getById(Long id){
        return topicRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("No topic found with this Id."));
    }

    public List<Topic> getByTeachingAssignmentAndDateRange(Long teachingAssignmentId, LocalDate start, LocalDate end) {
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        return topicRepository.findByTeachingAssignmentAndDateBetween(teachingAssignment, start, end);
    }

    public List<Topic> getAll(Long id) {
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(id);
        return topicRepository.findByTeachingAssignment(teachingAssignment);
    }

    public Topic createTopic(Long teachingAssignmentId, String topicName,
                             String description, LocalDate topicDate){
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        Topic topic = new Topic(teachingAssignment,topicDate,topicName);
        topic.setDescription(description);
        topicRepository.save(topic);

        return topic;
    }

    public void updateTopic(Long topicId, String newTopicName, String newDescription,
                             LocalDate newTopicDate){
        Topic topic = topicRepository.findById(topicId).orElseThrow(()-> new IllegalArgumentException(" No topic found with this ID."));
        if(!isWithinEditWindow(topic.getCreatedAt()))
            throw new IllegalStateException("The 24-hour edit window has passed for this topic;" +
                    " submit a modification request instead");

        topic.setDate(newTopicDate);
        topic.setName(newTopicName);
        topic.setDescription(newDescription);
        topicRepository.save(topic);
    }

    public void deleteTopic(Long topicId){
        if(!isWithinEditWindow(topicRepository.findById(topicId).get().getCreatedAt()))
            throw new IllegalStateException("The 24-hour edit window has passed for this topic; " +
                            "submit a modification request instead");

        topicRepository.delete(topicRepository.findById(topicId).orElseThrow(() -> new IllegalArgumentException("No topic found with that ID.")));
    }
}


