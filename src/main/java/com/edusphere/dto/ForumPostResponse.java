package com.edusphere.dto;

import java.time.LocalDateTime;

public record ForumPostResponse(
        Long id,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {
}
