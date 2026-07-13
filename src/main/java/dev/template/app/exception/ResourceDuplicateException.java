package dev.template.app.exception;

import org.springframework.http.HttpStatus;

public class ResourceDuplicateException extends ApplicationException {

    public ResourceDuplicateException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
