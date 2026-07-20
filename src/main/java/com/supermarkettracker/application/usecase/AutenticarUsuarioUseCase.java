package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.AutenticarUsuarioCommand;
import com.supermarkettracker.domain.exception.CredenciaisInvalidasException;
import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CredencialRepository;
import com.supermarkettracker.domain.repository.UsuarioRepository;
import com.supermarkettracker.application.service.TokenService;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Autentica um usuário por e-mail/senha e emite tokens de acesso. */
public final class AutenticarUsuarioUseCase {
    private final UsuarioRepository usuarios;
    private final CredencialRepository credenciais;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AutenticarUsuarioUseCase(UsuarioRepository usuarios, CredencialRepository credenciais,
            PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarios = usuarios;
        this.credenciais = credenciais;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AutenticacaoResult executar(AutenticarUsuarioCommand c) {
        Credencial credencial = credenciais.buscarPorEmail(new Email(c.email()))
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais invalidas"));
        if (!credencial.ativo() || !passwordEncoder.matches(c.senha(), credencial.senhaHash())) {
            throw new CredenciaisInvalidasException("Credenciais invalidas");
        }
        Usuario usuario = usuarios.buscarPorId(credencial.usuarioId())
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais invalidas"));

        UUID usuarioId = credencial.usuarioId().valor();
        String accessToken = tokenService.gerarAccessToken(usuarioId, credencial.email().valor(), credencial.papel().name());
        String refreshToken = tokenService.gerarRefreshToken(usuarioId, credencial.email().valor(), credencial.papel().name());
        return new AutenticacaoResult(usuario, credencial.papel(), accessToken, refreshToken,
                tokenService.accessTtlSegundos());
    }
}
