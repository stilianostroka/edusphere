package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.ParentMeetingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ParentMeetingService {

    private final ParentMeetingRepository parentMeetingRepository;

    public ParentMeetingService(ParentMeetingRepository parentMeetingRepository) {
        this.parentMeetingRepository = parentMeetingRepository;
    }

    public ParentMeeting recordMeeting(Teacher teacher, SchoolClass schoolClass,
                                       LocalDate meetingTime, String topic){

        ParentMeeting parentMeeting = new ParentMeeting(schoolClass,teacher,meetingTime,topic);
        parentMeetingRepository.save(parentMeeting);

        return parentMeeting;
    }

    public ParentMeeting editMeeting(ParentMeeting parentMeeting, Teacher teacher, LocalDate date, String topic) {
        if (!parentMeeting.getSchoolClass().getSupervisorTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Only the class's supervising teacher can edit this meeting");
        }
        if(date!=null)
           parentMeeting.setMeetingDateTime(date);
        if(topic!=null)
            parentMeeting.setTopicsDiscussed(topic);

        parentMeetingRepository.save(parentMeeting);
        return parentMeeting;
    }
}


