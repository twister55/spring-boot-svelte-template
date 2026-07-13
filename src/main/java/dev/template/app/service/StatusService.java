package dev.template.app.service;

import dev.template.app.exception.ResourceNotFoundException;
import dev.template.app.model.Status;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

    public Status getStatus() {
        return new Status("UP");
    }

    public Status getStatusByCode(String code) {
        if (!"UP".equalsIgnoreCase(code)) {
            throw new ResourceNotFoundException("Unknown status code: " + code);
        }
        return new Status("UP");
    }
}
