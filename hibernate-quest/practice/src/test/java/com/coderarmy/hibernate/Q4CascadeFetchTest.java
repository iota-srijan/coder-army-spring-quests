package com.coderarmy.hibernate;

import com.coderarmy.hibernate.entity.Course;
import com.coderarmy.hibernate.entity.Department;
import com.coderarmy.hibernate.entity.Order;
import com.coderarmy.hibernate.entity.OrderItem;
import com.coderarmy.hibernate.entity.Student;
import com.coderarmy.hibernate.repository.StudentRepository;
import com.coderarmy.hibernate.support.PracticeTest;
import com.coderarmy.hibernate.support.Repo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("Q4 · Cascade and fetching (Order, Student, StudentRepository)")
class Q4CascadeFetchTest extends PracticeTest {

    @Autowired
    StudentRepository repo;

    private int count(String table) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }

    private Long saveOrderWithTwoItems() {
        return inTx(() -> {
            Order order = new Order("Rahul");
            order.addItem(new OrderItem("Keyboard"));
            order.addItem(new OrderItem("Mouse"));
            em.persist(order);   // only the order: the items must follow by cascade
            return order.getId();
        });
    }

    @Test
    @DisplayName("Q4a · addItem() sets the owning side")
    void addItemSetsOwner() {
        Order order = new Order("Rahul");
        OrderItem item = new OrderItem("Keyboard");
        order.addItem(item);
        assertThat(order.getItems()).contains(item);
        assertThat(item.getOrder()).as("addItem must call item.setOrder(this): OrderItem.order owns order_id").isSameAs(order);
    }

    @Test
    @DisplayName("Q4a · persist(order) also saves its items, with order_id set")
    void cascadePersist() {
        Long orderId = saveOrderWithTwoItems();
        assertThat(count("order_item")).as("Items weren't saved. Add cascade PERSIST to Order.items").isEqualTo(2);
        Integer linked = jdbc.queryForObject("SELECT COUNT(*) FROM order_item WHERE order_id = ?", Integer.class, orderId);
        assertThat(linked).as("Items saved with order_id NULL: set the owning side in addItem").isEqualTo(2);
    }

    @Test
    @DisplayName("Q4a · remove(order) also removes its items")
    void cascadeRemove() {
        Long orderId = saveOrderWithTwoItems();
        inTxRun(() -> em.remove(em.find(Order.class, orderId)));
        assertThat(count("orders")).isZero();
        assertThat(count("order_item")).as("Items left behind. Add cascade REMOVE to Order.items").isZero();
    }

    @Test
    @DisplayName("Q4b · findAllByOrderByIdAsc() loads students AND departments in 1 query")
    void entityGraphNoNPlusOne() {
        inTxRun(() -> {
            Department[] depts = {new Department("Engineering"), new Department("HR"), new Department("Design")};
            for (Department d : depts) em.persist(d);
            for (int i = 0; i < 6; i++) {
                Student s = new Student("S" + i, "s" + i + "@gmail.com", 20 + i);
                s.setDepartment(depts[i % 3]);
                em.persist(s);
            }
        });
        stats().clear();
        List<String> names = inTx(() -> {
            List<Student> students = Repo.call(repo, "@EntityGraph(attributePaths = \"department\") List<Student> findAllByOrderByIdAsc();", "findAllByOrderByIdAsc");
            return students.stream().map(s -> s.getDepartment().getName()).toList();
        });
        assertThat(names).containsExactly("Engineering", "HR", "Design", "Engineering", "HR", "Design");
        assertThat(stats().getPrepareStatementCount())
                .as("%d SQL statements for one list: that's N+1. Put @EntityGraph(attributePaths = \"department\") on the method.",
                        stats().getPrepareStatementCount())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Q4c · Student.department is LAZY")
    void departmentIsLazy() {
        assertThat(columns("students")).as("Do Q3a first").contains("department_id");
        Long id = inTx(() -> {
            Department eng = new Department("Engineering");
            em.persist(eng);
            Student s = new Student("Rahul", "rahul@gmail.com", 24);
            s.setDepartment(eng);
            em.persist(s);
            return s.getId();
        });
        boolean loaded = inTx(() -> emf.getPersistenceUnitUtil().isLoaded(em.find(Student.class, id), "department"));
        assertThat(loaded).as("department was loaded with the student. @ManyToOne is EAGER by default: use fetch = FetchType.LAZY").isFalse();
    }

    @Test
    @DisplayName("Q4d · deleting a student keeps the shared course")
    void noCascadeRemoveOnSharedCourse() {
        Long[] ids = inTx(() -> {
            Course java = new Course("Java");
            em.persist(java);
            Student rahul = new Student("Rahul", "rahul@gmail.com", 24);
            Student priya = new Student("Priya", "priya@gmail.com", 22);
            rahul.addCourse(java);
            priya.addCourse(java);
            em.persist(rahul);
            em.persist(priya);
            return new Long[]{rahul.getId(), java.getId()};
        });
        assertThat(count("student_course")).isEqualTo(2);
        try {
            inTxRun(() -> {
                Student rahul = em.find(Student.class, ids[0]);
                for (Course c : rahul.getCourses()) c.getStudents().remove(rahul);
                em.remove(rahul);
            });
        } catch (RuntimeException e) {
            fail("Removing Rahul also tried to delete the Java course that Priya still takes. "
                    + "Don't cascade REMOVE (or ALL) on Student.courses: courses are shared.", e);
        }
        assertThat(count("courses")).as("The course was deleted for everyone. Never cascade REMOVE on Student.courses").isEqualTo(1);
        assertThat(count("student_course")).as("Priya's enrolment must remain").isEqualTo(1);
    }
}
