package dev.template.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DatabaseSchemaTest extends TestBase {

    @Test
    void flywayAppliedInitialSchema() {
        // existence check, not a row count: the container and schema are shared across
        // all TestBase subclasses, so row-level assertions would be order-dependent
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'todo')",
                Boolean.class);

        assertThat(exists).isTrue();
    }
}
