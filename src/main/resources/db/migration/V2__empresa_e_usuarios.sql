create table empresa (
    id uuid primary key default gen_random_uuid(),
    razao_social varchar(160) not null,
    nome_fantasia varchar(160) not null,
    cnpj char(14),
    status status_empresa not null default 'ATIVA',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_empresa_cnpj_formato check (cnpj is null or cnpj ~ '^[0-9]{14}$')
);
create unique index uk_empresa_cnpj on empresa (cnpj) where cnpj is not null;
create index ix_empresa_nome_fantasia on empresa (nome_fantasia);

create table usuario (
    id uuid primary key default gen_random_uuid(),
    auth_user_id uuid not null,
    nome varchar(160) not null,
    email varchar(255) not null,
    telefone varchar(30),
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_usuario_auth_user unique (auth_user_id),
    constraint uk_usuario_email unique (email)
);
create index ix_usuario_nome on usuario (nome);

create table empresa_usuario (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    usuario_id uuid not null references usuario(id) on update cascade on delete restrict,
    papel papel_usuario not null,
    status status_ativo not null default 'ATIVO',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_empresa_usuario unique (empresa_id, usuario_id)
);
create index ix_empresa_usuario_usuario_status on empresa_usuario (usuario_id, status);
