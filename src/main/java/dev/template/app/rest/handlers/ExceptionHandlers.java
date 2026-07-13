package dev.template.app.rest.handlers;

import dev.template.app.exception.ApplicationException;
import dev.template.app.rest.payload.ErrorResponse;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Shapes API errors into the ErrorResponse envelope from schema.yaml. Extends
 * ResponseEntityExceptionHandler so framework exceptions keep their proper
 * statuses (405 wrong method, 400 malformed body, 415/406 media types,
 * ResponseStatusException, ...) with only the body replaced in
 * handleExceptionInternal — a bare @ExceptionHandler(Exception.class) would
 * outrank Spring's default resolvers and turn them all into 500s.
 */
@RestControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException ex) {
        return errorResponse(ex.getStatus(), ex.getMessage(), new HttpHeaders());
    }

    // Only ConstraintViolationException, not the whole ValidationException
    // hierarchy: that hierarchy also covers validator *configuration* errors
    // (UnexpectedTypeException etc.), which are server bugs and must stay 500s.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), new HttpHeaders());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        logger.error("Unhandled exception", ex);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", new HttpHeaders());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return errorResponse(status, message.isEmpty() ? "Invalid request" : message, headers);
    }

    // Every framework exception the base class handles funnels through here;
    // headers must be preserved (e.g. Allow on a 405).
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        String message = body instanceof ProblemDetail problem && problem.getDetail() != null
                ? problem.getDetail()
                : reasonPhrase(statusCode);
        return errorResponse(statusCode, message, headers);
    }

    private static ResponseEntity<Object> errorResponse(HttpStatusCode status, String message, HttpHeaders headers) {
        ErrorResponse body = new ErrorResponse(status.value(), message, OffsetDateTime.now());
        return ResponseEntity.status(status).headers(headers).body(body);
    }

    private static String reasonPhrase(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status != null ? status.getReasonPhrase() : "Error";
    }
}
