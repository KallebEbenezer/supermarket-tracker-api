package com.supermarkettracker.infrastructure.web.dto;

import java.time.Instant;

public record PixDetailsResponse(
    String qrCode,
    String copiaCola,
    String status,
    Instant expiracao
) { }
