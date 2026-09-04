package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.application.query.BuscarProdutoQuery;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.application.query.ListarProdutosQuery;
import com.supermarkettracker.application.usecase.BuscarProdutoUseCase;
import com.supermarkettracker.application.usecase.CadastrarProdutoUseCase;
import com.supermarkettracker.application.usecase.ExcluirProdutoUseCase;
import com.supermarkettracker.application.usecase.ListarProdutosUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.ProdutoRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Produtos")
@RestController @RequestMapping("/api/v1/produtos")
public class ProdutoController {
    private final CadastrarProdutoUseCase cadastrarProduto; private final BuscarProdutoUseCase buscarProduto;
    private final ListarProdutosUseCase listarProdutos; private final ExcluirProdutoUseCase excluirProduto;
    public ProdutoController(CadastrarProdutoUseCase cadastrarProduto, BuscarProdutoUseCase buscarProduto,
            ListarProdutosUseCase listarProdutos, ExcluirProdutoUseCase excluirProduto) { this.cadastrarProduto = cadastrarProduto; this.buscarProduto = buscarProduto; this.listarProdutos = listarProdutos; this.excluirProduto = excluirProduto; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<ProdutoDto> criar(@Valid @RequestBody ProdutoRequest request) { return ApiResponse.of(cadastrarProduto.executar(request.toCommand())); }
    @GetMapping public ApiResponse<List<ProdutoDto>> listar(@RequestParam UUID empresaId) { return ApiResponse.of(listarProdutos.executar(new ListarProdutosQuery(empresaId))); }
    @GetMapping("/{id}") public ApiResponse<ProdutoDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarProduto.executar(new BuscarProdutoQuery(id))); }
    @PutMapping("/{id}")
    public ApiResponse<ProdutoDto> atualizar(@PathVariable UUID id, @Valid @RequestBody ProdutoRequest request) {
        return ApiResponse.of(cadastrarProduto.executarAtualizacao(
            new com.supermarkettracker.domain.model.valueobject.Identificador(id), request.toCommand()));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id) { excluirProduto.executar(new ExcluirEntidadeQuery(id)); return ResponseEntity.noContent().build(); }
}
