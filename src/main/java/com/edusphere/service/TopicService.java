package com.edusphere.service;

import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.Topic;
import com.edusphere.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private static final int EDIT_WINDOW_HOURS = 24;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    private boolean isWithinEditWindow(LocalDateTime createdAt) {
        return Duration.between(createdAt, LocalDateTime.now()).toHours() < EDIT_WINDOW_HOURS;
    }

    public Topic createTopic(TeachingAssignment teachingAssignment, String topicName,
                             String description, LocalDate topicDate){

        Topic topic = new Topic(teachingAssignment,topicDate,topicName);
        topic.setDescription(description);
        topicRepository.save(topic);

        return topic;
    }

    public void updateTopic(Topic topic, String newTopicName, String newDescription,
                             LocalDate newTopicDate){
        if(!isWithinEditWindow(topic.getCreatedAt()))
            throw new IllegalStateException("The 24-hour edit window has passed for this topic;" +
                    " submit a modification request instead");

        topic.setDate(newTopicDate);
        topic.setName(newTopicName);
        topic.setDescription(newDescription);
        topicRepository.save(topic);
    }

    public void deleteTopic(Topic topic){
        if(!isWithinEditWindow(topic.getCreatedAt()))
            throw new IllegalStateException("The 24-hour edit window has passed for this topic; " +
                            "submit a modification request instead");

        topicRepository.delete(topic);
    }
}


