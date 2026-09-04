package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.ContaBancariaDto;
import com.supermarkettracker.application.query.BuscarContaBancariaQuery;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.application.query.ListarContasBancariasQuery;
import com.supermarkettracker.application.usecase.BuscarContaBancariaUseCase;
import com.supermarkettracker.application.usecase.CadastrarContaBancariaUseCase;
import com.supermarkettracker.application.usecase.ExcluirContaBancariaUseCase;
import com.supermarkettracker.application.usecase.ListarContasBancariasUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.ContaBancariaRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Contas Bancarias")
@RestController @RequestMapping("/api/v1/contas-bancarias")
public class ContaBancariaController {
    private final CadastrarContaBancariaUseCase cadastrarConta;
    private final ListarContasBancariasUseCase listarContas;
    private final BuscarContaBancariaUseCase buscarConta;
    private final ExcluirContaBancariaUseCase excluirConta;

    public ContaBancariaController(CadastrarContaBancariaUseCase cadastrarConta,
            ListarContasBancariasUseCase listarContas,
            BuscarContaBancariaUseCase buscarConta,
            ExcluirContaBancariaUseCase excluirConta) {
        this.cadastrarConta = cadastrarConta;
        this.listarContas = listarContas;
        this.buscarConta = buscarConta;
        this.excluirConta = excluirConta;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContaBancariaDto> criar(@Valid @RequestBody ContaBancariaRequest request) {
        return ApiResponse.of(cadastrarConta.executar(request.toCommand()));
    }

    @GetMapping
    public ApiResponse<List<ContaBancariaDto>> listar(@RequestParam UUID empresaId) {
        return ApiResponse.of(listarContas.executar(new ListarContasBancariasQuery(empresaId)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ContaBancariaDto> buscar(@PathVariable UUID id) {
        return ApiResponse.of(buscarConta.executar(new BuscarContaBancariaQuery(id)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ContaBancariaDto> atualizar(@PathVariable UUID id, @Valid @RequestBody ContaBancariaRequest request) {
        return ApiResponse.of(cadastrarConta.executarAtualizacao(
            new com.supermarkettracker.domain.model.valueobject.Identificador(id), request.toCommand()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirConta.executar(new ExcluirEntidadeQuery(id));
        return ResponseEntity.noContent().build();
    }
}
