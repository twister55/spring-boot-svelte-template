package dev.template.app.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends ApplicationException {

    public NotAuthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
