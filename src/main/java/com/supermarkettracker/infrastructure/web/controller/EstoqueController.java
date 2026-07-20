package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.query.ListarMovimentacoesEstoqueQuery;
import com.supermarkettracker.application.usecase.ListarMovimentacoesEstoqueUseCase;
import com.supermarkettracker.application.usecase.RegistrarMovimentacaoEstoqueUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.MovimentacaoEstoqueResponse;
import com.supermarkettracker.infrastructure.web.request.MovimentacaoEstoqueRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Estoque")
@RestController @RequestMapping("/api/v1/estoque")
public class EstoqueController {
    private final RegistrarMovimentacaoEstoqueUseCase registrarMovimentacao;
    private final ListarMovimentacoesEstoqueUseCase listarMovimentacoes;
    public EstoqueController(RegistrarMovimentacaoEstoqueUseCase registrarMovimentacao,
            ListarMovimentacoesEstoqueUseCase listarMovimentacoes) { this.registrarMovimentacao = registrarMovimentacao; this.listarMovimentacoes = listarMovimentacoes; }
    @PostMapping("/movimentacoes") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<MovimentacaoEstoqueResponse> registrar(@Valid @RequestBody MovimentacaoEstoqueRequest request) { return ApiResponse.of(MovimentacaoEstoqueResponse.from(registrarMovimentacao.executar(request.toCommand()))); }
    @GetMapping("/movimentacoes") public ApiResponse<List<MovimentacaoEstoqueResponse>> listar(@RequestParam UUID empresaId, @RequestParam(required = false) UUID lojaId) { return ApiResponse.of(listarMovimentacoes.executar(new ListarMovimentacoesEstoqueQuery(empresaId, lojaId))); }
}
