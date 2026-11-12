package com.coderarmy.hibernate;

import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.service.StudentService;
import com.coderarmy.hibernate.support.PracticeTest;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Q2 · Persistence context (StudentService.java)")
class Q2InternalsTest extends PracticeTest {

    @Autowired
    StudentService service;

    private Long seed() {
        return inTx(() -> { Student s = new Student("Rahul", "rahul@gmail.com", 24); em.persist(s); return s.getId(); });
    }

    private String nameInDb(Long id) {
        return inTx(() -> em.find(Student.class, id).getName());
    }

    private long countStudents() {
        return inTx(() -> em.createQuery("select count(s) from Student s", Long.class).getSingleResult());
    }

    private List<Student> students(int n) {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(new Student("S" + i, "s" + i + "@gmail.com", 20));
        return list;
    }

    private int managedNow() {
        return em.unwrap(Session.class).getStatistics().getEntityCount();
    }

    @Test
    @DisplayName("Q2a · rename() reaches the database")
    void renameSticks() {
        Long id = seed();
        service.rename(id, "Rohan");
        assertThat(nameInDb(id))
                .as("The new name never reached the database. Was the student still managed when you changed it? (@Transactional)")
                .isEqualTo("Rohan");
    }

    @Test
    @DisplayName("Q2a · rename() sends exactly 1 SELECT + 1 UPDATE")
    void renameStatementCount() {
        Long id = seed();
        stats().clear();
        service.rename(id, "Rohan");
        assertThat(stats().getPrepareStatementCount())
                .as("Expected SELECT + UPDATE. An extra SELECT usually means the entity was detached and merged/saved. "
                        + "Use @Transactional and let dirty checking do the UPDATE.")
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Q2a · rename() of an unknown id throws IllegalArgumentException")
    void renameUnknown() {
        assertThatThrownBy(() -> service.rename(999L, "Ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found: 999");
    }

    @Test
    @DisplayName("Q2b · renameDetached() saves the new name")
    void renameDetachedSticks() {
        Long id = seed();
        Student detached = inTx(() -> em.find(Student.class, id));   // that transaction is over: detached now
        service.renameDetached(detached, "Rohan");
        assertThat(nameInDb(id))
                .as("The change was lost. If you changed `detached` AFTER merge(), you changed an object nobody tracks.")
                .isEqualTo("Rohan");
    }

    @Test
    @DisplayName("Q2b · renameDetached() returns the managed copy, not the argument")
    void renameDetachedReturnsManaged() {
        Long id = seed();
        Student detached = inTx(() -> em.find(Student.class, id));
        Student result = service.renameDetached(detached, "Rohan");
        assertThat(result).as("Return the instance merge() gave you").isNotSameAs(detached).isNotNull();
        assertThat(result.getName()).isEqualTo("Rohan");
    }

    @Test
    @DisplayName("Q2c · importStudents() runs in its own transaction")
    void importNeedsTransaction() {
        service.importStudents(students(3));
        assertThat(countStudents()).isEqualTo(3);
    }

    @Test
    @DisplayName("Q2c · 250 students are all saved")
    void importSavesAll() {
        service.importStudents(students(250));
        assertThat(countStudents()).as("Every student must be stored. Did clear() run before flush()?").isEqualTo(250);
    }

    @Test
    @DisplayName("Q2c · after exactly 100 students the context is empty")
    void importClearsEvery100() {
        int managed = inTx(() -> { service.importStudents(students(100)); return managedNow(); });
        assertThat(managed).as("After the 100th student: flush() then clear(). %d entities were still managed.", managed).isZero();
    }

    @Test
    @DisplayName("Q2c · a single student is not cleared away early (no off-by-one)")
    void importNoOffByOne() {
        int managed = inTx(() -> { service.importStudents(students(1)); return managedNow(); });
        assertThat(managed).as("Context was cleared after the first student: use (i + 1) % 100 == 0, not i % 100 == 0").isEqualTo(1);
    }
}
