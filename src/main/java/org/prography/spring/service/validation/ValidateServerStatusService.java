package org.prography.spring.service.validation;

import org.prography.spring.common.BussinessException;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.springframework.boot.actuate.health.Status.UP;

@Service
public class ValidateServerStatusService {

    public void validateServerStatus(Status dbStatus, Status status) {
        if (dbStatus != UP || status != UP) {
            throw new BussinessException(SEVER_ERROR);
        }
    }
}
