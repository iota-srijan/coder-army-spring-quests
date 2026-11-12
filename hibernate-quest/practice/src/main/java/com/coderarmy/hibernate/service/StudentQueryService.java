package com.coderarmy.hibernate.service;

import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/** Quest 5: sorting and pagination on top of Spring Data JPA. */
@Service
public class StudentQueryService {

    public static final int MAX_PAGE_SIZE = 50;

    private final StudentRepository studentRepository;

    public StudentQueryService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * TODO Q5d: build a Sort from client input (GET /students?sortBy=...&direction=...).
     * Only "name", "age" and "admissionDate" are allowed; anything else →
     * IllegalArgumentException("Unsupported sort property: " + sortBy), checked BEFORE building the Sort.
     * Always add id ascending as a tie-breaker after the requested order.
     */
    public Sort buildSort(String sortBy, String direction) {
        throw new UnsupportedOperationException("TODO Q5d: buildSort()");
    }

    /**
     * TODO Q5e: one page of ACTIVE students, sorted by name then id.
     * Never return pages bigger than MAX_PAGE_SIZE, whatever the client asks for.
     * Needs the repository method from Q5e.
     */
    public Page<Student> activePage(int page, int size) {
        throw new UnsupportedOperationException("TODO Q5e: activePage()");
    }
}
