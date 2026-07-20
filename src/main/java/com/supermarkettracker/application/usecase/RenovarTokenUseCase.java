package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.RenovarTokenCommand;
import com.supermarkettracker.application.service.TokenInvalidoException;
import com.supermarkettracker.application.service.TokenService;
import com.supermarkettracker.domain.exception.CredenciaisInvalidasException;
import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CredencialRepository;

/** Renova o access token a partir de um refresh token válido. */
public final class RenovarTokenUseCase {
    private final CredencialRepository credenciais;
    private final TokenService tokenService;

    public RenovarTokenUseCase(CredencialRepository credenciais, TokenService tokenService) {
        this.credenciais = credenciais;
        this.tokenService = tokenService;
    }

    public String executar(RenovarTokenCommand c) {
        TokenService.TokenPayload payload = tokenService.validar(c.refreshToken());
        if (!tokenService.ehRefresh(payload)) {
            throw new TokenInvalidoException("Token de refresh invalido");
        }
        Credencial credencial = credenciais.buscarPorUsuarioId(new Identificador(payload.usuarioId()))
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais invalidas"));
        if (!credencial.ativo()) {
            throw new CredenciaisInvalidasException("Credenciais invalidas");
        }
        return tokenService.gerarAccessToken(payload.usuarioId(), credencial.email().valor(), credencial.papel().name());
    }
}
