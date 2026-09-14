package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.infrastructure.integration.sumup.SumupTapToPayTokenService;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SumUp Tap-to-Pay")
@RestController
@RequestMapping("/api/v1/integracoes/sumup")
public class SumupTapToPayController {
    private final SumupTapToPayTokenService tokenService;
    public SumupTapToPayController(SumupTapToPayTokenService tokenService) { this.tokenService = tokenService; }

    @Operation(summary = "Obter access token OAuth temporário para o SDK Tap-to-Pay")
    @GetMapping("/tap-to-pay-token")
    public ApiResponse<SumupTapToPayTokenService.AccessToken> accessToken() {
        return ApiResponse.of(tokenService.accessToken());
    }
}
