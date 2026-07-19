package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.CadastrarProdutoCommand;
import com.supermarkettracker.application.dto.ProdutoDto;
import com.supermarkettracker.application.mapper.ProdutoMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.ConflitoDeDominioException;
import com.supermarkettracker.domain.model.Produto;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.ProdutoRepository;
import java.time.Instant;
public final class CadastrarProdutoUseCase { private final ProdutoRepository produtos; public CadastrarProdutoUseCase(ProdutoRepository produtos) { this.produtos = produtos; }
    public ProdutoDto executar(CadastrarProdutoCommand c) { ValidacaoCommand.obrigatorio(c.empresaId(), "Empresa"); ValidacaoCommand.obrigatorio(c.codigoBarras(), "Codigo de barras"); ValidacaoCommand.obrigatorio(c.nome(), "Nome"); ValidacaoCommand.naoNegativo(c.precoCompra(), "Preco de compra"); ValidacaoCommand.naoNegativo(c.precoVenda(), "Preco de venda"); ValidacaoCommand.naoNegativo(c.estoqueMinimo(), "Estoque minimo"); Identificador empresaId = new Identificador(c.empresaId()); if (produtos.buscarPorCodigoBarras(empresaId, c.codigoBarras()).isPresent()) throw new ConflitoDeDominioException("Codigo de barras ja cadastrado"); Instant agora = Instant.now(); Produto p = new Produto(Identificador.novo(), empresaId, c.categoriaId() == null ? null : new Identificador(c.categoriaId()), c.codigoBarras(), c.sku(), c.nome(), c.descricao(), null, c.unidadeMedida() == null ? "UN" : c.unidadeMedida(), new Dinheiro(c.precoCompra()), new Dinheiro(c.precoVenda()), Quantidade.zero(), new Quantidade(c.estoqueMinimo()), c.permiteEstoqueNegativo(), Produto.StatusProduto.ATIVO, agora, agora); return ProdutoMapper.paraDto(produtos.salvar(p)); } }
