package com.supermarkettracker.infrastructure.web.dto;

import java.time.Instant;

/** Contrato base de toda resposta exposta pela API. */
public sealed interface ApiResponse<T> permits SuccessResponse, ErrorResponse {
    Instant timestamp();
    int status();
    boolean success();

    static <T> SuccessResponse<T> of(T data, int status) {
        return new SuccessResponse<>(Instant.now(), status, true, data);
    }

    static <T> SuccessResponse<T> of(T data) {
        return of(data, 200);
    }
}
