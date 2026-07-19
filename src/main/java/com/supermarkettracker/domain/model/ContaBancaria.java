package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.enums.TipoContaBancaria;
import com.supermarkettracker.domain.model.valueobject.Documento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record ContaBancaria(Identificador id, Identificador empresaId, String bancoCodigo, String bancoNome,
                            String agencia, String conta, TipoContaBancaria tipo, String titularNome,
                            Documento titularDocumento, String chavePix, boolean principal, StatusAtivo status,
                            Instant criadoEm, Instant atualizadoEm) { }
