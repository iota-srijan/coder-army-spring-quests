package com.coderarmy.hibernate.service;

import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;

/** Quest 2: the persistence context, dirty checking, merge, flush and clear. */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * TODO Q2a: load the student and change its name. The change must reach the database.
     * Unknown id → IllegalArgumentException("Student not found: " + id).
     * The test counts statements: exactly 1 SELECT + 1 UPDATE.
     * Hint: a managed entity needs no save(), merge() or update(). What makes it managed?
     */
    public void rename(Long id, String newName) {
        throw new UnsupportedOperationException("TODO Q2a: rename()");
    }

    /**
     * TODO Q2b: {@code detached} came from an earlier, finished transaction.
     * Apply the new name, make it stick, and return the MANAGED instance.
     * Hint: merge() returns a different object than the one you pass in.
     */
    public Student renameDetached(Student detached, String newName) {
        throw new UnsupportedOperationException("TODO Q2b: renameDetached()");
    }

    /**
     * TODO Q2c: persist every student in one transaction.
     * Every 100 students: send the pending SQL, then empty the persistence context.
     * Watch the order of the two calls, and the off-by-one at i = 0.
     */
    public void importStudents(List<Student> students) {
        throw new UnsupportedOperationException("TODO Q2c: importStudents()");
    }
}
