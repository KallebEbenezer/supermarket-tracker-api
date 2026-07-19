package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.RegistrarMovimentacaoEstoqueUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.MovimentacaoEstoqueResponse;
import com.supermarkettracker.infrastructure.web.request.MovimentacaoEstoqueRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/estoque")
public class EstoqueController {
    private final RegistrarMovimentacaoEstoqueUseCase registrarMovimentacao;
    public EstoqueController(RegistrarMovimentacaoEstoqueUseCase registrarMovimentacao) { this.registrarMovimentacao = registrarMovimentacao; }
    @PostMapping("/movimentacoes") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<MovimentacaoEstoqueResponse> registrar(@Valid @RequestBody MovimentacaoEstoqueRequest request) { return ApiResponse.of(MovimentacaoEstoqueResponse.from(registrarMovimentacao.executar(request.toCommand()))); }
}
