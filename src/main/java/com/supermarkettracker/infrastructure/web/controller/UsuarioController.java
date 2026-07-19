package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.CadastrarUsuarioUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.UsuarioResponse;
import com.supermarkettracker.infrastructure.web.request.UsuarioRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes e usuários")
@RestController @RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final CadastrarUsuarioUseCase cadastrarUsuario;
    public UsuarioController(CadastrarUsuarioUseCase cadastrarUsuario) { this.cadastrarUsuario = cadastrarUsuario; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) { return ApiResponse.of(UsuarioResponse.from(cadastrarUsuario.executar(request.toCommand()))); }
}
