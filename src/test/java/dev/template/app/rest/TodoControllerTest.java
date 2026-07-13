package dev.template.app.rest;

import static org.assertj.core.api.Assertions.assertThat;

import dev.template.app.TestBase;
import dev.template.app.rest.payload.ErrorResponse;
import dev.template.app.rest.payload.TodoDto;
import dev.template.app.rest.payload.TodoWriteDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class TodoControllerTest extends TestBase {

    @Autowired
    private TestRestTemplate rest;

    private String uniqueTitle() {
        return "Test todo " + UUID.randomUUID();
    }

    private TodoDto createTodo() {
        return rest.postForObject("/api/v1/todos", new TodoWriteDto(uniqueTitle(), false), TodoDto.class);
    }

    @Test
    void createThenAppearsInList() {
        TodoDto created = createTodo();

        ResponseEntity<TodoDto[]> response = rest.getForEntity("/api/v1/todos", TodoDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(List.of(response.getBody())).extracting(TodoDto::getId).contains(created.getId());
    }

    @Test
    void getReturnsCreatedTodo() {
        TodoDto created = createTodo();

        ResponseEntity<TodoDto> response = rest.getForEntity("/api/v1/todos/{id}", TodoDto.class, created.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(created.getId());
        assertThat(response.getBody().getTitle()).isEqualTo(created.getTitle());
    }

    @Test
    void getUnknownIdReturns404() {
        ResponseEntity<ErrorResponse> response =
                rest.getForEntity("/api/v1/todos/{id}", ErrorResponse.class, Long.MAX_VALUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void createValidatesBlankTitle() {
        ResponseEntity<ErrorResponse> response =
                rest.postForEntity("/api/v1/todos", new TodoWriteDto("", false), ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void updateChangesTitleAndCompleted() {
        TodoDto created = createTodo();

        String updatedTitle = uniqueTitle();
        rest.put("/api/v1/todos/{id}", new HttpEntity<>(new TodoWriteDto(updatedTitle, true)), created.getId());

        ResponseEntity<TodoDto[]> response = rest.getForEntity("/api/v1/todos", TodoDto[].class);
        TodoDto updated = List.of(response.getBody()).stream()
                .filter(dto -> dto.getId().equals(created.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(updated.getTitle()).isEqualTo(updatedTitle);
        assertThat(updated.getCompleted()).isTrue();
    }

    @Test
    void updateUnknownIdReturns404() {
        ResponseEntity<ErrorResponse> response = rest.exchange(
                "/api/v1/todos/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new TodoWriteDto(uniqueTitle(), false)),
                ErrorResponse.class,
                Long.MAX_VALUE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void deleteRemovesTodoAndIsIdempotentlyNotFoundAfter() {
        TodoDto created = createTodo();

        rest.delete("/api/v1/todos/{id}", created.getId());

        ResponseEntity<ErrorResponse> response = rest.exchange(
                "/api/v1/todos/{id}",
                HttpMethod.DELETE,
                null,
                ErrorResponse.class,
                created.getId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // framework exceptions must keep their status instead of collapsing into 500
    // (ExceptionHandlers extends ResponseEntityExceptionHandler)
    @Test
    void wrongMethodReturns405WithErrorBody() {
        ResponseEntity<ErrorResponse> response =
                rest.exchange("/api/v1/todos", HttpMethod.DELETE, null, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getHeaders().getAllow()).contains(HttpMethod.GET, HttpMethod.POST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(405);
        assertThat(response.getBody().getMessage()).isNotBlank();
    }

    @Test
    void unknownApiPathIsNotFoundAndNotHtml() {
        ResponseEntity<String> response = rest.getForEntity("/api/v1/nope", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isNotNull();
        assertThat(contentType.toString()).doesNotContain("text/html");
    }
}
