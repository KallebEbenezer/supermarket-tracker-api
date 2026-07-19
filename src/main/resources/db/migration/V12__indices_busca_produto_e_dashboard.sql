-- A busca por nome normaliza a coluna com lower(...); o índice funcional evita
-- varredura sequencial em pesquisas parciais case-insensitive.
create index ix_produto_empresa_nome_lower_trgm on produto
    using gin (lower(nome) gin_trgm_ops);

-- Ranking consolidado por empresa, usado quando o dashboard não está filtrado por loja.
create index ix_dashboard_produto_empresa_data_ranking on dashboard_produto_diario
    (empresa_id, referencia_date desc, quantidade_vendida desc);
