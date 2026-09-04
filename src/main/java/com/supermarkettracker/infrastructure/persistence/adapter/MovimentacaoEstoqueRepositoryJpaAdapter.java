package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.MovimentacaoEstoque;
import com.supermarkettracker.domain.repository.MovimentacaoEstoqueRepository;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.infrastructure.persistence.entity.MovimentacaoEstoqueEntity;
import com.supermarkettracker.infrastructure.persistence.mapper.MovimentacaoEstoquePersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.MovimentacaoEstoqueJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class MovimentacaoEstoqueRepositoryJpaAdapter implements MovimentacaoEstoqueRepository {
    private final MovimentacaoEstoqueJpaRepository jpa;
    private final MovimentacaoEstoquePersistenceMapper mapper;
    private final EntityManager em;

    public MovimentacaoEstoqueRepositoryJpaAdapter(MovimentacaoEstoqueJpaRepository j,
            MovimentacaoEstoquePersistenceMapper m, EntityManager e) {
        jpa = j;
        mapper = m;
        em = e;
    }

    public MovimentacaoEstoque salvar(MovimentacaoEstoque x) {
        UUID id = (UUID) em.createNativeQuery(
                "select registrar_movimentacao_estoque(cast(? as uuid),cast(? as uuid),cast(? as uuid),cast(? as uuid),cast(? as tipo_movimentacao_estoque),cast(? as numeric),cast(? as uuid),cast(? as uuid),cast(? as numeric),?,?)")
                .setParameter(1, x.empresaId().valor()).setParameter(2, x.lojaId().valor())
                .setParameter(3, x.produtoId().valor()).setParameter(4, x.usuarioId().valor())
                .setParameter(5, x.tipo().name()).setParameter(6, x.quantidade().valor())
                .setParameter(7, x.vendaId() == null ? null : x.vendaId().valor())
                .setParameter(8, x.itemVendaId() == null ? null : x.itemVendaId().valor())
                .setParameter(9, x.custoUnitario() == null ? null : x.custoUnitario().valor())
                .setParameter(10, x.motivo()).setParameter(11, x.referenciaExterna()).getSingleResult();
        return mapper.toDomain(jpa.getReferenceById(id));
    }

    public List<MovimentacaoEstoque> listarPorProduto(Identificador id) {
        return jpa.findByProdutoIdOrderByCriadoEmDesc(id.valor()).stream().map(mapper::toDomain).toList();
    }

    public List<MovimentacaoEstoque> listarPorEmpresa(Identificador empresaId, Identificador lojaId) {
        List<MovimentacaoEstoqueEntity> e = lojaId == null ? jpa.findByEmpresaIdOrderByCriadoEmDesc(empresaId.valor())
                : jpa.findByEmpresaIdAndLojaIdOrderByCriadoEmDesc(empresaId.valor(), lojaId.valor());
        return e.stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existeMovimentacaoVenda(Identificador vendaId) {
        return jpa.existsByVendaId(vendaId.valor());
    }
}
