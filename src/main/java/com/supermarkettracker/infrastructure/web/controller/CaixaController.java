package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.CaixaDto;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.application.query.ListarCaixasQuery;
import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.infrastructure.web.dto.*;
import com.supermarkettracker.infrastructure.web.request.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Caixa")
@RestController @RequestMapping("/api/v1/caixas")
public class CaixaController {
    private final CadastrarCaixaUseCase cadastrarCaixa; private final AbrirSessaoCaixaUseCase abrirSessao;
    private final FecharSessaoCaixaUseCase fecharSessao; private final ExcluirCaixaUseCase excluirCaixa;
    private final ListarCaixasUseCase listarCaixas;
    private final BuscarSessaoAtualUseCase buscarSessaoAtual;

    public CaixaController(CadastrarCaixaUseCase cadastrarCaixa, AbrirSessaoCaixaUseCase abrirSessao,
            FecharSessaoCaixaUseCase fecharSessao, ExcluirCaixaUseCase excluirCaixa,
            ListarCaixasUseCase listarCaixas, BuscarSessaoAtualUseCase buscarSessaoAtual) {
        this.cadastrarCaixa = cadastrarCaixa;
        this.abrirSessao = abrirSessao;
        this.fecharSessao = fecharSessao;
        this.excluirCaixa = excluirCaixa;
        this.listarCaixas = listarCaixas;
        this.buscarSessaoAtual = buscarSessaoAtual;
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<CaixaResponse> criar(@Valid @RequestBody CaixaRequest request) { return ApiResponse.of(CaixaResponse.from(cadastrarCaixa.executar(request.toCommand()))); }
    @GetMapping public ApiResponse<List<CaixaDto>> listar(@RequestParam(required = false) UUID lojaId, @RequestParam(required = false) UUID empresaId) { return ApiResponse.of(listarCaixas.executar(new ListarCaixasQuery(lojaId, empresaId))); }
    @PostMapping("/{caixaId}/sessoes") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<SessaoCaixaResponse> abrir(@PathVariable UUID caixaId, @Valid @RequestBody AbrirSessaoCaixaRequest request) { return ApiResponse.of(SessaoCaixaResponse.from(abrirSessao.executar(request.toCommand(caixaId)))); }
    @GetMapping("/{caixaId}/sessoes/atual") public ApiResponse<SessaoCaixaResponse> buscarSessaoAtual(@PathVariable UUID caixaId) { return ApiResponse.of(SessaoCaixaResponse.from(buscarSessaoAtual.executar(caixaId))); }
    @PatchMapping("/{caixaId}/sessoes/atual") public ApiResponse<SessaoCaixaResponse> fechar(@PathVariable UUID caixaId, @Valid @RequestBody FecharSessaoCaixaRequest request) { return ApiResponse.of(SessaoCaixaResponse.from(fecharSessao.executar(request.toCommand(caixaId)))); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id) { excluirCaixa.executar(new ExcluirEntidadeQuery(id)); return ResponseEntity.noContent().build(); }
}
