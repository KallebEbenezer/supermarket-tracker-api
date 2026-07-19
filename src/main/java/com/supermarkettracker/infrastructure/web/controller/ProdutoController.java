package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.application.query.BuscarProdutoQuery;
import com.supermarkettracker.application.usecase.BuscarProdutoUseCase;
import com.supermarkettracker.application.usecase.CadastrarProdutoUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.ProdutoRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Produtos")
@RestController @RequestMapping("/api/v1/produtos")
public class ProdutoController {
    private final CadastrarProdutoUseCase cadastrarProduto; private final BuscarProdutoUseCase buscarProduto;
    public ProdutoController(CadastrarProdutoUseCase cadastrarProduto, BuscarProdutoUseCase buscarProduto) { this.cadastrarProduto = cadastrarProduto; this.buscarProduto = buscarProduto; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<ProdutoDto> criar(@Valid @RequestBody ProdutoRequest request) { return ApiResponse.of(cadastrarProduto.executar(request.toCommand())); }
    @GetMapping("/{id}") public ApiResponse<ProdutoDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarProduto.executar(new BuscarProdutoQuery(id))); }
}
