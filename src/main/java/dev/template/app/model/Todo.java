package dev.template.app.model;

import java.time.OffsetDateTime;

/**
 * Domain model, deliberately independent of the OpenAPI-generated payload classes.
 * Converted to {@code rest.payload} DTOs by the MapStruct mappers in {@code rest.mapper}.
 */
public record Todo(long id, String title, boolean completed, OffsetDateTime createdAt) {
}
