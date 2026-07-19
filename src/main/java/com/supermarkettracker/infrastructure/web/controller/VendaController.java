package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.*;
import com.supermarkettracker.application.query.BuscarVendaQuery;
import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.infrastructure.web.dto.*;
import com.supermarkettracker.infrastructure.web.request.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vendas")
@RestController @RequestMapping("/api/v1/vendas")
public class VendaController {
    private final BuscarVendaUseCase buscarVenda; private final FinalizarVendaUseCase finalizarVenda;
    public VendaController(BuscarVendaUseCase buscarVenda, FinalizarVendaUseCase finalizarVenda) { this.buscarVenda = buscarVenda; this.finalizarVenda = finalizarVenda; }
    @PostMapping("/finalizar") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<VendaDto> finalizar(@Valid @RequestBody FinalizarVendaRequest request) { return ApiResponse.of(finalizarVenda.executar(request.toCommand())); }
    @GetMapping("/{id}") public ApiResponse<VendaDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarVenda.executar(new BuscarVendaQuery(id))); }
}
