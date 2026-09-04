package com.supermarkettracker.application.usecase;

import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.StatusVenda;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.DashboardRepository;
import com.supermarkettracker.domain.repository.MovimentacaoEstoqueRepository;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.domain.repository.VendaRepository;
import com.supermarkettracker.infrastructure.websocket.PaymentWebSocketPublisher;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PixNotificationService {
    private static final Logger log = LoggerFactory.getLogger(PixNotificationService.class);

    private final PagamentoRepository pagamentos;
    private final VendaRepository vendas;
    private final DashboardRepository dashboard;
    private final MovimentacaoEstoqueRepository movimentacoes;
    private final FinalizarVendaUseCase finalizacaoHelper;
    private final PaymentWebSocketPublisher eventPublisher;

    public PixNotificationService(PagamentoRepository pagamentos, VendaRepository vendas,
            DashboardRepository dashboard, MovimentacaoEstoqueRepository movimentacoes,
            FinalizarVendaUseCase finalizacaoHelper, PaymentWebSocketPublisher eventPublisher) {
        this.pagamentos = pagamentos;
        this.vendas = vendas;
        this.dashboard = dashboard;
        this.movimentacoes = movimentacoes;
        this.finalizacaoHelper = finalizacaoHelper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processarNotificacao(String gatewayTransacaoId, String novoStatus) {
        log.info("Processando notificação PIX: gatewayTransacaoId={}, status={}", gatewayTransacaoId, novoStatus);

        var pagamentoPix = pagamentos.buscarPixPorGatewayTransacaoId(gatewayTransacaoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Pagamento PIX nao encontrado para gatewayTransacaoId: " + gatewayTransacaoId));

        Optional<Pagamento> pagamentoOpt = pagamentos.buscarPorId(pagamentoPix.pagamentoId());
        if (pagamentoOpt.isEmpty()) {
            log.warn("Pagamento não encontrado: {}", pagamentoPix.pagamentoId());
            return;
        }

        Pagamento pagamento = pagamentoOpt.get();

        if (pagamento.status() == StatusPagamento.APROVADO) {
            log.info("Pagamento {} já está aprovado, ignorando notificação", pagamento.id());
            return;
        }

        if ("APROVADO".equalsIgnoreCase(novoStatus)) {
            pagamentos.atualizarStatus(pagamento.id(), StatusPagamento.APROVADO, Instant.now());
            log.info("Pagamento {} aprovado", pagamento.id());

            Optional<Venda> vendaOpt = vendas.buscarPorId(pagamento.vendaId());
            if (vendaOpt.isPresent()) {
                Venda venda = vendaOpt.get();
                if (venda.status() == StatusVenda.PAGA) {
                    eventPublisher.publish(pagamento.vendaId().valor().toString(), "PIX", "APROVADO");
                    return;
                }

                if (todosPagamentosAprovados(pagamento.vendaId())) {
                    vendas.salvar(new Venda(venda.id(), venda.empresaId(), venda.lojaId(), venda.sessaoCaixaId(),
                            venda.usuarioId(), venda.clienteId(), venda.numero(), venda.subtotal(), venda.desconto(),
                            venda.acrescimo(), venda.total(), venda.custoTotal(), venda.lucro(), venda.prejuizo(),
                            venda.quantidadeItens(), StatusVenda.PAGA, Instant.now(), null, null, venda.observacao(),
                            venda.criadoEm(), Instant.now()));
                    List<ItemVenda> itens = vendas.listarItens(venda.id());
                    dashboard.registrarVendaPaga(venda, itens);
                    if (!movimentacoes.existeMovimentacaoVenda(venda.id())) {
                        finalizacaoHelper.movimentarEstoqueSeNecessario(venda, itens);
                    } else {
                        log.info("Venda {} já teve estoque movimentado — pulando (idempotência)", venda.id());
                    }
                    log.info("Venda {} marcada como PAGA, estoque movimentado e dashboard atualizado", venda.id());
                } else {
                    log.info("Venda {} ainda tem pagamentos pendentes, mantendo AGUARDANDO_PAGAMENTO", venda.id());
                }
                eventPublisher.publish(pagamento.vendaId().valor().toString(), "PIX", "APROVADO");
            }
        } else if ("REJEITADO".equalsIgnoreCase(novoStatus) || "EXPIRADO".equalsIgnoreCase(novoStatus)) {
            pagamentos.atualizarStatus(pagamento.id(), StatusPagamento.RECUSADO, null);
            log.info("Pagamento {} rejeitado/expirado", pagamento.id());
            eventPublisher.publish(pagamento.vendaId().valor().toString(), "PIX", "REJEITADO");
        }
    }

    private boolean todosPagamentosAprovados(Identificador vendaId) {
        List<Pagamento> pagamentosVenda = pagamentos.listarPorVenda(vendaId);
        if (pagamentosVenda.isEmpty()) {
            return false;
        }
        return pagamentosVenda.stream()
                .allMatch(p -> p.status() == StatusPagamento.APROVADO);
    }
}
