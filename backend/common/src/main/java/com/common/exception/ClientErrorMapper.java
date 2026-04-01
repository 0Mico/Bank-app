package com.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class ClientErrorMapper {

    private ClientErrorMapper() {}

    public static RuntimeException handleException(String serviceName, Exception e) {
        if (e instanceof HttpClientErrorException httpException) {
            ErrorResponse errorResponse = httpException.getResponseBodyAs(ErrorResponse.class);
            String message = (errorResponse != null && errorResponse.getMessage() != null)
                    ? errorResponse.getMessage()
                    : httpException.getStatusText();

            return switch (httpException.getStatusCode()) {
                case HttpStatus.NOT_FOUND -> new ResourceNotFoundException(message);
                case HttpStatus.BAD_REQUEST -> new BadRequestException(message);
                case HttpStatus.UNAUTHORIZED -> new UnauthorizedException(message);
                default -> new ServiceUnavailableException("Service " + serviceName + " error: " + message, e);
            };
        }
        return new ServiceUnavailableException("Service " + serviceName + " unavailable", e);
    }
}
