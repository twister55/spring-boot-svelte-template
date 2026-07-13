package dev.template.app.rest;

import dev.template.app.rest.mapper.StatusMapper;
import dev.template.app.rest.payload.StatusDto;
import dev.template.app.service.StatusService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusControllerImpl implements StatusController {

    private final StatusService statusService;
    private final StatusMapper statusMapper;

    public StatusControllerImpl(StatusService statusService, StatusMapper statusMapper) {
        this.statusService = statusService;
        this.statusMapper = statusMapper;
    }

    @Override
    public StatusDto getStatus() {
        return statusMapper.toDto(statusService.getStatus());
    }

    @Override
    public StatusDto getStatusByCode(String code) {
        return statusMapper.toDto(statusService.getStatusByCode(code));
    }
}
