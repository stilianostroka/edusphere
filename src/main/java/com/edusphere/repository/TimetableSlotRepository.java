package com.edusphere.repository;

import com.edusphere.entity.SchoolClass;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {

    List<TimetableSlot> findAllByTeachingAssignment(TeachingAssignment teachingAssignment);

    boolean existsByDayOfWeekAndTeachingAssignmentAndStartTime(DayOfWeek dayOfWeek,
                                                               TeachingAssignment teachingAssignment,
                                                               LocalTime startTime);

    @Query("SELECT s FROM TimetableSlot s WHERE s.teachingAssignment.teacher = :teacher " +
            "AND s.dayOfWeek = :dayOfWeek AND s.startTime < :endTime AND s.endTime > :startTime")
    List<TimetableSlot> findOverlappingSlotsForTeacher(@Param("teacher") Teacher teacher,
                                                       @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                       @Param("startTime") LocalTime startTime,
                                                       @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM TimetableSlot s WHERE s.teachingAssignment.schoolClass = :schoolClass " +
            "AND s.dayOfWeek = :dayOfWeek AND s.startTime < :endTime AND s.endTime > :startTime")
    List<TimetableSlot> findOverlappingSlotsForClass(@Param("schoolClass") SchoolClass schoolClass,
                                                     @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                     @Param("startTime") LocalTime startTime,
                                                     @Param("endTime") LocalTime endTime);
}
