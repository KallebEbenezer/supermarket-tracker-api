package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.RegistrarPagamentoCommand;
import com.supermarkettracker.application.dto.PagamentoDto;
import com.supermarkettracker.application.mapper.PagamentoMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.StatusVenda;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.domain.repository.VendaRepository;
import java.time.Instant;
public final class RegistrarPagamentoUseCase { private final PagamentoRepository pagamentos; private final VendaRepository vendas; private final ProcessadorPagamentoGateway processador;
    public RegistrarPagamentoUseCase(PagamentoRepository pagamentos, VendaRepository vendas, ProcessadorPagamentoGateway processador) { this.pagamentos = pagamentos; this.vendas = vendas; this.processador = processador; }
    public PagamentoDto executar(RegistrarPagamentoCommand c) { ValidacaoCommand.obrigatorio(c.vendaId(), "Venda"); ValidacaoCommand.obrigatorio(c.tipo(), "Tipo de pagamento"); ValidacaoCommand.positivo(c.valor(), "Valor"); Venda venda = vendas.buscarPorId(new Identificador(c.vendaId())).orElseThrow(() -> new EntidadeNaoEncontradaException("Venda nao encontrada")); if (venda.status() == StatusVenda.CANCELADA || venda.status() == StatusVenda.ESTORNADA) throw new RegraDeDominioException("Venda nao aceita pagamento"); String referencia = c.referencia(); if (c.tipo() == TipoPagamento.PIX) referencia = processador.processarPix(Identificador.novo(), new Dinheiro(c.valor())).referenciaExterna(); if (c.tipo() == TipoPagamento.CARTAO) { ValidacaoCommand.obrigatorio(c.modalidadeCartao(), "Modalidade do cartao"); referencia = processador.processarCartao(Identificador.novo(), new Dinheiro(c.valor()), c.modalidadeCartao(), c.parcelas()).referenciaExterna(); } Pagamento p = new Pagamento(Identificador.novo(), venda.id(), c.contaBancariaId() == null ? null : new Identificador(c.contaBancariaId()), c.tipo(), new Dinheiro(c.valor()), StatusPagamento.APROVADO, Instant.now(), referencia, Instant.now(), Instant.now()); return PagamentoMapper.paraDto(pagamentos.salvar(p)); } }
