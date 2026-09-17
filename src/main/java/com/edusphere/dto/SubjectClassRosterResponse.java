package com.edusphere.dto;

import java.util.List;

public record SubjectClassRosterResponse(
        Long teachingAssignmentId,
        String subjectName,
        String teacherName,
        List<ClassGradeRosterEntry> roster
) {}