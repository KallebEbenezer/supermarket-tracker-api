package com.supermarkettracker.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.supermarkettracker.application.command.SolicitarResetSenhaCommand;
import com.supermarkettracker.domain.gateway.EmailGateway;
import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.enums.PapelUsuario;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CredencialRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SolicitarResetSenhaUseCaseTest {

    @Test
    void emailNaoCadastrado_naoEnviaEmailNemLancaExcecao() {
        var credenciais = mock(CredencialRepository.class);
        var emailGateway = mock(EmailGateway.class);
        when(credenciais.buscarPorEmail(any())).thenReturn(Optional.empty());

        new SolicitarResetSenhaUseCase(credenciais, emailGateway)
                .executar(new SolicitarResetSenhaCommand("inexistente@x.com"));

        verify(credenciais, never()).salvar(any());
        verify(emailGateway, never()).enviar(any());
    }

    @Test
    void emailCadastrado_ativo_geraTokenEnviaEmailESalvaCredencial() {
        var credenciais = mock(CredencialRepository.class);
        var emailGateway = mock(EmailGateway.class);
        var email = new Email("user@x.com");
        var credencial = new Credencial(new Identificador(UUID.randomUUID()), email, "hash",
                new Identificador(UUID.randomUUID()), PapelUsuario.CAIXA, true, null, null,
                Instant.now(), Instant.now());
        when(credenciais.buscarPorEmail(email)).thenReturn(Optional.of(credencial));
        when(credenciais.salvar(any())).thenAnswer(i -> i.getArgument(0));

        new SolicitarResetSenhaUseCase(credenciais, emailGateway)
                .executar(new SolicitarResetSenhaCommand("user@x.com"));

        verify(credenciais).salvar(argThat(c -> c.tokenResetSenha() != null
                && c.tokenResetSenha().length() == 32
                && c.expiraResetEm() != null
                && c.expiraResetEm().isAfter(Instant.now())));
        verify(emailGateway).enviar(argThat(msg ->
                msg.destinatario().equals(email)
                        && msg.assunto().toLowerCase().contains("redefinição")
                        && msg.conteudo().contains("token")));
    }

    @Test
    void emailCadastradoInativo_naoEnviaEmail() {
        var credenciais = mock(CredencialRepository.class);
        var emailGateway = mock(EmailGateway.class);
        var email = new Email("user@x.com");
        var credencial = new Credencial(new Identificador(UUID.randomUUID()), email, "hash",
                new Identificador(UUID.randomUUID()), PapelUsuario.CAIXA, false, null, null,
                Instant.now(), Instant.now());
        when(credenciais.buscarPorEmail(email)).thenReturn(Optional.of(credencial));

        new SolicitarResetSenhaUseCase(credenciais, emailGateway)
                .executar(new SolicitarResetSenhaCommand("user@x.com"));

        verify(credenciais, never()).salvar(any());
        verify(emailGateway, never()).enviar(any());
    }
}
