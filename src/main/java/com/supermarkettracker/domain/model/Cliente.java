package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;
import java.time.LocalDate;

public record Cliente(Identificador id, Identificador empresaId, String nome, Documento cpfCnpj, Email email,
                      String telefone, LocalDate dataNascimento, StatusAtivo status,
                      Instant criadoEm, Instant atualizadoEm) { }
