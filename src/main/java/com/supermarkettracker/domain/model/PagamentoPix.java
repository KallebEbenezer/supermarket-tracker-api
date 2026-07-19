package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;

public record PagamentoPix(Identificador pagamentoId, String qrCode, String copiaCola, String txid,
                           String gateway, String gatewayTransacaoId, String statusGateway, Instant pagoEm) { }
