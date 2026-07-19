package com.supermarkettracker.domain.model;

import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Identificador;

public record PagamentoCartao(Identificador pagamentoId, ModalidadeCartao modalidade, short parcelas,
                              String bandeira, String nsu, String codigoAutorizacao, String terminalId,
                              String gateway, String gatewayTransacaoId, String statusGateway) { }
