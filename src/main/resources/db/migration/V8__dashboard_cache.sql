create table dashboard_cache_diario (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    loja_id uuid references loja(id) on update cascade on delete restrict,
    referencia_date date not null,
    vendas_quantidade integer not null default 0,
    faturamento numeric(14,2) not null default 0,
    descontos numeric(14,2) not null default 0,
    lucro numeric(14,2) not null default 0,
    prejuizo numeric(14,2) not null default 0,
    ticket_medio numeric(14,2) not null default 0,
    produtos_vendidos numeric(14,3) not null default 0,
    ultima_atualizacao timestamptz not null default now(),
    versao bigint not null default 1,
    constraint ck_dashboard_vendas check (vendas_quantidade >= 0),
    constraint ck_dashboard_produtos check (produtos_vendidos >= 0)
);
create unique index uk_dashboard_cache_periodo on dashboard_cache_diario (empresa_id, coalesce(loja_id, '00000000-0000-0000-0000-000000000000'::uuid), referencia_date);
create index ix_dashboard_empresa_data on dashboard_cache_diario (empresa_id, referencia_date desc);
create index ix_dashboard_loja_data on dashboard_cache_diario (loja_id, referencia_date desc) where loja_id is not null;
