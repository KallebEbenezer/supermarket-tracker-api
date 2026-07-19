package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.PapelUsuario;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record EmpresaUsuario(Identificador id, Identificador empresaId, Identificador usuarioId,
                             PapelUsuario papel, StatusAtivo status, Instant criadoEm, Instant atualizadoEm) { }
