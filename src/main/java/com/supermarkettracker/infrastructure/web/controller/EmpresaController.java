package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.EmpresaDto;
import com.supermarkettracker.application.query.ListarEmpresasQuery;
import com.supermarkettracker.application.usecase.CadastrarEmpresaUseCase;
import com.supermarkettracker.application.usecase.ListarEmpresasUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.EmpresaRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Empresas")
@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {
    private final CadastrarEmpresaUseCase cadastrarEmpresa;
    private final ListarEmpresasUseCase listarEmpresas;
    public EmpresaController(CadastrarEmpresaUseCase cadastrarEmpresa, ListarEmpresasUseCase listarEmpresas) { this.cadastrarEmpresa = cadastrarEmpresa; this.listarEmpresas = listarEmpresas; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmpresaDto> criar(@Valid @RequestBody EmpresaRequest request) { return ApiResponse.of(cadastrarEmpresa.executar(request.toCommand())); }
    @GetMapping
    public ApiResponse<List<EmpresaDto>> listar(@RequestParam UUID usuarioId) { return ApiResponse.of(listarEmpresas.executar(new ListarEmpresasQuery(usuarioId))); }
}
