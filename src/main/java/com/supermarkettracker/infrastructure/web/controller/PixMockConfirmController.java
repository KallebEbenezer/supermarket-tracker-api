package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.PixNotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint auxiliar para confirmar pagamentos PIX localmente em desenvolvimento.
 *
 * <p>Restrito ao profile {@code dev}: o bean não é registrado em outros profiles, portanto o
 * endpoint simplesmente não existe fora de desenvolvimento. Em produção a confirmação de
 * pagamentos ocorre exclusivamente via webhook autenticado do Mercado Pago.
 */
@Tag(name = "PIX Webhooks")
@RestController
@RequestMapping("/api/v1/webhooks")
@Profile("dev")
public class PixMockConfirmController {
    private static final Logger log = LoggerFactory.getLogger(PixMockConfirmController.class);

    private final PixNotificationService notificationService;

    public PixMockConfirmController(PixNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/pix/mock-confirm/{transacaoId}")
    public ResponseEntity<Map<String, Object>> mockConfirm(
            @PathVariable String transacaoId,
            @RequestParam(defaultValue = "APROVADO") String status) {
        log.info("Mock confirm PIX (profile=dev): transacaoId={}, status={}", transacaoId, status);

        notificationService.processarNotificacao(transacaoId, status);

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "transacaoId", transacaoId,
                "novoStatus", status
        ));
    }
}
