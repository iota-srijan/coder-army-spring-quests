package com.coderarmy.hibernate.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Base class for every quest test.
 * No test-managed transaction: each test commits for real, like your service would.
 * The database is wiped before every test.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class PracticeTest {

    @Autowired
    protected JdbcTemplate jdbc;

    @Autowired
    protected TransactionTemplate tx;

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected EntityManagerFactory emf;

    @BeforeEach
    void wipeDatabase() {
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");
        for (String table : jdbc.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'",
                String.class)) {
            jdbc.execute("TRUNCATE TABLE \"" + table + "\" RESTART IDENTITY");
        }
        jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");
        stats().clear();
    }

    /** Runs the work in its own committed transaction and returns its result. */
    protected <T> T inTx(Supplier<T> work) {
        return tx.execute(status -> work.get());
    }

    protected void inTxRun(Runnable work) {
        tx.executeWithoutResult(status -> work.run());
    }

    /** Hibernate's counters (statements sent, entities loaded...). */
    protected Statistics stats() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    /** Lower-case table names that exist right now. */
    protected Set<String> tables() {
        return jdbc.queryForList(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'",
                        String.class)
                .stream().map(t -> t.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }

    /** Lower-case column names of a table (empty if the table doesn't exist). */
    protected Set<String> columns(String table) {
        return jdbc.queryForList(
                        "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ?",
                        String.class, table.toUpperCase(Locale.ROOT))
                .stream().map(c -> c.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }

    /** INFORMATION_SCHEMA row for one column, or null if it doesn't exist. */
    protected Map<String, Object> column(String table, String column) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                table.toUpperCase(Locale.ROOT), column.toUpperCase(Locale.ROOT));
        return rows.isEmpty() ? null : rows.get(0);
    }
}
