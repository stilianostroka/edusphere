package com.edusphere.controller;

import com.edusphere.dto.ForumPostRequest;
import com.edusphere.dto.ForumPostResponse;
import com.edusphere.entity.ForumPost;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.ForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService forumService;
    private final CurrentUserProvider currentUserProvider;

    public ForumController(ForumService forumService, CurrentUserProvider currentUserProvider) {
        this.forumService = forumService;
        this.currentUserProvider = currentUserProvider;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<ForumPostResponse> createPost(@RequestBody ForumPostRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ForumPost post = forumService.createPost(teacherId, request.content());
        return ResponseEntity.ok(toResponse(post));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping
    public ResponseEntity<List<ForumPostResponse>> getAllPosts() {
        return ResponseEntity.ok(forumService.getAllPosts().stream().map(this::toResponse).toList());
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        forumService.deletePost(id, teacherId);
        return ResponseEntity.noContent().build();
    }

    private ForumPostResponse toResponse(ForumPost post) {
        return new ForumPostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName(),
                post.getContent(),
                post.getCreatedAt()
        );
    }
}