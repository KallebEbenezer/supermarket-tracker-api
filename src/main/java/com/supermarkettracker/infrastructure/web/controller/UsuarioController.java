package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.query.ExcluirEntidadeQuery;
import com.supermarkettracker.application.query.ListarUsuariosQuery;
import com.supermarkettracker.application.usecase.CadastrarUsuarioUseCase;
import com.supermarkettracker.application.usecase.ExcluirUsuarioUseCase;
import com.supermarkettracker.application.usecase.ListarUsuariosUseCase;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import com.supermarkettracker.infrastructure.web.dto.UsuarioResponse;
import com.supermarkettracker.infrastructure.web.request.UsuarioRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes e usuários")
@RestController @RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final CadastrarUsuarioUseCase cadastrarUsuario;
    private final ListarUsuariosUseCase listarUsuarios;
    private final ExcluirUsuarioUseCase excluirUsuario;
    public UsuarioController(CadastrarUsuarioUseCase cadastrarUsuario, ListarUsuariosUseCase listarUsuarios, ExcluirUsuarioUseCase excluirUsuario) { this.cadastrarUsuario = cadastrarUsuario; this.listarUsuarios = listarUsuarios; this.excluirUsuario = excluirUsuario; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApiResponse<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) { return ApiResponse.of(UsuarioResponse.from(cadastrarUsuario.executar(request.toCommand()))); }
    @GetMapping public ApiResponse<List<UsuarioResponse>> listar(@RequestParam UUID empresaId) { return ApiResponse.of(listarUsuarios.executar(new ListarUsuariosQuery(empresaId))); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable UUID id) { excluirUsuario.executar(new ExcluirEntidadeQuery(id)); return ResponseEntity.noContent().build(); }
}
