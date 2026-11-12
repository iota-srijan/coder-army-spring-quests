package com.coderarmy.hibernate.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * TODO Q3b: the INVERSE side of Student.department. Remove @Transient.
     * It must reuse students.department_id: no join table, no new column.
     */
    @Transient
    private List<Student> students = new ArrayList<>();

    protected Department() {
    }

    public Department(String name) {
        this.name = name;
    }

    /** TODO Q3b: update BOTH sides (the list, and the owning side that decides the FK). */
    public void addStudent(Student student) {
        throw new UnsupportedOperationException("TODO Q3b: addStudent()");
    }

    /** TODO Q3b: update BOTH sides. */
    public void removeStudent(Student student) {
        throw new UnsupportedOperationException("TODO Q3b: removeStudent()");
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Student> getStudents() { return students; }

    @Override
    public String toString() {
        return "Department{id=" + id + ", name=" + name + "}";
    }
}
