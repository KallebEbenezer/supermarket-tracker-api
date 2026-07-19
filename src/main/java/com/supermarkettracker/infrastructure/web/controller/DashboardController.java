package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.query.ConsultarDashboardQuery;
import com.supermarkettracker.application.usecase.ConsultarDashboardUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.DashboardResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final ConsultarDashboardUseCase consultarDashboard;

    public DashboardController(ConsultarDashboardUseCase consultarDashboard) {
        this.consultarDashboard = consultarDashboard;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> consultar(@RequestParam UUID empresaId,
            @RequestParam(required = false) UUID lojaId,
            @RequestParam(defaultValue = "10") int limite) {
        return ApiResponse.of(DashboardResponse.from(consultarDashboard.executar(
                new ConsultarDashboardQuery(empresaId, lojaId, limite))));
    }
}
