package com.coderarmy.hibernate.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** TODO Q1a: column "student_name", NOT NULL, length 100. */
    private String name;

    /** TODO Q1a: NOT NULL, unique, length 150. */
    private String email;

    private Integer age;

    private boolean active = true;

    /**
     * TODO Q1b: a teammate stored this by position (0, 1, 2).
     * Insert a constant at the top of the enum and every old row changes meaning.
     * Store the name instead.
     */
    @Enumerated(EnumType.ORDINAL)
    private StudentStatus status = StudentStatus.ACTIVE;

    /** TODO Q1c: only used for display. It must never become a column. */
    private String displayName;

    /**
     * TODO Q1d: store these strings in table student_skills (student_id, skill).
     * Remove @Transient. Hint: @ElementCollection + @CollectionTable + @Column.
     */
    @Transient
    private Set<String> skills = new HashSet<>();

    /** Finished for you: columns address_line, city, state, postal_code. */
    @Embedded
    private Address currentAddress;

    /**
     * TODO Q1e: remove @Transient and embed it, with columns
     * permanent_address_line, permanent_city, permanent_state, permanent_postal_code.
     * Embedding it WITHOUT renaming crashes startup for every test (repeated column).
     */
    @Transient
    private Address permanentAddress;

    /**
     * TODO Q3a: many students → one department, foreign-key column department_id.
     * Remove @Transient.  (Q4c later: make this association LAZY.)
     */
    @Transient
    private Department department;

    /**
     * TODO Q3c: this is the OWNING side of Student ↔ Course.
     * Join table student_course(student_id, course_id); the pair must be unique.
     * Remove @Transient. Don't cascade REMOVE: courses are shared.
     */
    @Transient
    private Set<Course> courses = new HashSet<>();

    protected Student() {
    }

    public Student(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /** TODO Q3c: add to courses AND add this student to course.getStudents(). */
    public void addCourse(Course course) {
        throw new UnsupportedOperationException("TODO Q3c: addCourse()");
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public StudentStatus getStatus() { return status; }
    public void setStatus(StudentStatus status) { this.status = status; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Set<String> getSkills() { return skills; }
    public Address getCurrentAddress() { return currentAddress; }
    public void setCurrentAddress(Address currentAddress) { this.currentAddress = currentAddress; }
    public Address getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(Address permanentAddress) { this.permanentAddress = permanentAddress; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Set<Course> getCourses() { return courses; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + "}";   // no relationships here: no recursion
    }
}
