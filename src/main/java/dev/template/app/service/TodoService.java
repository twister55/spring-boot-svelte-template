package dev.template.app.service;

import dev.template.app.exception.ResourceNotFoundException;
import dev.template.app.model.Todo;
import dev.template.app.repository.TodoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodo(long id) {
        return todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unknown todo id: " + id));
    }

    public Todo createTodo(String title, boolean completed) {
        return todoRepository.insert(title, completed);
    }

    public Todo updateTodo(long id, String title, boolean completed) {
        return todoRepository
                .update(id, title, completed)
                .orElseThrow(() -> new ResourceNotFoundException("Unknown todo id: " + id));
    }

    public void deleteTodo(long id) {
        if (!todoRepository.delete(id)) {
            throw new ResourceNotFoundException("Unknown todo id: " + id);
        }
    }
}
