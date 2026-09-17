package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.ParentMeetingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParentMeetingService {

    private final ParentMeetingRepository parentMeetingRepository;
    private final SchoolClassService schoolClassService;
    private final TeacherService teacherService;

    public ParentMeetingService(ParentMeetingRepository parentMeetingRepository, SchoolClassService schoolClassService, TeacherService teacherService) {
        this.parentMeetingRepository = parentMeetingRepository;
        this.schoolClassService = schoolClassService;
        this.teacherService = teacherService;
    }

    public ParentMeeting recordMeeting(Long teacherId, Long schoolClassId,
                                       LocalDate meetingTime, String topic){
        SchoolClass schoolClass = schoolClassService.getById(schoolClassId);
        Teacher teacher = teacherService.getById(teacherId);
        ParentMeeting parentMeeting = new ParentMeeting(schoolClass,teacher,meetingTime,topic);
        parentMeetingRepository.save(parentMeeting);

        return parentMeeting;
    }

    public ParentMeeting editMeeting(Long parentMeetingId, Long teacherId, LocalDate date, String topic) {
        ParentMeeting parentMeeting = parentMeetingRepository.findById(parentMeetingId)
                .orElseThrow(() -> new IllegalArgumentException("No parent meeting found with that id."));
        Teacher teacher = teacherService.getById(teacherId);
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

    public void deleteMeeting(Long parentMeetingId,Long teacherId){
        ParentMeeting parentMeeting = parentMeetingRepository.findById(parentMeetingId)
                .orElseThrow(() -> new IllegalArgumentException("No parent meeting found with that id."));
        Teacher teacher = teacherService.getById(teacherId);
        if (!parentMeeting.getSchoolClass().getSupervisorTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Only the class's supervising teacher can delete this meeting");
        }
        parentMeetingRepository.delete(parentMeeting);
    }

    public List<ParentMeeting> getBySchoolClass(Long schoolClassId){
        SchoolClass schoolClass = schoolClassService.getById(schoolClassId);
        return parentMeetingRepository.findAllBySchoolClass(schoolClass);
    }
}


