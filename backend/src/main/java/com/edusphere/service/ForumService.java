package com.edusphere.service;

import com.edusphere.entity.ForumPost;
import com.edusphere.entity.Teacher;
import com.edusphere.repository.ForumPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {

    private final ForumPostRepository forumPostRepository;
    private final TeacherService teacherService;

    public ForumService(ForumPostRepository forumPostRepository, TeacherService teacherService) {
        this.forumPostRepository = forumPostRepository;
        this.teacherService = teacherService;
    }

    public ForumPost createPost(Long teacherId, String content) {
        Teacher author = teacherService.getById(teacherId);
        ForumPost post = new ForumPost(author, content);
        forumPostRepository.save(post);
        return post;
    }

    public List<ForumPost> getAllPosts() {
        return forumPostRepository.findAllByOrderByCreatedAtDesc();
    }

    public void deletePost(Long postId, Long teacherId) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("No forum post found with id " + postId));
        if (!post.getAuthor().getId().equals(teacherId)) {
            throw new IllegalArgumentException("Only the author can delete their own post");
        }
        forumPostRepository.delete(post);
    }
}