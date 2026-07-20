package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.PapelUsuario;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

/**
 * Credencial de acesso local do usuário (e-mail + senha criptografada via BCrypt).
 *
 * <p>É a fonte de verdade para autenticação (login, renovação e redefinição de
 * senha). Liga-se a um {@link Usuario} pelo {@code usuarioId}.
 */
public record Credencial(Identificador id, Email email, String senhaHash, Identificador usuarioId,
                         PapelUsuario papel, boolean ativo, String tokenResetSenha, Instant expiraResetEm,
                         Instant criadoEm, Instant atualizadoEm) {

    public Credencial comSenha(String novaSenhaHash) {
        return new Credencial(id, email, novaSenhaHash, usuarioId, papel, ativo, null, null, criadoEm, Instant.now());
    }

    public Credencial comTokenReset(String token, Instant expiraEm) {
        return new Credencial(id, email, senhaHash, usuarioId, papel, ativo, token, expiraEm, criadoEm, Instant.now());
    }
}
