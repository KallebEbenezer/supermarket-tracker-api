package com.supermarkettracker.application.query;

import java.util.UUID;

public record ConsultarDashboardQuery(UUID empresaId, UUID lojaId, int limite) { }
