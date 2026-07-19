package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.EmpresaDto;
import com.supermarkettracker.application.usecase.CadastrarEmpresaUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.EmpresaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {
    private final CadastrarEmpresaUseCase cadastrarEmpresa;
    public EmpresaController(CadastrarEmpresaUseCase cadastrarEmpresa) { this.cadastrarEmpresa = cadastrarEmpresa; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmpresaDto> criar(@Valid @RequestBody EmpresaRequest request) { return ApiResponse.of(cadastrarEmpresa.executar(request.toCommand())); }
}
