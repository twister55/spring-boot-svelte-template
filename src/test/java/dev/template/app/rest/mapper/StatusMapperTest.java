package dev.template.app.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.template.app.model.Status;
import dev.template.app.rest.payload.StatusDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StatusMapperTest {

    private final StatusMapper mapper = Mappers.getMapper(StatusMapper.class);

    @Test
    void mapsDomainCodeToDtoStatus() {
        StatusDto dto = mapper.toDto(new Status("UP"));

        assertThat(dto.getStatus()).isEqualTo("UP");
    }
}
