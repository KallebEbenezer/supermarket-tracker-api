package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.RegistrarMovimentacaoEstoqueCommand;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.Produto;
import com.supermarkettracker.domain.model.enums.TipoMovimentacaoEstoque;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.MovimentacaoEstoqueRepository;
import com.supermarkettracker.domain.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.time.Instant;
public final class RegistrarMovimentacaoEstoqueUseCase { private final ProdutoRepository produtos; private final MovimentacaoEstoqueRepository movimentacoes;
    public RegistrarMovimentacaoEstoqueUseCase(ProdutoRepository produtos, MovimentacaoEstoqueRepository movimentacoes) { this.produtos = produtos; this.movimentacoes = movimentacoes; }
    public MovimentacaoEstoque executar(RegistrarMovimentacaoEstoqueCommand c) { ValidacaoCommand.obrigatorio(c.empresaId(), "Empresa"); ValidacaoCommand.obrigatorio(c.lojaId(), "Loja"); ValidacaoCommand.obrigatorio(c.produtoId(), "Produto"); ValidacaoCommand.obrigatorio(c.usuarioId(), "Usuario"); ValidacaoCommand.obrigatorio(c.tipo(), "Tipo"); ValidacaoCommand.positivo(c.quantidade(), "Quantidade"); if (c.tipo() == TipoMovimentacaoEstoque.AJUSTE) ValidacaoCommand.obrigatorio(c.motivo(), "Motivo do ajuste"); Produto produto = produtos.buscarPorId(new Identificador(c.produtoId())).orElseThrow(() -> new EntidadeNaoEncontradaException("Produto nao encontrado")); BigDecimal anterior = produto.estoqueAtual().valor(); BigDecimal posterior = anterior.add(c.tipo() == TipoMovimentacaoEstoque.ENTRADA || c.tipo() == TipoMovimentacaoEstoque.CANCELAMENTO ? c.quantidade() : c.quantidade().negate()); if (posterior.signum() < 0 && !produto.permiteEstoqueNegativo()) throw new RegraDeDominioException("Estoque insuficiente"); MovimentacaoEstoque m = new MovimentacaoEstoque(Identificador.novo(), new Identificador(c.empresaId()), new Identificador(c.lojaId()), produto.id(), new Identificador(c.usuarioId()), c.vendaId() == null ? null : new Identificador(c.vendaId()), c.itemVendaId() == null ? null : new Identificador(c.itemVendaId()), c.tipo(), new Quantidade(c.quantidade()), new Quantidade(anterior), new Quantidade(posterior), c.custoUnitario() == null ? null : new Dinheiro(c.custoUnitario()), c.motivo(), c.referenciaExterna(), Instant.now()); return movimentacoes.salvar(m); } }
