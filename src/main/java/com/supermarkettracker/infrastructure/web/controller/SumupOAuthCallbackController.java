package com.supermarkettracker.infrastructure.web.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL pública cadastrada na SumUp para o retorno da autorização OAuth.
 *
 * <p>Não exibe nem registra o código retornado. A troca do código e a
 * persistência segura do refresh token serão habilitadas depois que as
 * credenciais da aplicação OAuth forem configuradas no ambiente.</p>
 */
@RestController
@RequestMapping("/api/v1/integracoes/sumup/oauth")
public class SumupOAuthCallbackController {

    @GetMapping(value = "/callback", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error) {
        if (error != null && !error.isBlank()) {
            return ResponseEntity.badRequest().body(page(
                    "Autorização não concluída",
                    "A autorização da SumUp foi cancelada ou recusada. Você pode fechar esta página e tentar novamente."));
        }
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(page(
                    "Retorno OAuth inválido",
                    "Não recebemos uma autorização válida da SumUp. Volte ao sistema e inicie o processo novamente."));
        }
        return ResponseEntity.ok(page(
                "Autorização recebida",
                "A SumUp retornou ao Supermarket Tracker. Você pode fechar esta página e voltar ao sistema."));
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
