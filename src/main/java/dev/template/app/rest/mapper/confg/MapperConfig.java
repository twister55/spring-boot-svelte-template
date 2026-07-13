package dev.template.app.rest.mapper.confg;

import org.mapstruct.ReportingPolicy;

/**
 * Shared MapStruct config for every mapper converting domain models to OpenAPI-generated
 * DTOs ({@code rest.payload}). {@code unmappedTargetPolicy = ERROR} means adding a schema.yaml
 * property without updating its mapper fails the build instead of silently leaving it null.
 */
@org.mapstruct.MapperConfig(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapperConfig {
}
