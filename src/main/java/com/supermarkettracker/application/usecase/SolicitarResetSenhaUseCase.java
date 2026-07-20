package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.SolicitarResetSenhaCommand;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.repository.CredencialRepository;
import java.time.Instant;
import java.util.UUID;

/**
 * Solicita a redefinição de senha, gerando um token (válido por 1 hora).
 *
 * <p>Em ambiente de produção o token seria enviado por e-mail (via
 * {@code EmailGateway}); neste MVP o token é devolvido para permitir o fluxo
 * no app. O e-mail é normalizado pelo value object {@link Email}.
 */
public final class SolicitarResetSenhaUseCase {
    private static final int EXPIRACAO_MINUTOS = 60;

    private final CredencialRepository credenciais;

    public SolicitarResetSenhaUseCase(CredencialRepository credenciais) {
        this.credenciais = credenciais;
    }

    public String executar(SolicitarResetSenhaCommand c) {
        var credencial = credenciais.buscarPorEmail(new Email(c.email())).orElse(null);
        if (credencial == null || !credencial.ativo()) {
            return null;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiraEm = Instant.now().plusSeconds(EXPIRACAO_MINUTOS * 60L);
        credenciais.salvar(credencial.comTokenReset(token, expiraEm));
        return token;
    }
}
