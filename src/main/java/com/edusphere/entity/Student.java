package com.edusphere.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender",nullable = false)
    private String gender;

    @Column(name = "personal_id",nullable = false)
    private String personalId;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status;

    @Column
    private String address;

    @ManyToMany
    @JoinTable(name = "student_parents",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private Set<Parent> parents = new HashSet<>();

    public Student() {
    }

    public Student(String firstName, String lastName, LocalDate dateOfBirth, String personalId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.personalId = personalId;
        this.enrollmentDate = enrollmentDate;
        this.status = StudentStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public Set<Parent> getParents() {
        return parents;
    }

    public void addParent(Parent parent) {
        parents.add(parent);
    }

    public void removeParent(Parent parent){
        parents.remove(parent);
    }
}


