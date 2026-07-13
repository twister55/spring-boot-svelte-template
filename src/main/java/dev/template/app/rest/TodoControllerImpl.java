package dev.template.app.rest;

import dev.template.app.rest.mapper.TodoMapper;
import dev.template.app.rest.payload.TodoDto;
import dev.template.app.rest.payload.TodoWriteDto;
import dev.template.app.service.TodoService;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoControllerImpl implements TodoController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

    public TodoControllerImpl(TodoService todoService, TodoMapper todoMapper) {
        this.todoService = todoService;
        this.todoMapper = todoMapper;
    }

    @Override
    public List<TodoDto> getTodos() {
        return todoMapper.toDtoList(todoService.getTodos());
    }

    @Override
    public TodoDto getTodo(Long id) {
        return todoMapper.toDto(todoService.getTodo(id));
    }

    @Override
    public TodoDto createTodo(TodoWriteDto todoWriteDto) {
        return todoMapper.toDto(todoService.createTodo(todoWriteDto.getTitle(), todoWriteDto.getCompleted()));
    }

    @Override
    public TodoDto updateTodo(Long id, TodoWriteDto todoWriteDto) {
        return todoMapper.toDto(todoService.updateTodo(id, todoWriteDto.getTitle(), todoWriteDto.getCompleted()));
    }

    @Override
    public void deleteTodo(Long id) {
        todoService.deleteTodo(id);
    }
}
