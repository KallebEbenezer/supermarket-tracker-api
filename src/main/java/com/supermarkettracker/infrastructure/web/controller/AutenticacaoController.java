package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.service.TokenService;
import com.supermarkettracker.application.usecase.AutenticacaoResult;
import com.supermarkettracker.application.usecase.AutenticarUsuarioUseCase;
import com.supermarkettracker.application.usecase.BuscarUsuarioAtualUseCase;
import com.supermarkettracker.application.usecase.RedefinirSenhaUseCase;
import com.supermarkettracker.application.usecase.RegistrarUsuarioUseCase;
import com.supermarkettracker.application.usecase.RenovarTokenUseCase;
import com.supermarkettracker.application.usecase.SolicitarResetSenhaUseCase;
import com.supermarkettracker.application.usecase.UsuarioAtual;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.AuthResponse;
import com.supermarkettracker.infrastructure.web.dto.RecuperarSenhaResponse;
import com.supermarkettracker.infrastructure.web.dto.RefreshResponse;
import com.supermarkettracker.infrastructure.web.dto.UsuarioAuthResponse;
import com.supermarkettracker.infrastructure.web.request.LoginRequest;
import com.supermarkettracker.infrastructure.web.request.RecuperarSenhaRequest;
import com.supermarkettracker.infrastructure.web.request.RedefinirSenhaRequest;
import com.supermarkettracker.infrastructure.web.request.RefreshRequest;
import com.supermarkettracker.infrastructure.web.request.RegistroRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticacao")
@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    private final RegistrarUsuarioUseCase registrar;
    private final AutenticarUsuarioUseCase autenticar;
    private final RenovarTokenUseCase renovar;
    private final BuscarUsuarioAtualUseCase buscarAtual;
    private final SolicitarResetSenhaUseCase solicitarReset;
    private final RedefinirSenhaUseCase redefinir;
    private final TokenService tokenService;

    public AutenticacaoController(RegistrarUsuarioUseCase registrar, AutenticarUsuarioUseCase autenticar,
            RenovarTokenUseCase renovar, BuscarUsuarioAtualUseCase buscarAtual,
            SolicitarResetSenhaUseCase solicitarReset, RedefinirSenhaUseCase redefinir, TokenService tokenService) {
        this.registrar = registrar;
        this.autenticar = autenticar;
        this.renovar = renovar;
        this.buscarAtual = buscarAtual;
        this.solicitarReset = solicitarReset;
        this.redefinir = redefinir;
        this.tokenService = tokenService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        AutenticacaoResult result = registrar.executar(request.toCommand());
        return ApiResponse.of(toResponse(result));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AutenticacaoResult result = autenticar.executar(request.toCommand());
        return ApiResponse.of(toResponse(result));
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String accessToken = renovar.executar(request.toCommand());
        return ApiResponse.of(new RefreshResponse(accessToken, tokenService.accessTtlSegundos()));
    }

    @GetMapping("/me")
    public ApiResponse<UsuarioAuthResponse> me(Authentication authentication) {
        TokenService.TokenPayload payload = (TokenService.TokenPayload) authentication.getPrincipal();
        UsuarioAtual atual = buscarAtual.executar(payload.usuarioId());
        return ApiResponse.of(UsuarioAuthResponse.from(atual.usuario(), atual.papel()));
    }

    @PostMapping("/recuperar-senha")
    public ApiResponse<RecuperarSenhaResponse> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequest request) {
        String token = solicitarReset.executar(request.toCommand());
        return ApiResponse.of(new RecuperarSenhaResponse(token));
    }

    @PostMapping("/redefinir-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        redefinir.executar(request.toCommand());
    }

    private AuthResponse toResponse(AutenticacaoResult result) {
        return new AuthResponse(result.accessToken(), result.refreshToken(), result.expiresInSegundos(),
                UsuarioAuthResponse.from(result.usuario(), result.papel()));
    }
}
