package com.supermarkettracker.application.command;

import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import java.math.BigDecimal;
import java.util.UUID;

public record PagamentoCheckoutCommand(UUID contaBancariaId, TipoPagamento tipo, BigDecimal valor,
                                       ModalidadeCartao modalidadeCartao, short parcelas, String referencia) { }
