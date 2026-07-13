package dev.template.app.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.template.app.model.Todo;
import dev.template.app.rest.payload.TodoDto;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TodoMapperTest {

    private final TodoMapper mapper = Mappers.getMapper(TodoMapper.class);

    @Test
    void mapsDomainFieldsToDto() {
        OffsetDateTime createdAt = OffsetDateTime.now();
        TodoDto dto = mapper.toDto(new Todo(1L, "Buy milk", false, createdAt));

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Buy milk");
        assertThat(dto.getCompleted()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }
}
