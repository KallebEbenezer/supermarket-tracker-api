package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.command.RegistrarUsuarioCommand;
import com.supermarkettracker.domain.exception.ConflitoDeDominioException;
import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.StatusAtivo;
import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CredencialRepository;
import com.supermarkettracker.domain.repository.UsuarioRepository;
import com.supermarkettracker.infrastructure.config.JwtProperties;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Registra um novo usuário (perfil + credencial) e emite tokens de acesso. */
public final class RegistrarUsuarioUseCase {
    private static final int SENHA_MINIMA = 6;

    private final UsuarioRepository usuarios;
    private final CredencialRepository credenciais;
    private final PasswordEncoder passwordEncoder;
    private final com.supermarkettracker.application.service.TokenService tokenService;

    public RegistrarUsuarioUseCase(UsuarioRepository usuarios, CredencialRepository credenciais,
            PasswordEncoder passwordEncoder, com.supermarkettracker.application.service.TokenService tokenService) {
        this.usuarios = usuarios;
        this.credenciais = credenciais;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AutenticacaoResult executar(RegistrarUsuarioCommand c) {
        if (c.nome() == null || c.nome().isBlank()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        if (c.senha() == null || c.senha().length() < SENHA_MINIMA) {
            throw new IllegalArgumentException("A senha deve ter ao menos " + SENHA_MINIMA + " caracteres");
        }
        Email email = new Email(c.email());
        if (credenciais.buscarPorEmail(email).isPresent()) {
            throw new ConflitoDeDominioException("E-mail ja cadastrado");
        }

        Instant agora = Instant.now();
        Identificador usuarioId = Identificador.novo();
        Usuario usuario = usuarios.salvar(new Usuario(usuarioId, usuarioId, c.nome().trim(), email, c.telefone(),
                StatusAtivo.ATIVO, agora, agora));

        String senhaHash = passwordEncoder.encode(c.senha());
        credenciais.salvar(new Credencial(Identificador.novo(), email, senhaHash, usuarioId, c.papel(), true, null,
                null, agora, agora));

        String accessToken = tokenService.gerarAccessToken(usuarioId.valor(), email.valor(), c.papel().name());
        String refreshToken = tokenService.gerarRefreshToken(usuarioId.valor(), email.valor(), c.papel().name());
        return new AutenticacaoResult(usuario, c.papel(), accessToken, refreshToken, tokenService.accessTtlSegundos());
    }
}
