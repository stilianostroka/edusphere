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

    public Subject(int totalHours, String subjectName, String code) {
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
}


