# Hibernate Quest

> Part of [Coder Army Spring Quests](../README.md).

Learn Hibernate and JPA by playing it. You start from the problem it solves (turning rows into objects without writing the SQL by hand), then work up to mapping, the persistence context, relationships, cascade and the N+1 problem, and Spring Data JPA.

It's a small learning kit with three parts:

| Part | What it is | Where |
|---|---|---|
| **The course** | A game-like course in your browser: 58 lessons with quizzes, Java code lessons, local quests and a boss at the end of every chapter. XP and levels. | `site/hibernate-quest.html` |
| **Five explainer labs** | Interactive pages with animations and simulators for the ideas the course uses. The course links into them. | `site/*.html` (table below) |
| **A Java practice project** | Real Java (Spring Boot, Hibernate, JUnit). You fill in the empty parts and the tests tell you when you're right. No MySQL needed. | `practice/` |

## Quick start

**Play the course.** Open `site/hibernate-quest.html` in a browser (double-click it). The lab pages sit next to it, so the "Deep dive" links work. The pages load a few libraries from CDNs, so you need an internet connection.

```bash
# or serve the folder locally
cd site
python -m http.server 8000      # then open http://localhost:8000/hibernate-quest.html
```

Your progress, XP and code are saved in your browser (`localStorage`) and don't leave your machine. **Every chapter is open from the start**, so you can begin with any of the five. Inside a chapter the lessons unlock one by one. The sidebar also has "Unlock all lessons (review mode)".

**Run the practice project.** You need Java 21+ and Maven.

```bash
cd practice
mvn test                              # everything (starts out all red)
mvn test -Dtest=Q1MappingTest         # one chapter at a time
```

Every `TODO` in the code is tagged with its quest, for example `TODO Q3b`. The tests run against H2, an in-memory database, so nothing has to be installed. See [`practice/README.md`](practice/README.md) for the quest table and what each test checks.

## The learning path

Five chapters. Each one ends with a local quest (the Java project) and a boss quiz.

| # | Chapter | You learn | Lab | Local quests |
|---|---|---|---|---|
| 1 | **Meet Hibernate** | why an ORM, your first entity, `application.properties`, what `ddl-auto` does, `@Column`, enums, converters, embedded types, collections | [Hibernate Intro](site/hibernate-intro.html) | `Q1MappingTest` |
| 2 | **Inside Hibernate** | the persistence context, the first-level cache, dirty checking, entity states, `merge()`, batch imports, transactions, flush vs commit | [Hibernate Internals](site/hibernate-internals.html) | `Q2InternalsTest` |
| 3 | **Relationships** | `@ManyToOne`, the owning and inverse side, `mappedBy`, helper methods, `@OneToOne`, `@ManyToMany` with a join table, Lombok traps | [JPA Relationships](site/jpa-relationships.html) | `Q3RelationshipsTest` |
| 4 | **Cascade, Lazy & N+1** | what flows through a relationship, lazy vs eager, counting the N+1 problem, joined queries, `@EntityGraph`, making the hidden SQL visible | [Cascade and N+1](site/jpa-fetching.html) | `Q4CascadeFetchTest` |
| 5 | **Spring Data JPA** | repository interfaces, derived queries, JPQL, sorting safely, paging, `Page` vs `Slice`, `countQuery` | [Spring Data JPA](site/spring-data-jpa.html) | `Q5SpringDataTest` |

Suggested order for each chapter: read the lab, play the chapter, then do its local quest. Do the chapters in order the first time, because the practice project's mappings build on each other.

## Fork it and learn Hibernate your way

The course is just files, with no build step. The lesson format (quiz, Java code lesson, local quest, boss) is explained step by step in the [JDBC Quest README](../jdbc-quest/README.md#fork-it-and-learn-jdbc-your-way). Everything there applies here too: the lessons live in the `COURSE DATA` block of `site/hibernate-quest.html`, and the progress key is the `storageKey` setting.

## Folder layout

```
site/
  hibernate-quest.html     the course (all lessons live in one file)
  hibernate-intro.html     lab 1: mapping and CRUD, converters, embedded types
  hibernate-internals.html lab 2: persistence context, dirty checking, flush, transactions
  jpa-relationships.html   lab 3: owning vs inverse side, join tables
  jpa-fetching.html        lab 4: cascade, lazy loading, the N+1 problem
  spring-data-jpa.html     lab 5: repositories, JPQL, paging
practice/
  pom.xml                  Spring Boot parent, Java 21, H2, JUnit 5, AssertJ
  src/main/java/...        the entities and services you fill in
  src/test/java/...        the tests that check you
```
