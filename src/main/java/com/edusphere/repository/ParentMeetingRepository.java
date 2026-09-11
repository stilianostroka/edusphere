package com.edusphere.repository;

import com.edusphere.entity.ParentMeeting;
import com.edusphere.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentMeetingRepository extends JpaRepository<ParentMeeting, Long> {
    List<ParentMeeting> findAllBySchoolClass(SchoolClass schoolClass);
    boolean existsByTopicsDiscussed(String topicsDiscussed);
}
