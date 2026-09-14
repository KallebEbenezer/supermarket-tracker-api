package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.domain.exception.GatewayIndisponivelException;
import com.supermarkettracker.infrastructure.integration.sumup.SumupOAuthAuthorizationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL pública cadastrada na SumUp para o retorno da autorização OAuth.
 *
 * <p>Não exibe nem registra o código retornado. Após validar o state, a troca
 * acontece exclusivamente no servidor e o refresh token fica cifrado no banco.</p>
 */
@RestController
@RequestMapping("/api/v1/integracoes/sumup/oauth")
public class SumupOAuthCallbackController {
    private final SumupOAuthAuthorizationService authorizationService;

    public SumupOAuthCallbackController(SumupOAuthAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping(value = "/callback", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        if (error != null && !error.isBlank()) {
            return ResponseEntity.badRequest().body(page(
                    "Autorização não concluída",
                    "A autorização da SumUp foi cancelada ou recusada. Você pode fechar esta página e tentar novamente."));
        }
        if (code == null || code.isBlank() || state == null || state.isBlank()) {
            return ResponseEntity.badRequest().body(page(
                    "Retorno OAuth inválido",
                    "Não recebemos uma autorização válida da SumUp. Volte ao sistema e inicie o processo novamente."));
        }
        try {
            authorizationService.finishAuthorization(code, state);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(page(
                    "Retorno OAuth inválido", "A autorização expirou ou não corresponde ao pedido iniciado."));
        } catch (GatewayIndisponivelException exception) {
            return ResponseEntity.internalServerError().body(page(
                    "Não foi possível concluir a autorização", "Tente iniciar a autorização novamente."));
        }
        return ResponseEntity.ok(page(
                "Autorização concluída",
                "A conta SumUp foi autorizada com segurança. Você pode fechar esta página e voltar ao sistema."));
    }

    private String page(String title, String message) {
        return """
                <!doctype html>
                <html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
                <title>%s — Supermarket Tracker</title>
                <style>body{margin:0;font-family:Arial,sans-serif;background:#f6f8f6;color:#16351f}.card{max-width:560px;margin:12vh auto;padding:32px;background:#fff;border-radius:16px;box-shadow:0 8px 30px #0002}h1{margin-top:0;color:#1b5e20}p{line-height:1.5}</style>
                </head><body><main class="card"><h1>%s</h1><p>%s</p></main></body></html>
                """.formatted(title, title, message);
    }
}
