# Coder Army Spring Quests

Hands-on practice for the Spring Boot lectures of the Coder Army course. Every topic is a small kit you can play: a **browser course** (short lessons, quizzes, XP), a few **interactive labs** that explain the ideas with animations, and a **Java practice project** where real tests tell you when your code is right.

Built while working through the course, roughly lectures 25 to 40 so far. The quests are added one by one as I finish each topic.

## The quests

| Quest | Topic | Folder | Status |
|---|---|---|---|
| **JDBC Quest** | raw JDBC, then Spring `JdbcTemplate` | [`jdbc-quest/`](jdbc-quest) | done |
| **Hibernate Quest** | Hibernate, JPA mapping, relationships, cascade and N+1, Spring Data JPA | [`hibernate-quest/`](hibernate-quest) | done |
| **AOP Quest** | aspect-oriented programming (lectures 22 to 25) | `aop-quest/` | planned |
| More | added as I complete them | | |

Suggested order: JDBC first, then Hibernate. Hibernate makes much more sense once you have written the SQL and the row-to-object code by hand, because that is exactly the work it takes over.

## Folder layout

Every quest has the same shape, so a new one drops in next to the others:

```
coder-army-spring-quests/
├── README.md
├── LICENSE
├── jdbc-quest/
│   ├── README.md
│   ├── site/        the course page and its labs (open the .html files in a browser)
│   └── practice/    the Java project with empty methods and tests (Maven + JUnit)
├── hibernate-quest/
│   ├── README.md
│   ├── site/
│   └── practice/
└── ...              the next quest goes here
```

## How to use a quest

1. **Play the course.** Open `<quest>/site/<quest>.html` in a browser (for example `jdbc-quest/site/jdbc-quest.html`). Double-clicking the file is enough. The lab pages sit in the same folder, so the "Deep dive" links work. The pages load a few libraries from CDNs, so you need an internet connection.
2. **Do the local quests.** Open `<quest>/practice` as a Maven project (Java 21 or newer). Every method starts empty and every test starts red. Make them green one quest at a time:

   ```bash
   cd jdbc-quest/practice        # or hibernate-quest/practice
   mvn test
   ```

   The tests use H2, an in-memory database, so you don't need to install MySQL.
3. **Your progress stays on your machine.** The course saves XP, progress and your code in your browser (`localStorage`). Nothing is uploaded.

Each quest's own README has its learning path and the exact commands.

## Make it yours

The courses are plain files: no framework and no build step. You can fork this repo and change the lessons to match your own sources. [`jdbc-quest/README.md`](jdbc-quest/README.md) has a step-by-step guide to the lesson format.

## License

[MIT](LICENSE). You can use, change and share this, including your own fork. Keep the license notice with it.
