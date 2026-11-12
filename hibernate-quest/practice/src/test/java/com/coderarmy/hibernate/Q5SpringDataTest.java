package com.coderarmy.hibernate;

import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.repository.StudentRepository;
import com.coderarmy.hibernate.service.StudentQueryService;
import com.coderarmy.hibernate.support.PracticeTest;
import com.coderarmy.hibernate.support.Repo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Q5 · Spring Data JPA (StudentRepository, StudentQueryService)")
class Q5SpringDataTest extends PracticeTest {

    @Autowired
    StudentRepository repo;

    @Autowired
    StudentQueryService queries;

    @BeforeEach
    void seed() {
        Object[][] rows = {
                {"Rahul", 24, true}, {"Priya", 22, true}, {"Aman", 27, false}, {"Neha", 21, true},
                {"Harsh", 23, true}, {"Kabir", 26, true}, {"Isha", 24, false}};
        for (Object[] r : rows) {
            Student s = new Student((String) r[0], ((String) r[0]).toLowerCase() + "@gmail.com", (Integer) r[1]);
            s.setActive((Boolean) r[2]);
            repo.save(s);
        }
    }

    private static List<String> names(List<Student> students) {
        return students.stream().map(Student::getName).toList();
    }

    @Test
    @DisplayName("Q5a · findByEmail returns an Optional")
    void findByEmail() {
        String sig = "Optional<Student> findByEmail(String email);";
        Optional<Student> found = Repo.call(repo, sig, "findByEmail", "priya@gmail.com");
        Optional<Student> missing = Repo.call(repo, sig, "findByEmail", "ghost@gmail.com");
        assertThat(found).map(Student::getName).contains("Priya");
        assertThat(missing).isEmpty();
    }

    @Test
    @DisplayName("Q5a · existsByEmail answers yes / no")
    void existsByEmail() {
        String sig = "boolean existsByEmail(String email);";
        boolean yes = Repo.call(repo, sig, "existsByEmail", "kabir@gmail.com");
        boolean no = Repo.call(repo, sig, "existsByEmail", "ghost@gmail.com");
        assertThat(yes).isTrue();
        assertThat(no).isFalse();
    }

    @Test
    @DisplayName("Q5b · findByAgeGreaterThan(23)")
    void ageGreaterThan() {
        List<Student> result = Repo.call(repo, "List<Student> findByAgeGreaterThan(Integer age);", "findByAgeGreaterThan", 23);
        assertThat(names(result)).containsExactlyInAnyOrder("Rahul", "Aman", "Kabir", "Isha");
    }

    @Test
    @DisplayName("Q5b · findTop3ByActiveTrueOrderByAgeDesc: oldest three active")
    void top3() {
        List<Student> result = Repo.call(repo, "List<Student> findTop3ByActiveTrueOrderByAgeDesc();", "findTop3ByActiveTrueOrderByAgeDesc");
        assertThat(names(result)).containsExactly("Kabir", "Rahul", "Harsh");
    }

    @Test
    @DisplayName("Q5b · findByNameContainingIgnoreCase(\"HA\")")
    void containingIgnoreCase() {
        List<Student> result = Repo.call(repo, "List<Student> findByNameContainingIgnoreCase(String part);", "findByNameContainingIgnoreCase", "HA");
        assertThat(names(result)).containsExactlyInAnyOrder("Harsh", "Isha", "Neha");
    }

    @Test
    @DisplayName("Q5c · findStudentsInAgeRange is JPQL with named parameters")
    void jpqlQuery() {
        String sig = "@Query(\"SELECT s FROM Student s WHERE ...\") List<Student> findStudentsInAgeRange(@Param(\"minimumAge\") Integer minimumAge, @Param(\"maximumAge\") Integer maximumAge);";
        Method m = Repo.method("findStudentsInAgeRange", 2, sig);
        Query q = m.getAnnotation(Query.class);
        assertThat(q).as("Annotate the method with @Query").isNotNull();
        assertThat(q.nativeQuery()).as("Write JPQL, not native SQL").isFalse();
        assertThat(q.value()).as("JPQL uses the entity name Student, not the table students").contains("Student").doesNotContain("students")
                .as("Use named parameters").contains(":minimumAge", ":maximumAge");
        List<Student> result = Repo.call(repo, sig, "findStudentsInAgeRange", 22, 24);
        assertThat(names(result)).containsExactlyInAnyOrder("Rahul", "Priya", "Harsh", "Isha");
    }

    @Test
    @DisplayName("Q5d · buildSort keeps the requested order and adds id as tie-breaker")
    void buildSort() {
        Sort sort = queries.buildSort("age", "desc");
        assertThat(sort.getOrderFor("age")).as("age should be sorted").isNotNull();
        assertThat(sort.getOrderFor("age").getDirection()).isEqualTo(Sort.Direction.DESC);
        Sort.Order last = sort.toList().get(sort.toList().size() - 1);
        assertThat(last.getProperty()).as("End with Sort.by(\"id\").ascending() as tie-breaker").isEqualTo("id");
        assertThat(last.getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("Q5d · buildSort rejects properties outside the allowlist")
    void buildSortAllowlist() {
        assertThatThrownBy(() -> queries.buildSort("password", "asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported sort property: password");
        assertThat(queries.buildSort("admissionDate", "asc")).isNotNull();
    }

    @Test
    @DisplayName("Q5e · activePage(1, 2): second page of active students by name")
    void activePage() {
        Page<Student> page = queries.activePage(1, 2);
        assertThat(names(page.getContent())).containsExactly("Neha", "Priya");
        assertThat(page.getTotalElements()).as("5 active students").isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Q5e · activePage caps the page size at 50")
    void activePageCap() {
        Page<Student> page = queries.activePage(0, 1000);
        assertThat(page.getSize()).as("Clients must not get pages bigger than MAX_PAGE_SIZE").isEqualTo(StudentQueryService.MAX_PAGE_SIZE);
    }
}
