package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.repository.DashboardRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepositoryJpaAdapter implements DashboardRepository {
    private final EntityManager entityManager;

    public DashboardRepositoryJpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void registrarVendaPaga(Venda venda) {
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
    }
}
