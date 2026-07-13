-- Initial schema. Applied automatically by Flyway on app start and in Testcontainers tests.
CREATE TABLE IF NOT EXISTS todo (
    id         BIGSERIAL PRIMARY KEY,
    title      TEXT NOT NULL,
    completed  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
