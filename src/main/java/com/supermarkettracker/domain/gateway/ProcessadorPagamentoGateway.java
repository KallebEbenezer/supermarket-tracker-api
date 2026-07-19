package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;

public interface ProcessadorPagamentoGateway {
    ResultadoPagamento processarPix(Identificador pagamentoId, Dinheiro valor);
    ResultadoPagamento processarCartao(Identificador pagamentoId, Dinheiro valor, ModalidadeCartao modalidade, short parcelas);
    record ResultadoPagamento(String referenciaExterna, String status, String codigoAutorizacao) { }
}
