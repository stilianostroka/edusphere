package com.edusphere.service;

import com.edusphere.entity.*;
import com.edusphere.repository.TimetableSlotRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimetableService {

    private final TimetableSlotRepository timetableSlotRepository;
    private final UserRepository userRepository;
    private final TeachingAssignmentService teachingAssignmentService;
    private final TeacherService teacherService;

    public TimetableService(TimetableSlotRepository timetableSlotRepository, UserRepository userRepository, TeachingAssignmentService teachingAssignmentService, TeacherService teacherService) {
        this.timetableSlotRepository = timetableSlotRepository;
        this.userRepository = userRepository;
        this.teachingAssignmentService = teachingAssignmentService;
        this.teacherService = teacherService;
    }

    public TimetableSlot createTimeSlot(Long userId, Long teachingAssignmentId, DayOfWeek dayOfWeek,
                                        LocalTime startTime, LocalTime endTime){
        User user  = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("No user found with this id."));
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Only an Administrator can create a timetable slot");
        }
        List<TimetableSlot> list = timetableSlotRepository.findOverlappingSlotsForTeacher(teachingAssignment.getTeacher(),dayOfWeek,
                startTime, endTime);

        if(!list.isEmpty())
            throw new IllegalStateException("Teacher is booked that time.");

        List<TimetableSlot> list2 = timetableSlotRepository.
                findOverlappingSlotsForClass(teachingAssignment.getSchoolClass(),
                dayOfWeek, startTime, endTime);

        if(!list2.isEmpty())
            throw new IllegalStateException("Class has lesson that time.");

        TimetableSlot slot = new TimetableSlot(teachingAssignment, dayOfWeek, startTime, endTime);
        timetableSlotRepository.save(slot);
        return slot;
    }

    public TimetableSlot rescheduleSlot(Long userId, Long slotId, DayOfWeek newDayOfWeek,
                                        LocalTime newStartTime, LocalTime newEndTime) {
        User user  = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("No user found with this id."));

        TimetableSlot slot = timetableSlotRepository.getById(slotId);
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Only an Administrator can reschedule a timetable slot");
        }

        TeachingAssignment teachingAssignment = slot.getTeachingAssignment();

        List<TimetableSlot> teacherConflicts = timetableSlotRepository.findOverlappingSlotsForTeacher(
                teachingAssignment.getTeacher(), newDayOfWeek, newStartTime, newEndTime);
        boolean teacherConflict = teacherConflicts.stream()
                .anyMatch(existing -> !existing.getId().equals(slot.getId()));
        if (teacherConflict) {
            throw new IllegalStateException("Teacher is booked that time.");
        }

        List<TimetableSlot> classConflicts = timetableSlotRepository.findOverlappingSlotsForClass(
                teachingAssignment.getSchoolClass(), newDayOfWeek, newStartTime, newEndTime);
        boolean classConflict = classConflicts.stream()
                .anyMatch(existing -> !existing.getId().equals(slot.getId()));
        if (classConflict) {
            throw new IllegalStateException("Class has lesson that time.");
        }

        slot.reschedule(newDayOfWeek, newStartTime, newEndTime);
        timetableSlotRepository.save(slot);
        return slot;
    }

    public List<TimetableSlot> getByTeachingAssignment(Long teachingAssignmentId){
        TeachingAssignment teachingAssignment = teachingAssignmentService.getById(teachingAssignmentId);
        return timetableSlotRepository.findAllByTeachingAssignment(teachingAssignment);
    }

    public List<TimetableSlot> getByTeacher(Long teacherId) {
        List<TeachingAssignment> assignments = teachingAssignmentService.getByTeacher(teacherId);
        List<TimetableSlot> slots = new ArrayList<>();
        for (TeachingAssignment assignment : assignments) {
            slots.addAll(timetableSlotRepository.findAllByTeachingAssignment(assignment));
        }
        return slots;
    }
}


