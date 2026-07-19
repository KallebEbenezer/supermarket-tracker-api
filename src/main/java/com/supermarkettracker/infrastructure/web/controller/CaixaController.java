package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.infrastructure.web.dto.*;
import com.supermarkettracker.infrastructure.web.request.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Caixa")
@RestController @RequestMapping("/api/v1/caixas")
public class CaixaController {
    private final CadastrarCaixaUseCase cadastrarCaixa; private final AbrirSessaoCaixaUseCase abrirSessao; private final FecharSessaoCaixaUseCase fecharSessao;
    public CaixaController(CadastrarCaixaUseCase cadastrarCaixa, AbrirSessaoCaixaUseCase abrirSessao, FecharSessaoCaixaUseCase fecharSessao) { this.cadastrarCaixa = cadastrarCaixa; this.abrirSessao = abrirSessao; this.fecharSessao = fecharSessao; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<CaixaResponse> criar(@Valid @RequestBody CaixaRequest request) { return ApiResponse.of(CaixaResponse.from(cadastrarCaixa.executar(request.toCommand()))); }
    @PostMapping("/{caixaId}/sessoes") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<SessaoCaixaResponse> abrir(@PathVariable UUID caixaId, @Valid @RequestBody AbrirSessaoCaixaRequest request) { return ApiResponse.of(SessaoCaixaResponse.from(abrirSessao.executar(request.toCommand(caixaId)))); }
    @PatchMapping("/{caixaId}/sessoes/atual") public ApiResponse<SessaoCaixaResponse> fechar(@PathVariable UUID caixaId, @Valid @RequestBody FecharSessaoCaixaRequest request) { return ApiResponse.of(SessaoCaixaResponse.from(fecharSessao.executar(request.toCommand(caixaId)))); }
}
