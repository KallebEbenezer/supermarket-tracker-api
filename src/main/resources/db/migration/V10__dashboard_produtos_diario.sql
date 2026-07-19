create table dashboard_produto_diario (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    loja_id uuid not null references loja(id) on update cascade on delete restrict,
    referencia_date date not null,
    produto_id uuid references produto(id) on update cascade on delete set null,
    produto_nome varchar(200) not null,
    quantidade_vendida numeric(14,3) not null default 0,
    faturamento numeric(14,2) not null default 0,
    ultima_atualizacao timestamptz not null default now(),
    versao bigint not null default 1,
    constraint ck_dashboard_produto_quantidade check (quantidade_vendida >= 0),
    constraint ck_dashboard_produto_faturamento check (faturamento >= 0)
);

create unique index uk_dashboard_produto_periodo on dashboard_produto_diario (
    empresa_id, loja_id, referencia_date,
    coalesce(produto_id, '00000000-0000-0000-0000-000000000000'::uuid), produto_nome
);
create index ix_dashboard_produto_ranking on dashboard_produto_diario
    (empresa_id, loja_id, referencia_date desc, quantidade_vendida desc);
