package com.coderarmy.hibernate;

import com.coderarmy.hibernate.entity.Course;
import com.coderarmy.hibernate.entity.Department;
import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.support.PracticeTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Q3 · Relationships (Student, Department, Course)")
class Q3RelationshipsTest extends PracticeTest {

    private Long departmentIdInDb(Long studentId) {
        return jdbc.queryForObject("SELECT department_id FROM students WHERE id = ?", Long.class, studentId);
    }

    @Test
    @DisplayName("Q3a · students.department_id stores the reference")
    void manyToOneColumn() {
        assertThat(columns("students")).as("No column department_id. Map Student.department with @ManyToOne + @JoinColumn(name = \"department_id\")").contains("department_id");
        Long[] ids = inTx(() -> {
            Department eng = new Department("Engineering");
            em.persist(eng);
            Student s = new Student("Rahul", "rahul@gmail.com", 24);
            s.setDepartment(eng);
            em.persist(s);
            return new Long[]{s.getId(), eng.getId()};
        });
        assertThat(departmentIdInDb(ids[0])).isEqualTo(ids[1]);
    }

    @Test
    @DisplayName("Q3b · addStudent() / removeStudent() update both sides")
    void helpersSyncBothSides() {
        Department eng = new Department("Engineering");
        Student s = new Student("Rahul", "rahul@gmail.com", 24);
        eng.addStudent(s);
        assertThat(eng.getStudents()).contains(s);
        assertThat(s.getDepartment()).as("addStudent must set the owning side: student.setDepartment(this)").isSameAs(eng);
        eng.removeStudent(s);
        assertThat(eng.getStudents()).doesNotContain(s);
        assertThat(s.getDepartment()).as("removeStudent must clear the owning side").isNull();
    }

    @Test
    @DisplayName("Q3b · saving through addStudent() writes department_id")
    void helperWritesForeignKey() {
        Long[] ids = inTx(() -> {
            Department eng = new Department("Engineering");
            Student s = new Student("Rahul", "rahul@gmail.com", 24);
            em.persist(eng);
            em.persist(s);
            eng.addStudent(s);   // dirty checking on the owning side → UPDATE students SET department_id
            return new Long[]{s.getId(), eng.getId()};
        });
        assertThat(departmentIdInDb(ids[0])).isEqualTo(ids[1]);
    }

    @Test
    @DisplayName("Q3b · Department.students loads from department_id (mappedBy, no join table)")
    void inverseSideUsesForeignKey() {
        assertThat(columns("students")).as("Do Q3a first").contains("department_id");
        assertThat(tables()).as("A join table appeared. The inverse side needs mappedBy = \"department\".")
                .noneMatch(t -> t.contains("department") && t.contains("student"));
        Long deptId = inTx(() -> {
            Department eng = new Department("Engineering");
            em.persist(eng);
            for (String n : new String[]{"Rahul", "Priya"}) {
                Student s = new Student(n, n.toLowerCase() + "@gmail.com", 22);
                s.setDepartment(eng);
                em.persist(s);
            }
            return eng.getId();
        });
        int size = inTx(() -> em.find(Department.class, deptId).getStudents().size());
        assertThat(size).as("department.getStudents() should see both students").isEqualTo(2);
    }

    @Test
    @DisplayName("Q3c · join table student_course(student_id, course_id)")
    void joinTable() {
        assertThat(tables()).as("No table student_course. Use @ManyToMany + @JoinTable(name = \"student_course\", ...) on Student.courses").contains("student_course");
        assertThat(columns("student_course")).containsExactlyInAnyOrder("student_id", "course_id");
        assertThat(tables()).as("A second join table exists. Course.students needs mappedBy = \"courses\".")
                .noneMatch(t -> t.startsWith("course") && t.contains("student"));
    }

    @Test
    @DisplayName("Q3c · addCourse() updates both sides")
    void addCourseSync() {
        Student s = new Student("Rahul", "rahul@gmail.com", 24);
        Course java = new Course("Java");
        s.addCourse(java);
        assertThat(s.getCourses()).contains(java);
        assertThat(java.getStudents()).as("addCourse must also do course.getStudents().add(this)").contains(s);
    }

    @Test
    @DisplayName("Q3c · enrolment is saved and visible from the course side")
    void enrolmentRoundTrip() {
        Long courseId = inTx(() -> {
            Course java = new Course("Java");
            em.persist(java);
            Student s = new Student("Rahul", "rahul@gmail.com", 24);
            s.addCourse(java);
            em.persist(s);
            return java.getId();
        });
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM student_course", Integer.class)).isEqualTo(1);
        int size = inTx(() -> em.find(Course.class, courseId).getStudents().size());
        assertThat(size).isEqualTo(1);
    }

    @Test
    @DisplayName("Q3c · the same enrolment can't be stored twice")
    void pairIsUnique() {
        Long[] ids = inTx(() -> {
            Course java = new Course("Java");
            em.persist(java);
            Student s = new Student("Rahul", "rahul@gmail.com", 24);
            s.addCourse(java);
            em.persist(s);
            return new Long[]{s.getId(), java.getId()};
        });
        assertThatThrownBy(() -> jdbc.update("INSERT INTO student_course(student_id, course_id) VALUES (?, ?)", ids[0], ids[1]))
                .as("Duplicate pair accepted. Add uniqueConstraints = @UniqueConstraint(columnNames = {\"student_id\", \"course_id\"})")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
