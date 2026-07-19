package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.repository.DashboardRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepositoryJpaAdapter implements DashboardRepository {
    private final EntityManager entityManager;

    public DashboardRepositoryJpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void registrarVendaPaga(Venda venda, List<ItemVenda> itens) {
        entityManager.createNativeQuery("""
                insert into dashboard_cache_diario (
                    id, empresa_id, loja_id, referencia_date, vendas_quantidade, faturamento, descontos,
                    lucro, prejuizo, ticket_medio, produtos_vendidos, ultima_atualizacao, versao
                ) values (
                    gen_random_uuid(), cast(? as uuid), cast(? as uuid), cast(? as date), 1, cast(? as numeric),
                    cast(? as numeric), cast(? as numeric), cast(? as numeric), cast(? as numeric),
                    cast(? as numeric), now(), 1
                ) on conflict (empresa_id, (coalesce(loja_id, '00000000-0000-0000-0000-000000000000'::uuid)), referencia_date)
                do update set
                    vendas_quantidade = dashboard_cache_diario.vendas_quantidade + 1,
                    faturamento = dashboard_cache_diario.faturamento + excluded.faturamento,
                    descontos = dashboard_cache_diario.descontos + excluded.descontos,
                    lucro = dashboard_cache_diario.lucro + excluded.lucro,
                    prejuizo = dashboard_cache_diario.prejuizo + excluded.prejuizo,
                    ticket_medio = (dashboard_cache_diario.faturamento + excluded.faturamento)
                        / (dashboard_cache_diario.vendas_quantidade + 1),
                    produtos_vendidos = dashboard_cache_diario.produtos_vendidos + excluded.produtos_vendidos,
                    ultima_atualizacao = now(),
                    versao = dashboard_cache_diario.versao + 1
                """)
                .setParameter(1, venda.empresaId().valor())
                .setParameter(2, venda.lojaId().valor())
                .setParameter(3, LocalDate.now())
                .setParameter(4, venda.total().valor())
                .setParameter(5, venda.desconto().valor())
                .setParameter(6, venda.lucro().valor())
                .setParameter(7, venda.prejuizo().valor())
                .setParameter(8, venda.total().valor())
                .setParameter(9, venda.quantidadeItens().valor())
                .executeUpdate();
        for (ItemVenda item : itens) {
            registrarItemVendido(venda, item);
        }
    }

    private void registrarItemVendido(Venda venda, ItemVenda item) {
        entityManager.createNativeQuery("""
                insert into dashboard_produto_diario (
                    id, empresa_id, loja_id, referencia_date, produto_id, produto_nome, quantidade_vendida,
                    faturamento, ultima_atualizacao, versao
                ) values (
                    gen_random_uuid(), cast(? as uuid), cast(? as uuid), cast(? as date), cast(? as uuid), ?,
                    cast(? as numeric), cast(? as numeric), now(), 1
                ) on conflict (empresa_id, loja_id, referencia_date,
                    (coalesce(produto_id, '00000000-0000-0000-0000-000000000000'::uuid)), produto_nome)
                do update set
                    quantidade_vendida = dashboard_produto_diario.quantidade_vendida + excluded.quantidade_vendida,
                    faturamento = dashboard_produto_diario.faturamento + excluded.faturamento,
                    ultima_atualizacao = now(),
                    versao = dashboard_produto_diario.versao + 1
                """)
                .setParameter(1, venda.empresaId().valor())
                .setParameter(2, venda.lojaId().valor())
                .setParameter(3, LocalDate.now())
                .setParameter(4, item.produtoId() == null ? null : item.produtoId().valor())
                .setParameter(5, item.produtoNome())
                .setParameter(6, item.quantidade().valor())
                .setParameter(7, item.subtotal().valor())
                .executeUpdate();
    }
}
