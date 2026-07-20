package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.RedefinirSenhaCommand;
import com.supermarkettracker.domain.exception.TokenRedefinicaoInvalidoException;
import com.supermarkettracker.domain.repository.CredencialRepository;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Redefine a senha a partir de um token de recuperação válido e não expirado. */
public final class RedefinirSenhaUseCase {
    private static final int SENHA_MINIMA = 6;

    private final CredencialRepository credenciais;
    private final PasswordEncoder passwordEncoder;

    public RedefinirSenhaUseCase(CredencialRepository credenciais, PasswordEncoder passwordEncoder) {
        this.credenciais = credenciais;
        this.passwordEncoder = passwordEncoder;
    }

    public void executar(RedefinirSenhaCommand c) {
        if (c.novaSenha() == null || c.novaSenha().length() < SENHA_MINIMA) {
            throw new IllegalArgumentException("A nova senha deve ter ao menos " + SENHA_MINIMA + " caracteres");
        }
        var credencial = credenciais.buscarPorTokenReset(c.token())
                .orElseThrow(() -> new TokenRedefinicaoInvalidoException("Token de redefinicao invalido"));
        if (credencial.expiraResetEm() == null || credencial.expiraResetEm().isBefore(Instant.now())) {
            throw new TokenRedefinicaoInvalidoException("Token de redefinicao expirado");
        }
        String senhaHash = passwordEncoder.encode(c.novaSenha());
        credenciais.salvar(credencial.comSenha(senhaHash));
    }
}
