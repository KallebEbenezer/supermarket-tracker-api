package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.*;
import com.supermarkettracker.application.query.BuscarVendaQuery;
import com.supermarkettracker.application.query.ListarVendasQuery;
import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.infrastructure.web.dto.*;
import com.supermarkettracker.infrastructure.web.request.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vendas")
@RestController @RequestMapping("/api/v1/vendas")
public class VendaController {
    private final BuscarVendaUseCase buscarVenda; private final FinalizarVendaUseCase finalizarVenda;
    private final ListarVendasUseCase listarVendas;
    public VendaController(BuscarVendaUseCase buscarVenda, FinalizarVendaUseCase finalizarVenda,
            ListarVendasUseCase listarVendas) { this.buscarVenda = buscarVenda; this.finalizarVenda = finalizarVenda; this.listarVendas = listarVendas; }
    @PostMapping("/finalizar") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<VendaDto> finalizar(@Valid @RequestBody FinalizarVendaRequest request) { return ApiResponse.of(finalizarVenda.executar(request.toCommand())); }
    @GetMapping public ApiResponse<List<VendaDto>> listar(@RequestParam UUID empresaId, @RequestParam(required = false) UUID lojaId, @RequestParam(defaultValue = "50") int limite) { return ApiResponse.of(listarVendas.executar(new ListarVendasQuery(empresaId, lojaId, limite))); }
    @GetMapping("/{id}") public ApiResponse<VendaDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarVenda.executar(new BuscarVendaQuery(id))); }
}
