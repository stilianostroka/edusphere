package com.edusphere.service;

import com.edusphere.entity.Role;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.TimetableSlot;
import com.edusphere.entity.User;
import com.edusphere.repository.TimetableSlotRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class TimetableService {

    private final TimetableSlotRepository timetableSlotRepository;

    public TimetableService(TimetableSlotRepository timetableSlotRepository) {
        this.timetableSlotRepository = timetableSlotRepository;
    }

    public TimetableSlot createTimeSlot(User user, TeachingAssignment teachingAssignment, DayOfWeek dayOfWeek,
                                        LocalTime startTime, LocalTime endTime){

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

    public TimetableSlot rescheduleSlot(User user, TimetableSlot slot, DayOfWeek newDayOfWeek,
                                        LocalTime newStartTime, LocalTime newEndTime) {
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
}


