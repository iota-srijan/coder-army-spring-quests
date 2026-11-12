# hibernate-practice

The local quests of **Hibernate Quest**: real entities, real Hibernate 7, real JUnit tests.
Each test class matches one course chapter. The tests run against an in-memory H2 database, so no MySQL is needed.

Course: [`../site/hibernate-quest.html`](../site/hibernate-quest.html)

## Run

In IntelliJ: open this folder as a Maven project, right-click a test class → Run.

From a terminal (Java 21):

```bash
mvn test -Dtest=Q1MappingTest
```

`mvn test` runs everything. Every test fails at the start. That's the point: make them green, one quest at a time.

## Quests

Do them in order. Later quests build on the mappings from earlier ones.

| Quest | File(s) | What you do | Tests |
|---|---|---|---|
| Q1 Entity mapping | `entity/Student.java` | Q1a `@Column` for name and email · Q1b enum stored as STRING · Q1c `@Transient` · Q1d `@ElementCollection` skills · Q1e second embedded address with `@AttributeOverrides` | `Q1MappingTest` (8) |
| Q2 Persistence context | `service/StudentService.java` | Q2a rename via dirty checking · Q2b `merge()` return value · Q2c batch import with `flush()` + `clear()` | `Q2InternalsTest` (9) |
| Q3 Relationships | `Student`, `Department`, `Course` | Q3a `@ManyToOne` department · Q3b `@OneToMany(mappedBy)` + helper methods · Q3c `@ManyToMany` join table + `addCourse` | `Q3RelationshipsTest` (8) |
| Q4 Cascade & fetching | `Order`, `Student`, `StudentRepository` | Q4a cascade PERSIST/REMOVE + `addItem` · Q4b `@EntityGraph` against N+1 · Q4c LAZY department · Q4d no cascade on shared courses | `Q4CascadeFetchTest` (6) |
| Q5 Spring Data JPA | `StudentRepository`, `service/StudentQueryService.java` | Q5a/b derived queries · Q5c `@Query` JPQL · Q5d sort allowlist + tie-breaker · Q5e paging with a size cap | `Q5SpringDataTest` (10) |

Every `TODO` in the code is tagged with its quest (for example `TODO Q3b`).

## What the tests check (no peeking at your code, only behaviour)

- **Schema:** the real columns and tables Hibernate created (`student_name`, `student_skills`, `student_course`…), read from H2's `INFORMATION_SCHEMA`.
- **SQL counts:** Hibernate statistics. `rename()` must be exactly 1 SELECT + 1 UPDATE. The N+1 test allows exactly 1 statement.
- **Persistence context:** how many entities are still managed after `importStudents()`, and whether `department` is loaded lazily.
- **Real commits:** tests don't wrap themselves in a transaction. If your service method isn't `@Transactional`, it shows.
- **Missing repository methods:** tests call them by name, so the project compiles before you add them. A missing one fails with the exact signature to declare.

## Traps the tests catch on purpose

- `EnumType.ORDINAL` stores `2` instead of `SUSPENDED`
- Embedding the same `Address` twice without renaming its columns (startup fails for every test: fix Q1e first)
- `save()` / `merge()` on an entity that should simply be managed (extra SELECT)
- Changing the detached object after `merge()` instead of the returned copy
- `i % 100 == 0` instead of `(i + 1) % 100 == 0`, and `clear()` before `flush()`
- Forgetting `mappedBy` (Hibernate creates an extra join table)
- Updating only the inverse side of a relationship
- `@ManyToOne` left EAGER; N+1 in a list query
- `CascadeType.REMOVE`/`ALL` on shared courses
- Passing client input straight into `Sort`; no tie-breaker

Watch the SQL while you work: set `spring.jpa.show-sql=true` in `src/test/resources/application.properties`.

Stuck? Ask Claude to review your code. There's no solution in this project on purpose.
