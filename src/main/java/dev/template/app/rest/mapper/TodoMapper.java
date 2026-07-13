package dev.template.app.rest.mapper;

import dev.template.app.model.Todo;
import dev.template.app.rest.mapper.config.MapperConfig;
import dev.template.app.rest.payload.TodoDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface TodoMapper {

    TodoDto toDto(Todo todo);

    List<TodoDto> toDtoList(List<Todo> todos);
}
