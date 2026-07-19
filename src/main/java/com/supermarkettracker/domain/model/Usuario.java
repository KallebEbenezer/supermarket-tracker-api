package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record Usuario(Identificador id, Identificador authUserId, String nome, Email email, String telefone,
                      StatusAtivo status, Instant criadoEm, Instant atualizadoEm) { }
