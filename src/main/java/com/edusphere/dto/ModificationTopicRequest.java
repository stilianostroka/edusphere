package com.edusphere.dto;

public record ModificationTopicRequest(
        Long teacherId,
        Long topicId,
        String proposedTopicName,
        String proposedDescription,
        String explanation
) {}


