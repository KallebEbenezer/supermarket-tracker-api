create table loja (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    codigo varchar(30) not null,
    nome varchar(160) not null,
    cnpj char(14),
    endereco jsonb,
    telefone varchar(30),
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_loja_empresa_codigo unique (empresa_id, codigo)
);
create index ix_loja_empresa_status on loja (empresa_id, status);

create table caixa (
    id uuid primary key default gen_random_uuid(),
    loja_id uuid not null references loja(id) on update cascade on delete restrict,
    codigo varchar(30) not null,
    nome varchar(100) not null,
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_caixa_loja_codigo unique (loja_id, codigo)
);
create index ix_caixa_loja_status on caixa (loja_id, status);

create table sessao_caixa (
    id uuid primary key default gen_random_uuid(),
    caixa_id uuid not null references caixa(id) on update cascade on delete restrict,
    usuario_abertura_id uuid not null references usuario(id) on update cascade on delete restrict,
    usuario_fechamento_id uuid references usuario(id) on update cascade on delete restrict,
    aberto_em timestamptz not null default now(),
    fechado_em timestamptz,
    valor_abertura numeric(14,2) not null default 0,
    valor_fechamento_informado numeric(14,2),
    status varchar(20) not null default 'ABERTO',
    observacao text,
    constraint ck_sessao_caixa_valor_abertura check (valor_abertura >= 0),
    constraint ck_sessao_caixa_status check (status in ('ABERTO', 'FECHADO', 'CANCELADO')),
    constraint ck_sessao_caixa_fechamento check (status <> 'ABERTO' or fechado_em is null)
);
create unique index uk_sessao_caixa_aberta on sessao_caixa (caixa_id) where status = 'ABERTO';
create index ix_sessao_caixa_aberto_em on sessao_caixa (aberto_em desc);
