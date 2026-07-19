package com.supermarkettracker.infrastructure.web.dto;

import com.supermarkettracker.domain.model.Cliente;
import java.time.LocalDate;
import java.util.UUID;

public record ClienteResponse(UUID id, UUID empresaId, String nome, String cpfCnpj, String email, String telefone,
                              LocalDate dataNascimento, String status) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(cliente.id().valor(), cliente.empresaId().valor(), cliente.nome(),
                cliente.cpfCnpj() == null ? null : cliente.cpfCnpj().valor(),
                cliente.email() == null ? null : cliente.email().valor(), cliente.telefone(), cliente.dataNascimento(),
                cliente.status().name());
    }
}
