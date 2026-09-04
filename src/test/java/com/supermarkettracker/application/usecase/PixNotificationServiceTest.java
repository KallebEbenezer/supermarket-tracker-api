package com.supermarkettracker.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.PagamentoPix;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.StatusVenda;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.DashboardRepository;
import com.supermarkettracker.domain.repository.MovimentacaoEstoqueRepository;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.domain.repository.VendaRepository;
import com.supermarkettracker.infrastructure.websocket.PaymentWebSocketPublisher;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PixNotificationServiceTest {

    private PagamentoRepository pagamentos;
    private VendaRepository vendas;
    private DashboardRepository dashboard;
    private MovimentacaoEstoqueRepository movimentacoes;
    private FinalizarVendaUseCase finalizacaoHelper;
    private PaymentWebSocketPublisher eventPublisher;
    private PixNotificationService service;

    private Identificador vendaId;
    private Identificador pagamentoId;
    private Identificador pagamentoPixId;

    @BeforeEach
    void setUp() {
        pagamentos = mock(PagamentoRepository.class);
        vendas = mock(VendaRepository.class);
        dashboard = mock(DashboardRepository.class);
        movimentacoes = mock(MovimentacaoEstoqueRepository.class);
        finalizacaoHelper = mock(FinalizarVendaUseCase.class);
        eventPublisher = mock(PaymentWebSocketPublisher.class);
        service = new PixNotificationService(pagamentos, vendas, dashboard, movimentacoes, finalizacaoHelper, eventPublisher);

        vendaId = new Identificador(UUID.randomUUID());
        pagamentoId = new Identificador(UUID.randomUUID());
        pagamentoPixId = new Identificador(UUID.randomUUID());

        var pix = new PagamentoPix(pagamentoId, "qr", "copia", "tx-mp", "mercadopago", "tx-mp", "PENDENTE", null, null);
        when(pagamentos.buscarPixPorGatewayTransacaoId("tx-mp")).thenReturn(Optional.of(pix));
    }

    @Test
    void aprovadoQuandoTodosPagamentosAprovados_marcaVendaPagaEMovimentaEstoqueEDashboard() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.PENDENTE)));
        when(vendas.buscarPorId(vendaId)).thenReturn(Optional.of(venda(StatusVenda.AGUARDANDO_PAGAMENTO)));
        when(pagamentos.listarPorVenda(vendaId)).thenReturn(List.of(pagamento(StatusPagamento.APROVADO)));
        when(vendas.listarItens(vendaId)).thenReturn(List.of(item()));
        when(movimentacoes.existeMovimentacaoVenda(vendaId)).thenReturn(false);

        service.processarNotificacao("tx-mp", "APROVADO");

        verify(pagamentos).atualizarStatus(eq(pagamentoId), eq(StatusPagamento.APROVADO), any());
        verify(vendas).salvar(any(Venda.class));
        verify(dashboard).registrarVendaPaga(any(Venda.class), anyList());
        verify(finalizacaoHelper).movimentarEstoqueSeNecessario(any(Venda.class), anyList());
        verify(eventPublisher).publish(vendaId.valor().toString(), "PIX", "APROVADO");
    }

    @Test
    void aprovadoQuandoEstoqueJaMovimentado_pulaMovimentacao() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.PENDENTE)));
        when(vendas.buscarPorId(vendaId)).thenReturn(Optional.of(venda(StatusVenda.AGUARDANDO_PAGAMENTO)));
        when(pagamentos.listarPorVenda(vendaId)).thenReturn(List.of(pagamento(StatusPagamento.APROVADO)));
        when(vendas.listarItens(vendaId)).thenReturn(List.of(item()));
        when(movimentacoes.existeMovimentacaoVenda(vendaId)).thenReturn(true);

        service.processarNotificacao("tx-mp", "APROVADO");

        verify(dashboard).registrarVendaPaga(any(Venda.class), anyList());
        verify(finalizacaoHelper, never()).movimentarEstoqueSeNecessario(any(), anyList());
    }

    @Test
    void aprovadoQuandoAindaHaPagamentosPendentes_naoMarcaVendaPaga() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.PENDENTE)));
        when(vendas.buscarPorId(vendaId)).thenReturn(Optional.of(venda(StatusVenda.AGUARDANDO_PAGAMENTO)));
        when(pagamentos.listarPorVenda(vendaId)).thenReturn(List.of(
                pagamento(StatusPagamento.APROVADO),
                pagamento(StatusPagamento.PENDENTE)));

        service.processarNotificacao("tx-mp", "APROVADO");

        verify(vendas, never()).salvar(any(Venda.class));
        verify(dashboard, never()).registrarVendaPaga(any(), anyList());
        verify(finalizacaoHelper, never()).movimentarEstoqueSeNecessario(any(), anyList());
        verify(eventPublisher).publish(vendaId.valor().toString(), "PIX", "APROVADO");
    }

    @Test
    void aprovadoQuandoVendaJaEstaPaga_apenasNotifica() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.PENDENTE)));
        when(vendas.buscarPorId(vendaId)).thenReturn(Optional.of(venda(StatusVenda.PAGA)));

        service.processarNotificacao("tx-mp", "APROVADO");

        verify(vendas, never()).salvar(any(Venda.class));
        verify(dashboard, never()).registrarVendaPaga(any(), anyList());
        verify(eventPublisher).publish(vendaId.valor().toString(), "PIX", "APROVADO");
    }

    @Test
    void rejeitadoMarcaPagamentoRecusadoENotifica() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.PENDENTE)));

        service.processarNotificacao("tx-mp", "REJEITADO");

        verify(pagamentos).atualizarStatus(eq(pagamentoId), eq(StatusPagamento.RECUSADO), eq(null));
        verify(eventPublisher).publish(vendaId.valor().toString(), "PIX", "REJEITADO");
    }

    @Test
    void expiradoMarcaPagamentoRecusadoENotifica() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.PENDENTE)));

        service.processarNotificacao("tx-mp", "EXPIRADO");

        verify(pagamentos).atualizarStatus(eq(pagamentoId), eq(StatusPagamento.RECUSADO), eq(null));
        verify(eventPublisher).publish(vendaId.valor().toString(), "PIX", "REJEITADO");
    }

    @Test
    void pagamentoJaAprovado_ignoraNotificacao() {
        when(pagamentos.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento(StatusPagamento.APROVADO)));

        service.processarNotificacao("tx-mp", "APROVADO");

        verify(pagamentos, never()).atualizarStatus(any(), any(), any());
        verify(eventPublisher, never()).publish(any(), any(), any());
    }

    private Pagamento pagamento(StatusPagamento status) {
        return new Pagamento(pagamentoId, vendaId, null, TipoPagamento.PIX,
                new Dinheiro(BigDecimal.TEN), status, null, "tx-mp", Instant.now(), Instant.now());
    }

    private Venda venda(StatusVenda status) {
        return new Venda(vendaId, null, null, null, null, null, 1L,
                new Dinheiro(BigDecimal.TEN), new Dinheiro(BigDecimal.ZERO), new Dinheiro(BigDecimal.ZERO),
                new Dinheiro(BigDecimal.TEN), new Dinheiro(BigDecimal.TEN), new Dinheiro(BigDecimal.ZERO),
                new Dinheiro(BigDecimal.ZERO), new Quantidade(BigDecimal.ONE),
                status, null, null, null, null, Instant.now(), Instant.now());
    }

    private ItemVenda item() {
        return new ItemVenda(new Identificador(UUID.randomUUID()), vendaId, null, 1,
                "Arroz", null, "UN", new Quantidade(BigDecimal.ONE),
                new Dinheiro(BigDecimal.TEN), new Dinheiro(BigDecimal.TEN),
                new Dinheiro(BigDecimal.ZERO), new Dinheiro(BigDecimal.ZERO),
                new Dinheiro(BigDecimal.TEN), new Dinheiro(BigDecimal.TEN),
                new Dinheiro(BigDecimal.ZERO), Instant.now());
    }
}
