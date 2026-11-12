package com.coderarmy.hibernate.repository;

import com.coderarmy.hibernate.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data creates the implementation at startup. You only declare methods.
 * The tests look these methods up by name, so the project compiles before you add them.
 *
 * TODO Q4b: List<Student> findAllByOrderByIdAsc()
 *           must load each student's department in the SAME query (no N+1).
 *
 * TODO Q5a: Optional<Student> findByEmail(String email)
 *           boolean existsByEmail(String email)
 *
 * TODO Q5b: List<Student> findByAgeGreaterThan(Integer age)
 *           List<Student> findTop3ByActiveTrueOrderByAgeDesc()
 *           List<Student> findByNameContainingIgnoreCase(String part)
 *
 * TODO Q5c: List<Student> findStudentsInAgeRange(Integer minimumAge, Integer maximumAge)
 *           written as JPQL with @Query and named parameters :minimumAge / :maximumAge.
 *
 * TODO Q5e: Page<Student> findByActiveTrue(Pageable pageable)
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
}
