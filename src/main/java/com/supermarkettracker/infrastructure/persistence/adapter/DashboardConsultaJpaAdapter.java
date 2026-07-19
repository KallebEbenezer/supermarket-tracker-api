package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.Dashboard;
import com.supermarkettracker.domain.model.DashboardEstoqueBaixo;
import com.supermarkettracker.domain.model.DashboardProdutoMaisVendido;
import com.supermarkettracker.domain.model.DashboardResumo;
import com.supermarkettracker.domain.model.DashboardUltimaVenda;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.DashboardConsultaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardConsultaJpaAdapter implements DashboardConsultaRepository {
    private final EntityManager entityManager;

    public DashboardConsultaJpaAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Dashboard consultar(Identificador empresaId, Identificador lojaId, int limite) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        return new Dashboard(resumo(empresaId.valor(), id(lojaId), inicioMes, hoje),
                ultimasVendas(empresaId.valor(), id(lojaId), limite),
                produtosMaisVendidos(empresaId.valor(), id(lojaId), inicioMes, hoje, limite),
                estoqueBaixo(empresaId.valor(), limite));
    }

    private DashboardResumo resumo(UUID empresaId, UUID lojaId, LocalDate inicioMes, LocalDate hoje) {
        String sql = """
                select coalesce(sum(faturamento), 0), coalesce(sum(lucro), 0), coalesce(sum(prejuizo), 0),
                       coalesce(sum(faturamento) / nullif(sum(vendas_quantidade), 0), 0),
                       coalesce(sum(vendas_quantidade) filter (where referencia_date = ?), 0),
                       coalesce(sum(vendas_quantidade), 0)
                  from dashboard_cache_diario
                 where empresa_id = ? and referencia_date between ? and ?
                """ + filtroLoja(lojaId);
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, hoje);
        query.setParameter(2, empresaId);
        query.setParameter(3, inicioMes);
        query.setParameter(4, hoje);
        adicionarLoja(query, lojaId, 5);
        Object[] row = (Object[]) query.getSingleResult();
        return new DashboardResumo(decimal(row[0]), decimal(row[1]), decimal(row[2]), decimal(row[3]),
                inteiro(row[4]), inteiro(row[5]));
    }

    private List<DashboardUltimaVenda> ultimasVendas(UUID empresaId, UUID lojaId, int limite) {
        String sql = """
                select id, numero, total, finalizada_em
                  from venda
                 where empresa_id = ? and status = 'PAGA'
                """ + filtroLoja(lojaId) + " order by finalizada_em desc limit ?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, empresaId);
        int indiceLimite = adicionarLoja(query, lojaId, 2);
        query.setParameter(indiceLimite, limite);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(row -> new DashboardUltimaVenda((UUID) row[0], ((Number) row[1]).longValue(),
                decimal(row[2]), instante(row[3]))).toList();
    }

    private List<DashboardProdutoMaisVendido> produtosMaisVendidos(UUID empresaId, UUID lojaId,
            LocalDate inicioMes, LocalDate hoje, int limite) {
        String sql = """
                select produto_id, produto_nome, sum(quantidade_vendida), sum(faturamento)
                  from dashboard_produto_diario
                 where empresa_id = ? and referencia_date between ? and ?
                """ + filtroLoja(lojaId) + " group by produto_id, produto_nome order by sum(quantidade_vendida) desc, produto_nome limit ?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, empresaId);
        query.setParameter(2, inicioMes);
        query.setParameter(3, hoje);
        int indiceLimite = adicionarLoja(query, lojaId, 4);
        query.setParameter(indiceLimite, limite);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(row -> new DashboardProdutoMaisVendido((UUID) row[0], (String) row[1],
                decimal(row[2]), decimal(row[3]))).toList();
    }

    private List<DashboardEstoqueBaixo> estoqueBaixo(UUID empresaId, int limite) {
        Query query = entityManager.createNativeQuery("""
                select id, nome, estoque_atual, estoque_minimo, unidade_medida
                  from produto
                 where empresa_id = ? and status = 'ATIVO' and estoque_atual <= estoque_minimo
                 order by estoque_atual asc, nome asc
                 limit ?
                """);
        query.setParameter(1, empresaId);
        query.setParameter(2, limite);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(row -> new DashboardEstoqueBaixo((UUID) row[0], (String) row[1], decimal(row[2]),
                decimal(row[3]), (String) row[4])).toList();
    }

    private String filtroLoja(UUID lojaId) {
        return lojaId == null ? "" : " and loja_id = ?";
    }

    private int adicionarLoja(Query query, UUID lojaId, int indice) {
        if (lojaId != null) {
            query.setParameter(indice, lojaId);
            return indice + 1;
        }
        return indice;
    }

    private UUID id(Identificador identificador) {
        return identificador == null ? null : identificador.valor();
    }

    private BigDecimal decimal(Object value) {
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    private int inteiro(Object value) {
        return ((Number) value).intValue();
    }

    private Instant instante(Object value) {
        if (value instanceof Instant instant) return instant;
        if (value instanceof OffsetDateTime data) return data.toInstant();
        return ((java.sql.Timestamp) value).toInstant();
    }
}
