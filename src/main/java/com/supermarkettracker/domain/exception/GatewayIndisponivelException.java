package com.supermarkettracker.domain.exception;

public class GatewayIndisponivelException extends RuntimeException {
    public GatewayIndisponivelException(String message) {
        super(message);
    }

    public GatewayIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
