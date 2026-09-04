package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.RegistrarPagamentoCommand;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PagamentoRequest(UUID contaBancariaId, @NotNull TipoPagamento tipo,
                               @NotNull @DecimalMin(value = "0.01") BigDecimal valor,
                               ModalidadeCartao modalidadeCartao, Short parcelas, String referencia,
                               String tokenCartao) {
    public RegistrarPagamentoCommand toCommand(UUID vendaId) { return new RegistrarPagamentoCommand(vendaId, contaBancariaId, tipo, valor, modalidadeCartao, parcelas == null ? 0 : parcelas, referencia); }
}
