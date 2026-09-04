package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.SolicitarResetSenhaCommand;
import com.supermarkettracker.domain.gateway.EmailGateway;
import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.repository.CredencialRepository;
import java.time.Instant;
import java.util.UUID;

/**
 * Solicita a redefinição de senha gerando um token (válido por 1 hora) e enviando-o
 * por e-mail ao usuário.
 *
 * <p>Para evitar enumeração de contas, a operação sempre responde com sucesso
 * independentemente de o e-mail existir — apenas registra log quando o usuário
 * não é encontrado.
 */
public final class SolicitarResetSenhaUseCase {
    private static final int EXPIRACAO_MINUTOS = 60;

    private final CredencialRepository credenciais;
    private final EmailGateway emailGateway;

    public SolicitarResetSenhaUseCase(CredencialRepository credenciais, EmailGateway emailGateway) {
        this.credenciais = credenciais;
        this.emailGateway = emailGateway;
    }

    public void executar(SolicitarResetSenhaCommand c) {
        Email email = new Email(c.email());
        var credencialOpt = credenciais.buscarPorEmail(email);
        if (credencialOpt.isEmpty() || !credencialOpt.get().ativo()) {
            // Não vaza se o e-mail existe; apenas registra e segue.
            return;
        }
        Credencial credencial = credencialOpt.get();
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiraEm = Instant.now().plusSeconds(EXPIRACAO_MINUTOS * 60L);
        credenciais.salvar(credencial.comTokenReset(token, expiraEm));

        String conteudo = "Olá,\n\n"
                + "Recebemos uma solicitação de redefinição de senha para a sua conta no Supermarket Tracker.\n"
                + "Use o token abaixo no aplicativo para definir uma nova senha (válido por 1 hora):\n\n"
                + "    " + token + "\n\n"
                + "Se você não fez essa solicitação, ignore esta mensagem.\n";

        emailGateway.enviar(new EmailGateway.EmailMensagem(
                credencial.email(),
                "Redefinição de senha — Supermarket Tracker",
                conteudo));
    }
}
