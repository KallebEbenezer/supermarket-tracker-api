package com.supermarkettracker.infrastructure.web.dto;

import java.time.Instant;

public record SuccessResponse<T>(Instant timestamp, int status, boolean success, T data) implements ApiResponse<T> { }
