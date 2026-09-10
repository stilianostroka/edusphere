package com.edusphere.entity;

import jakarta.persistence.*;

@Entity
@Table(name="subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 5)
    private String code;

    @Column(nullable = false, name = "subject_name")
    private String subjectName;

    @Column(nullable = false, name = "total_hours")
    private int totalHours;

    @Column(name = "program_year", nullable = false)
    private Integer programYear;

    public Subject(int totalHours, String subjectName, String code, Integer programYear) {
        this.programYear = programYear;
        this.totalHours = totalHours;
        this.subjectName = subjectName;
        this.code = code;
    }
    public Subject() {

    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public Integer getProgramYear() {
        return programYear;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public void setProgramYear(Integer programYear){
        this.programYear = programYear;
    }
}


