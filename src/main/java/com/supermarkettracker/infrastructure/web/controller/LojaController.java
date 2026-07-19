package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.LojaDto;
import com.supermarkettracker.application.query.ListarLojasQuery;
import com.supermarkettracker.application.usecase.CadastrarLojaUseCase;
import com.supermarkettracker.application.usecase.ListarLojasUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.LojaRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/lojas")
public class LojaController {
    private final CadastrarLojaUseCase cadastrarLoja; private final ListarLojasUseCase listarLojas;
    public LojaController(CadastrarLojaUseCase cadastrarLoja, ListarLojasUseCase listarLojas) { this.cadastrarLoja = cadastrarLoja; this.listarLojas = listarLojas; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<LojaDto> criar(@Valid @RequestBody LojaRequest request) { return ApiResponse.of(cadastrarLoja.executar(request.toCommand())); }
    @GetMapping public ApiResponse<List<LojaDto>> listar(@RequestParam UUID empresaId) { return ApiResponse.of(listarLojas.executar(new ListarLojasQuery(empresaId))); }
}
