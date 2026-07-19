package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.CadastrarClienteUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.ClienteResponse;
import com.supermarkettracker.infrastructure.web.request.ClienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final CadastrarClienteUseCase cadastrarCliente;
    public ClienteController(CadastrarClienteUseCase cadastrarCliente) { this.cadastrarCliente = cadastrarCliente; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) { return ApiResponse.of(ClienteResponse.from(cadastrarCliente.executar(request.toCommand()))); }
}
