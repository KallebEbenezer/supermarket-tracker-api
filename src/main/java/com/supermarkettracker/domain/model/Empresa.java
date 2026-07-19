package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusEmpresa;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record Empresa(Identificador id, String razaoSocial, String nomeFantasia, Documento cnpj,
                      StatusEmpresa status, Instant criadoEm, Instant atualizadoEm) { }
