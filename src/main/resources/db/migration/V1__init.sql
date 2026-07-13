-- Initial schema. Applied automatically by Flyway on app start and in Testcontainers tests.
CREATE TABLE IF NOT EXISTS app_info (
    id    BIGSERIAL PRIMARY KEY,
    key   TEXT NOT NULL UNIQUE,
    value TEXT NOT NULL
);
