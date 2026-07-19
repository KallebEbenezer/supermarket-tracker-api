create index ix_venda_dashboard_ultimas on venda (empresa_id, loja_id, finalizada_em desc)
    where status = 'PAGA';

create index ix_dashboard_cache_consulta on dashboard_cache_diario
    (empresa_id, loja_id, referencia_date desc);
