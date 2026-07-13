package dev.template.app.rest.mapper;

import dev.template.app.model.Status;
import dev.template.app.rest.mapper.config.MapperConfig;
import dev.template.app.rest.payload.StatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface StatusMapper {

    @Mapping(target = "status", source = "code")
    StatusDto toDto(Status status);
}
