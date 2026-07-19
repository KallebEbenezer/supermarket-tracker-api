package com.supermarkettracker.infrastructure.persistence.adapter;
import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.VendaRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.VendaPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.ItemVendaJpaRepository;
import com.supermarkettracker.infrastructure.persistence.repository.VendaJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class VendaRepositoryJpaAdapter implements VendaRepository {
    private final VendaJpaRepository vendas;
    private final ItemVendaJpaRepository itens;
    private final VendaPersistenceMapper mapper;
    private final EntityManager entityManager;

    public VendaRepositoryJpaAdapter(VendaJpaRepository vendas, ItemVendaJpaRepository itens,
            VendaPersistenceMapper mapper, EntityManager entityManager) {
        this.vendas = vendas;
        this.itens = itens;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    public Venda salvar(Venda venda) { return mapper.toDomain(vendas.save(mapper.toEntity(venda))); }
    public Optional<Venda> buscarPorId(Identificador id) { return vendas.findById(id.valor()).map(mapper::toDomain); }
    public ItemVenda salvarItem(ItemVenda item) { return mapper.toDomain(itens.save(mapper.toEntity(item))); }

    @Transactional
    public ItemVenda salvarItemComBaixaEstoque(ItemVenda item, MovimentacaoEstoque movimentacao) {
        ItemVenda salvo = mapper.toDomain(itens.saveAndFlush(mapper.toEntity(item)));
        registrarMovimentacao(movimentacao);
        return salvo;
    }

    public List<ItemVenda> listarItens(Identificador vendaId) {
        return itens.findByVendaIdOrderByNumeroAsc(vendaId.valor()).stream().map(mapper::toDomain).toList();
    }

    private UUID registrarMovimentacao(MovimentacaoEstoque movimentacao) {
        return (UUID) entityManager.createNativeQuery("select registrar_movimentacao_estoque(cast(? as uuid),"
                + "cast(? as uuid),cast(? as uuid),cast(? as uuid),cast(? as tipo_movimentacao_estoque),"
                + "cast(? as numeric),cast(? as uuid),cast(? as uuid),cast(? as numeric),?,?)")
                .setParameter(1, movimentacao.empresaId().valor())
                .setParameter(2, movimentacao.lojaId().valor())
                .setParameter(3, movimentacao.produtoId().valor())
                .setParameter(4, movimentacao.usuarioId().valor())
                .setParameter(5, movimentacao.tipo().name())
                .setParameter(6, movimentacao.quantidade().valor())
                .setParameter(7, movimentacao.vendaId() == null ? null : movimentacao.vendaId().valor())
                .setParameter(8, movimentacao.itemVendaId() == null ? null : movimentacao.itemVendaId().valor())
                .setParameter(9, movimentacao.custoUnitario() == null ? null : movimentacao.custoUnitario().valor())
                .setParameter(10, movimentacao.motivo())
                .setParameter(11, movimentacao.referenciaExterna())
                .getSingleResult();
    }
}
