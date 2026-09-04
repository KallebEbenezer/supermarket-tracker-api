package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.application.query.ListarClientesQuery;
import com.supermarkettracker.application.usecase.CadastrarClienteUseCase;
import com.supermarkettracker.application.usecase.ExcluirClienteUseCase;
import com.supermarkettracker.application.usecase.ListarClientesUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.ClienteResponse;
import com.supermarkettracker.infrastructure.web.request.ClienteRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes e usuários")
@RestController @RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final CadastrarClienteUseCase cadastrarCliente;
    private final ListarClientesUseCase listarClientes;
    private final ExcluirClienteUseCase excluirCliente;
    public ClienteController(CadastrarClienteUseCase cadastrarCliente, ListarClientesUseCase listarClientes, ExcluirClienteUseCase excluirCliente) { this.cadastrarCliente = cadastrarCliente; this.listarClientes = listarClientes; this.excluirCliente = excluirCliente; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) { return ApiResponse.of(ClienteResponse.from(cadastrarCliente.executar(request.toCommand()))); }
    @GetMapping public ApiResponse<List<ClienteResponse>> listar(@RequestParam UUID empresaId) { return ApiResponse.of(listarClientes.executar(new ListarClientesQuery(empresaId))); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id) { excluirCliente.executar(new ExcluirEntidadeQuery(id)); return ResponseEntity.noContent().build(); }
}
