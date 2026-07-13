package dev.template.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base for integration tests. The static PostgreSQL container is shared by every subclass
 * (one container and one cached Spring context per JVM); {@code @ServiceConnection} both
 * starts it and wires the datasource. Flyway applies db/migration on context start.
 * Requires a running Docker daemon.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public abstract class TestBase {

    @ServiceConnection
    protected static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17")
            .withDatabaseName("app")
            .withUsername("app")
            .withPassword("app");

    @Autowired
    protected JdbcTemplate jdbcTemplate;
}
