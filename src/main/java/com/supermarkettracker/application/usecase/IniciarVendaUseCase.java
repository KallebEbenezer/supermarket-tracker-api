package com.supermarkettracker.application.usecase;
import com.supermarkettracker.application.command.IniciarVendaCommand;
import com.supermarkettracker.application.dto.VendaDto;
import com.supermarkettracker.application.mapper.VendaMapper;
import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.enums.StatusVenda;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.model.valueobject.Quantidade;
import com.supermarkettracker.domain.repository.VendaRepository;
import java.math.BigDecimal;
import java.time.Instant;
public final class IniciarVendaUseCase { private final VendaRepository vendas; public IniciarVendaUseCase(VendaRepository vendas) { this.vendas = vendas; }
    public VendaDto executar(IniciarVendaCommand c) { ValidacaoCommand.obrigatorio(c.empresaId(), "Empresa"); ValidacaoCommand.obrigatorio(c.lojaId(), "Loja"); ValidacaoCommand.obrigatorio(c.usuarioId(), "Usuario"); ValidacaoCommand.positivo(c.quantidadeItens(), "Quantidade de itens"); ValidacaoCommand.naoNegativo(c.subtotal(), "Subtotal"); ValidacaoCommand.naoNegativo(c.desconto(), "Desconto"); ValidacaoCommand.naoNegativo(c.acrescimo(), "Acrescimo"); BigDecimal total = c.subtotal().subtract(c.desconto()).add(c.acrescimo()); if (total.signum() < 0) throw new IllegalArgumentException("Total da venda nao pode ser negativo"); Instant agora = Instant.now(); Venda v = new Venda(Identificador.novo(), new Identificador(c.empresaId()), new Identificador(c.lojaId()), c.sessaoCaixaId() == null ? null : new Identificador(c.sessaoCaixaId()), new Identificador(c.usuarioId()), c.clienteId() == null ? null : new Identificador(c.clienteId()), c.numero(), new Dinheiro(c.subtotal()), new Dinheiro(c.desconto()), new Dinheiro(c.acrescimo()), new Dinheiro(total), Dinheiro.zero(), Dinheiro.zero(), Dinheiro.zero(), new Quantidade(c.quantidadeItens()), StatusVenda.ABERTA, null, null, null, c.observacao(), agora, agora); return VendaMapper.paraDto(vendas.salvar(v)); } }
