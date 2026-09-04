package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.PagamentoCartao;
import com.supermarkettracker.domain.model.PagamentoPix;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PagamentoRepository {
    Pagamento salvar(Pagamento pagamento);
    Pagamento salvarPix(Pagamento pagamento, PagamentoPix pix);
    Pagamento salvarCartao(Pagamento pagamento, PagamentoCartao cartao);
    Optional<Pagamento> buscarPorId(Identificador id);
    List<Pagamento> listarPorVenda(Identificador vendaId);
    void atualizarStatus(Identificador pagamentoId, StatusPagamento status, Instant recebidoEm);
    Optional<PagamentoPix> buscarPixPorGatewayTransacaoId(String gatewayTransacaoId);
    Optional<PagamentoPix> buscarPixPorVendaId(Identificador vendaId);
}
