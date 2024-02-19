package org.prography.spring.service.validation;

import org.prography.spring.common.BussinessException;
import org.prography.spring.dto.request.InitializationRequest;
import org.springframework.stereotype.Service;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

@Service
public class ValidateInitService {

    public void validateInitializationRequest(InitializationRequest initializationRequest) {
        if(initializationRequest.validateInitRequest()){
            throw new BussinessException(BAD_REQUEST);
        }
    }
}
