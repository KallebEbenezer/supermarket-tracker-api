create table conta_bancaria (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    banco_codigo varchar(10) not null,
    banco_nome varchar(100) not null,
    agencia varchar(20),
    conta varchar(30),
    tipo tipo_conta_bancaria not null,
    titular_nome varchar(160) not null,
    titular_documento varchar(18),
    chave_pix varchar(255),
    principal boolean not null default false,
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create unique index uk_conta_bancaria_principal on conta_bancaria (empresa_id) where principal and status = 'ATIVO';
create index ix_conta_bancaria_empresa_status on conta_bancaria (empresa_id, status);

create table categoria_produto (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    categoria_pai_id uuid references categoria_produto(id) on update cascade on delete restrict,
    nome varchar(100) not null,
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now()
);
create unique index uk_categoria_produto_nome on categoria_produto (empresa_id, coalesce(categoria_pai_id, '00000000-0000-0000-0000-000000000000'::uuid), nome);

create table produto (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    categoria_id uuid references categoria_produto(id) on update cascade on delete restrict,
    codigo_barras varchar(100) not null,
    sku varchar(80),
    nome varchar(200) not null,
    descricao text,
    imagem_url text,
    unidade_medida varchar(10) not null default 'UN',
    preco_compra numeric(14,2) not null default 0,
    preco_venda numeric(14,2) not null,
    estoque_atual numeric(14,3) not null default 0,
    estoque_minimo numeric(14,3) not null default 0,
    permite_estoque_negativo boolean not null default false,
    status varchar(20) not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_produto_codigo_barras unique (empresa_id, codigo_barras),
    constraint ck_produto_preco_compra check (preco_compra >= 0),
    constraint ck_produto_preco_venda check (preco_venda >= 0),
    constraint ck_produto_estoque_minimo check (estoque_minimo >= 0),
    constraint ck_produto_status check (status in ('ATIVO', 'INATIVO', 'ARQUIVADO'))
);
create unique index uk_produto_sku on produto (empresa_id, sku) where sku is not null;
create index ix_produto_scanner on produto (empresa_id, codigo_barras) where status = 'ATIVO';
create index ix_produto_empresa_nome on produto (empresa_id, nome);
create index ix_produto_nome_trgm on produto using gin (nome gin_trgm_ops);
create index ix_produto_busca_texto on produto using gin (to_tsvector('portuguese', nome || ' ' || coalesce(descricao, '')));
create index ix_produto_estoque_baixo on produto (empresa_id, estoque_atual) where status = 'ATIVO';

create table cliente (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    nome varchar(160) not null,
    cpf_cnpj varchar(18),
    email varchar(255),
    telefone varchar(30),
    data_nascimento date,
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create unique index uk_cliente_documento on cliente (empresa_id, cpf_cnpj) where cpf_cnpj is not null;
create index ix_cliente_empresa_nome on cliente (empresa_id, nome);
create index ix_cliente_telefone on cliente (empresa_id, telefone) where telefone is not null;
