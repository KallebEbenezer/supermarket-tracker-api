package com.supermarkettracker.infrastructure.web.dto;

import java.time.Instant;
import java.util.Map;

public record ErroResponse(Instant timestamp, int status, String mensagem, Map<String, String> campos) { }
