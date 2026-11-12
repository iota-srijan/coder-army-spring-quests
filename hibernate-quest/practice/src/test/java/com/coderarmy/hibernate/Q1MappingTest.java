package com.coderarmy.hibernate;

import com.coderarmy.hibernate.entity.Address;
import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.entity.StudentStatus;
import com.coderarmy.hibernate.support.PracticeTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Q1 · Entity mapping (Student.java)")
class Q1MappingTest extends PracticeTest {

    private Long save(Student s) {
        return inTx(() -> { em.persist(s); return s.getId(); });
    }

    @Test
    @DisplayName("Q1a · name lives in column student_name, NOT NULL, length 100")
    void nameColumn() {
        Map<String, Object> col = column("students", "student_name");
        assertThat(col).as("No column student_name. Put @Column(name = \"student_name\", ...) on the name field.").isNotNull();
        assertThat(col.get("IS_NULLABLE")).as("student_name must be NOT NULL: nullable = false").isEqualTo("NO");
        assertThat(((Number) col.get("CHARACTER_MAXIMUM_LENGTH")).intValue()).as("length = 100").isEqualTo(100);
        assertThat(columns("students")).as("The old column 'name' should be gone").doesNotContain("name");
    }

    @Test
    @DisplayName("Q1a · email is NOT NULL, length 150")
    void emailColumn() {
        Map<String, Object> col = column("students", "email");
        assertThat(col).as("Column email should exist").isNotNull();
        assertThat(col.get("IS_NULLABLE")).as("email must be NOT NULL: nullable = false").isEqualTo("NO");
        assertThat(((Number) col.get("CHARACTER_MAXIMUM_LENGTH")).intValue()).as("length = 150").isEqualTo(150);
    }

    @Test
    @DisplayName("Q1a · a second student with the same email is rejected")
    void emailUnique() {
        save(new Student("Rahul", "same@gmail.com", 24));
        assertThatThrownBy(() -> save(new Student("Rohan", "same@gmail.com", 25)))
                .as("The database accepted a duplicate email. Add unique = true to email's @Column.")
                .isNotInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Q1b · status is stored by name, not by position")
    void statusByName() {
        Student s = new Student("Rahul", "rahul@gmail.com", 24);
        s.setStatus(StudentStatus.SUSPENDED);
        save(s);
        String stored = jdbc.queryForObject("SELECT CAST(status AS VARCHAR) FROM students", String.class);
        assertThat(stored).as("Stored '%s'. Use @Enumerated(EnumType.STRING), not ORDINAL.", stored).isEqualTo("SUSPENDED");
    }

    @Test
    @DisplayName("Q1c · displayName never becomes a column")
    void displayNameTransient() {
        assertThat(columns("students")).as("Column display_name exists. Mark displayName @Transient.").doesNotContain("display_name");
        Student s = new Student("Rahul", "rahul@gmail.com", 24);
        s.setDisplayName("Rahul (CSE)");
        Long id = save(s);
        String reloaded = inTx(() -> em.find(Student.class, id).getDisplayName());
        assertThat(reloaded).as("displayName came back from the database, so it was stored").isNull();
    }

    @Test
    @DisplayName("Q1d · skills are stored in student_skills(student_id, skill)")
    void skillsCollectionTable() {
        assertThat(tables()).as("No table student_skills. Use @ElementCollection + @CollectionTable(name = \"student_skills\", ...)").contains("student_skills");
        assertThat(columns("student_skills")).as("Columns should be student_id and skill").containsExactlyInAnyOrder("student_id", "skill");
        Student s = new Student("Rahul", "rahul@gmail.com", 24);
        s.getSkills().add("Java");
        s.getSkills().add("SQL");
        save(s);
        List<String> skills = jdbc.queryForList("SELECT skill FROM student_skills ORDER BY skill", String.class);
        assertThat(skills).containsExactly("Java", "SQL");
    }

    @Test
    @DisplayName("Q1e · permanentAddress gets its own permanent_* columns")
    void permanentAddressColumns() {
        assertThat(columns("students"))
                .as("Embed permanentAddress and rename its columns with @AttributeOverrides")
                .contains("permanent_address_line", "permanent_city", "permanent_state", "permanent_postal_code")
                .as("currentAddress keeps the original columns")
                .contains("address_line", "city", "state", "postal_code");
    }

    @Test
    @DisplayName("Q1e · both addresses survive a round trip")
    void addressesRoundTrip() {
        Student s = new Student("Rahul", "rahul@gmail.com", 24);
        s.setCurrentAddress(new Address("12 MG Rd", "Pune", "MH", "411001"));
        s.setPermanentAddress(new Address("4 Lake View", "Bhopal", "MP", "462001"));
        Long id = save(s);
        Student back = inTx(() -> em.find(Student.class, id));
        assertThat(back.getCurrentAddress().getCity()).isEqualTo("Pune");
        assertThat(back.getPermanentAddress()).as("permanentAddress wasn't stored").isNotNull();
        assertThat(back.getPermanentAddress().getCity()).isEqualTo("Bhopal");
    }
}
