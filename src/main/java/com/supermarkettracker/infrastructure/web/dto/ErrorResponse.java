package com.supermarkettracker.infrastructure.web.dto;

import java.time.Instant;
import java.util.Map;

/** Não inclui causa ou stack trace para impedir vazamento de detalhes internos. */
public record ErrorResponse(Instant timestamp, int status, boolean success, String code, String message,
                            String path, String traceId, Map<String, String> validationErrors)
        implements ApiResponse<Void> { }
