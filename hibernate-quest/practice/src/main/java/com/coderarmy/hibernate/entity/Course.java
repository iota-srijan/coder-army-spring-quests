package com.coderarmy.hibernate.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    /** TODO Q3c: the INVERSE side of Student.courses. Remove @Transient. No second join table. */
    @Transient
    private Set<Student> students = new HashSet<>();

    protected Course() {
    }

    public Course(String title) {
        this.title = title;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Set<Student> getStudents() { return students; }

    @Override
    public String toString() {
        return "Course{id=" + id + ", title=" + title + "}";
    }
}
