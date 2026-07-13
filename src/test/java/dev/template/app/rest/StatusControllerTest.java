package dev.template.app.rest;

import static org.assertj.core.api.Assertions.assertThat;

import dev.template.app.TestBase;
import dev.template.app.rest.payload.ErrorResponse;
import dev.template.app.rest.payload.StatusDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class StatusControllerTest extends TestBase {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void statusEndpointRespondsUp() {
        ResponseEntity<StatusDto> response = rest.getForEntity("/api/v1/status", StatusDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("UP");
    }

    @Test
    void statusByCodeRespondsUp() {
        ResponseEntity<StatusDto> response = rest.getForEntity("/api/v1/status/UP", StatusDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("UP");
    }

    @Test
    void statusByCodeUnknownReturns404WithErrorBody() {
        ResponseEntity<ErrorResponse> response = rest.getForEntity("/api/v1/status/nope", ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
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

    // framework exceptions must keep their status instead of collapsing into 500
    // (ExceptionHandlers extends ResponseEntityExceptionHandler)
    @Test
    void wrongMethodReturns405WithErrorBody() {
        ResponseEntity<ErrorResponse> response = rest.postForEntity("/api/v1/status", null, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getHeaders().getAllow()).contains(HttpMethod.GET);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(405);
        assertThat(response.getBody().getMessage()).isNotBlank();
    }
}
