package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.*;
import com.supermarkettracker.application.query.BuscarVendaQuery;
import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.infrastructure.web.dto.*;
import com.supermarkettracker.infrastructure.web.request.*;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/vendas")
public class VendaController {
    private final IniciarVendaUseCase iniciarVenda; private final BuscarVendaUseCase buscarVenda; private final AdicionarItemVendaUseCase adicionarItem; private final RegistrarPagamentoUseCase registrarPagamento;
    public VendaController(IniciarVendaUseCase iniciarVenda, BuscarVendaUseCase buscarVenda, AdicionarItemVendaUseCase adicionarItem, RegistrarPagamentoUseCase registrarPagamento) { this.iniciarVenda = iniciarVenda; this.buscarVenda = buscarVenda; this.adicionarItem = adicionarItem; this.registrarPagamento = registrarPagamento; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<VendaDto> criar(@Valid @RequestBody VendaRequest request) { return ApiResponse.of(iniciarVenda.executar(request.toCommand())); }
    @GetMapping("/{id}") public ApiResponse<VendaDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarVenda.executar(new BuscarVendaQuery(id))); }
    @PostMapping("/{vendaId}/itens") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<ItemVendaResponse> adicionarItem(@PathVariable UUID vendaId, @Valid @RequestBody ItemVendaRequest request) { return ApiResponse.of(ItemVendaResponse.from(adicionarItem.executar(request.toCommand(vendaId)))); }
    @PostMapping("/{vendaId}/pagamentos") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<PagamentoDto> registrarPagamento(@PathVariable UUID vendaId, @Valid @RequestBody PagamentoRequest request) { return ApiResponse.of(registrarPagamento.executar(request.toCommand(vendaId))); }
}
