package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.LojaDto;
import com.supermarkettracker.application.dto.PaginatedResult;
import com.supermarkettracker.application.query.BuscarLojaQuery;
import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.application.query.ListarLojasQuery;
import com.supermarkettracker.application.usecase.BuscarLojaUseCase;
import com.supermarkettracker.application.usecase.CadastrarLojaUseCase;
import com.supermarkettracker.application.usecase.ExcluirLojaUseCase;
import com.supermarkettracker.application.usecase.ListarLojasUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.request.LojaRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lojas")
@RestController @RequestMapping("/api/v1/lojas")
public class LojaController {
    private final CadastrarLojaUseCase cadastrarLoja; private final ListarLojasUseCase listarLojas; private final BuscarLojaUseCase buscarLoja; private final ExcluirLojaUseCase excluirLoja;
    public LojaController(CadastrarLojaUseCase cadastrarLoja, ListarLojasUseCase listarLojas, BuscarLojaUseCase buscarLoja, ExcluirLojaUseCase excluirLoja) { this.cadastrarLoja = cadastrarLoja; this.listarLojas = listarLojas; this.buscarLoja = buscarLoja; this.excluirLoja = excluirLoja; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<LojaDto> criar(@Valid @RequestBody LojaRequest request) { return ApiResponse.of(cadastrarLoja.executar(request.toCommand())); }
    @GetMapping public ApiResponse<PaginatedResult<LojaDto>> listar(
            @RequestParam UUID empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<LojaDto> all = listarLojas.executar(new ListarLojasQuery(empresaId));
        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<LojaDto> paged = start < all.size() ? all.subList(start, end) : List.of();
        return ApiResponse.of(PaginatedResult.of(paged, page, size, all.size()));
    }
    @GetMapping("/{id}") public ApiResponse<LojaDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarLoja.executar(new BuscarLojaQuery(id))); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id) { excluirLoja.executar(new ExcluirEntidadeQuery(id)); return ResponseEntity.noContent().build(); }
}
