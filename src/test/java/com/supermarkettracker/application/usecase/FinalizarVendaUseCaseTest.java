package com.supermarkettracker.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.supermarkettracker.application.command.*;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.gateway.*;
import com.supermarkettracker.domain.model.*;
import com.supermarkettracker.domain.model.enums.*;
import com.supermarkettracker.domain.model.valueobject.*;
import com.supermarkettracker.domain.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;

class FinalizarVendaUseCaseTest {
        private final UUID empresa = UUID.randomUUID(), loja = UUID.randomUUID(), usuario = UUID.randomUUID(),
                        produto = UUID.randomUUID();

        @Test
        void finalizaVendaDinheiro_salvaItensPagamentoMovimentacaoEDashboard() {
                VendaRepository vendas = mock(VendaRepository.class);
                ProdutoRepository produtos = mock(ProdutoRepository.class);
                PagamentoRepository pagamentos = mock(PagamentoRepository.class);
                MovimentacaoEstoqueRepository movimentacoes = mock(MovimentacaoEstoqueRepository.class);
                DashboardRepository dashboard = mock(DashboardRepository.class);
                when(vendas.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(vendas.salvarItem(any())).thenAnswer(i -> i.getArgument(0));
                when(pagamentos.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(produtos.buscarPorId(any())).thenReturn(Optional.of(produto()));
                when(movimentacoes.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(movimentacoes.existeMovimentacaoVenda(any())).thenReturn(false);

                var dto = useCase(vendas, produtos, pagamentos, movimentacoes, dashboard, mock(PixGateway.class),
                                mock(CartaoGateway.class)).executar(command(TipoPagamento.DINHEIRO));

                assertThat(dto.status()).isEqualTo(StatusVenda.PAGA.name());
                assertThat(dto.total()).isEqualByComparingTo("20.00");
                verify(vendas, times(2)).salvar(any());
                verify(vendas).salvarItem(any());
                verify(pagamentos).salvar(any());
                verify(movimentacoes).salvar(argThat(m -> m.tipo() == TipoMovimentacaoEstoque.VENDA
                                && m.estoquePosterior().valor().compareTo(BigDecimal.valueOf(8)) == 0));
                verify(dashboard).registrarVendaPaga(any(), anyList());
        }

        @Test
        void finalizaVenda_pixECartaoUsamGatewaysEspecializados() {
                VendaRepository vendas = mock(VendaRepository.class);
                ProdutoRepository produtos = mock(ProdutoRepository.class);
                PagamentoRepository pagamentos = mock(PagamentoRepository.class);
                MovimentacaoEstoqueRepository movimentacoes = mock(MovimentacaoEstoqueRepository.class);
                DashboardRepository dashboard = mock(DashboardRepository.class);
                PixGateway pix = mock(PixGateway.class);
                CartaoGateway cartao = mock(CartaoGateway.class);
                when(vendas.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(vendas.salvarItem(any())).thenAnswer(i -> i.getArgument(0));
                when(produtos.buscarPorId(any())).thenReturn(Optional.of(produto()));
                when(movimentacoes.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(movimentacoes.existeMovimentacaoVenda(any())).thenReturn(false);
                when(pix.criarCobranca(any(), any(), any()))
                                .thenReturn(new PixGateway.CobrancaPix("pix", "tx", "APROVADO", "qr", "copia", null));
                var pixCommand = new FinalizarVendaCommand(empresa, loja, null, usuario, null, 1L, BigDecimal.ZERO,
                                BigDecimal.ZERO, null, List.of(item()),
                                List.of(new PagamentoCheckoutCommand(null, TipoPagamento.PIX,
                                                BigDecimal.valueOf(20), null, (short) 1, null, null)));
                useCase(vendas, produtos, pagamentos, movimentacoes, dashboard, pix, cartao).executar(pixCommand);
                verify(pagamentos).salvarPix(any(), any());

                reset(pagamentos);
                when(cartao.processar(any(), any(), any(), anyShort(), any()))
                                .thenReturn(new CartaoGateway.TransacaoCartao("card", "tx", "APROVADO", "auth"));
                var cardCommand = new FinalizarVendaCommand(empresa, loja, null, usuario, null, 2L, BigDecimal.ZERO,
                                BigDecimal.ZERO, null, List.of(item()),
                                List.of(new PagamentoCheckoutCommand(null, TipoPagamento.CARTAO,
                                                BigDecimal.valueOf(20), ModalidadeCartao.CREDITO, (short) 2, null, null)));
                useCase(vendas, produtos, pagamentos, movimentacoes, dashboard, pix, cartao).executar(cardCommand);
                verify(pagamentos).salvarCartao(any(), any());
        }

        @Test
        void finalizaVenda_rejeitaPagamentoComTotalDiferenteOuGatewayNegado() {
                var uc = useCase(mock(VendaRepository.class), mock(ProdutoRepository.class),
                                mock(PagamentoRepository.class),
                                mock(MovimentacaoEstoqueRepository.class), mock(DashboardRepository.class),
                                mock(PixGateway.class),
                                mock(CartaoGateway.class));
                var invalido = new FinalizarVendaCommand(empresa, loja, null, usuario, null, 1L, BigDecimal.ZERO,
                                BigDecimal.ZERO, null, List.of(item()), List.of(new PagamentoCheckoutCommand(null,
                                                TipoPagamento.DINHEIRO, BigDecimal.ONE, null, (short) 1, null, null)));
                assertThatThrownBy(() -> uc.executar(invalido)).isInstanceOf(RegraDeDominioException.class)
                                .hasMessageContaining("total dos pagamentos");
        }

        @Test
        void finalizasVenda_consultaProdutoUmaVezQuandoEleApareceEmMaisDeUmItem() {
                VendaRepository vendas = mock(VendaRepository.class);
                ProdutoRepository produtos = mock(ProdutoRepository.class);
                PagamentoRepository pagamentos = mock(PagamentoRepository.class);
                MovimentacaoEstoqueRepository movimentacoes = mock(MovimentacaoEstoqueRepository.class);
                when(vendas.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(vendas.salvarItem(any())).thenAnswer(i -> i.getArgument(0));
                when(pagamentos.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(produtos.buscarPorId(any())).thenReturn(Optional.of(produto()));
                when(movimentacoes.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(movimentacoes.existeMovimentacaoVenda(any())).thenReturn(false);
                var itens = List.of(item(), new ItemVendaCheckoutCommand(produto, 2, "Arroz", "789", "UN",
                                BigDecimal.valueOf(2), BigDecimal.TEN, BigDecimal.valueOf(6), BigDecimal.ZERO,
                                BigDecimal.ZERO));
                var pagamento = new PagamentoCheckoutCommand(null, TipoPagamento.DINHEIRO, BigDecimal.valueOf(40), null,
                                (short) 1, null, null);

                useCase(vendas, produtos, pagamentos, movimentacoes, mock(DashboardRepository.class),
                                mock(PixGateway.class),
                                mock(CartaoGateway.class)).executar(
                                                new FinalizarVendaCommand(empresa, loja, null, usuario, null,
                                                                1L, BigDecimal.ZERO, BigDecimal.ZERO, null, itens,
                                                                List.of(pagamento)));

                verify(produtos, times(1)).buscarPorId(new Identificador(produto));
                verify(movimentacoes, times(2)).salvar(any());
        }

        @Test
        void finalizaVenda_comPagamentoPixPendente_vendaFicaAguardandoPagamentoSemMovimentarEstoque() {
                VendaRepository vendas = mock(VendaRepository.class);
                ProdutoRepository produtos = mock(ProdutoRepository.class);
                PagamentoRepository pagamentos = mock(PagamentoRepository.class);
                MovimentacaoEstoqueRepository movimentacoes = mock(MovimentacaoEstoqueRepository.class);
                DashboardRepository dashboard = mock(DashboardRepository.class);
                PixGateway pix = mock(PixGateway.class);
                CartaoGateway cartao = mock(CartaoGateway.class);
                when(vendas.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(vendas.salvarItem(any())).thenAnswer(i -> i.getArgument(0));
                when(produtos.buscarPorId(any())).thenReturn(Optional.of(produto()));
                when(pix.criarCobranca(any(), any(), any()))
                                .thenReturn(new PixGateway.CobrancaPix("mock", "tx-123", "PENDENTE", "qr", "copia", null));

                Identificador vendaId = new Identificador(UUID.randomUUID());
                Pagamento pagamentoPendente = mock(Pagamento.class);
                when(pagamentoPendente.status()).thenReturn(StatusPagamento.PENDENTE);
                when(pagamentos.listarPorVenda(any())).thenReturn(List.of(pagamentoPendente));

                var pixCommand = new FinalizarVendaCommand(empresa, loja, null, usuario, null, 1L, BigDecimal.ZERO,
                                BigDecimal.ZERO, null, List.of(item()),
                                List.of(new PagamentoCheckoutCommand(null, TipoPagamento.PIX,
                                                BigDecimal.valueOf(20), null, (short) 1, null, null)));

                var dto = useCase(vendas, produtos, pagamentos, movimentacoes, dashboard, pix, cartao)
                                .executar(pixCommand);

                assertThat(dto.status()).isEqualTo(StatusVenda.AGUARDANDO_PAGAMENTO.name());
                verify(movimentacoes, never()).salvar(any());
                verify(dashboard, never()).registrarVendaPaga(any(), anyList());
        }

        @Test
        void finalizaVenda_pagamentoMistoDinheiroMaisPix_vendaFicaAguardandoPagamento() {
                VendaRepository vendas = mock(VendaRepository.class);
                ProdutoRepository produtos = mock(ProdutoRepository.class);
                PagamentoRepository pagamentos = mock(PagamentoRepository.class);
                MovimentacaoEstoqueRepository movimentacoes = mock(MovimentacaoEstoqueRepository.class);
                DashboardRepository dashboard = mock(DashboardRepository.class);
                PixGateway pix = mock(PixGateway.class);
                CartaoGateway cartao = mock(CartaoGateway.class);
                when(vendas.salvar(any())).thenAnswer(i -> i.getArgument(0));
                when(vendas.salvarItem(any())).thenAnswer(i -> i.getArgument(0));
                when(produtos.buscarPorId(any())).thenReturn(Optional.of(produto()));
                when(pix.criarCobranca(any(), any(), any()))
                                .thenReturn(new PixGateway.CobrancaPix("mock", "tx", "PENDENTE", "qr", "copia", null));

                Pagamento pixPendente = mock(Pagamento.class);
                when(pixPendente.status()).thenReturn(StatusPagamento.PENDENTE);
                Pagamento dinheiroAprovado = mock(Pagamento.class);
                when(dinheiroAprovado.status()).thenReturn(StatusPagamento.APROVADO);
                when(pagamentos.listarPorVenda(any())).thenReturn(List.of(pixPendente, dinheiroAprovado));

                var mixedCommand = new FinalizarVendaCommand(empresa, loja, null, usuario, null, 1L, BigDecimal.ZERO,
                                BigDecimal.ZERO, null, List.of(item()),
                                List.of(
                                                new PagamentoCheckoutCommand(null, TipoPagamento.PIX,
                                                                BigDecimal.valueOf(30), null, (short) 1, null, null),
                                                new PagamentoCheckoutCommand(null, TipoPagamento.DINHEIRO,
                                                                BigDecimal.valueOf(20), null, (short) 1, null, null)));

                var dto = useCase(vendas, produtos, pagamentos, movimentacoes, dashboard, pix, cartao)
                                .executar(mixedCommand);

                assertThat(dto.status()).isEqualTo(StatusVenda.AGUARDANDO_PAGAMENTO.name());
                verify(movimentacoes, never()).salvar(any());
                verify(dashboard, never()).registrarVendaPaga(any(), anyList());
        }

        private FinalizarVendaUseCase useCase(VendaRepository v, ProdutoRepository p, PagamentoRepository pg,
                        MovimentacaoEstoqueRepository m, DashboardRepository d, PixGateway pix, CartaoGateway card) {
                ContaBancariaRepository contas = mock(ContaBancariaRepository.class);
                return new FinalizarVendaUseCase(v, p, pg, m, d, contas, pix, card);
        }

        private FinalizarVendaCommand command(TipoPagamento tipo) {
                return new FinalizarVendaCommand(empresa, loja, null, usuario, null, 1L, BigDecimal.ZERO,
                                BigDecimal.ZERO, null,
                                List.of(item()),
                                List.of(new PagamentoCheckoutCommand(null, tipo, BigDecimal.valueOf(20), null,
                                                (short) 1, null, null)));
        }

        private ItemVendaCheckoutCommand item() {
                return new ItemVendaCheckoutCommand(produto, 1, "Arroz", "789", "UN", BigDecimal.valueOf(2),
                                BigDecimal.TEN,
                                BigDecimal.valueOf(6), BigDecimal.ZERO, BigDecimal.ZERO);
        }

        private Produto produto() {
                Instant now = Instant.now();
                return new Produto(new Identificador(produto), new Identificador(empresa), null, "789", null, "Arroz",
                                null,
                                null, "UN", new Dinheiro(BigDecimal.valueOf(6)), new Dinheiro(BigDecimal.TEN),
                                new Quantidade(BigDecimal.TEN), Quantidade.zero(), false, Produto.StatusProduto.ATIVO,
                                now, now);
        }
}
