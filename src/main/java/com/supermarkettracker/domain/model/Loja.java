package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Endereco;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record Loja(Identificador id, Identificador empresaId, String codigo, String nome, Documento cnpj,
                   Endereco endereco, String telefone, StatusAtivo status, Instant criadoEm, Instant atualizadoEm) { }
