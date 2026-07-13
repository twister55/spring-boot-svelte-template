package dev.template.app.rest;

import dev.template.app.exception.ResourceNotFoundException;
import dev.template.app.rest.payload.StatusDto;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusControllerImpl implements StatusController {

    @Override
    public StatusDto getStatus() {
        return new StatusDto("UP");
    }

    @Override
    public StatusDto getStatusByCode(String code) {
        if (!"UP".equalsIgnoreCase(code)) {
            throw new ResourceNotFoundException("Unknown status code: " + code);
        }
        return new StatusDto("UP");
    }
}
