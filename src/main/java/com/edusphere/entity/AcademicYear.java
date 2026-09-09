package com.edusphere.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="academic_years")
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "academic_year")
    private String academicYear;

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, name = "active")
    private boolean active;

    public AcademicYear() {
    }
    public AcademicYear(String academicYear, LocalDate startDate, LocalDate endDate, boolean active) {
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}


