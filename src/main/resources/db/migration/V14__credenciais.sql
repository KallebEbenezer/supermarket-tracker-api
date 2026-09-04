create table credencial (
    id uuid primary key default gen_random_uuid(),
    email varchar(255) not null,
    senha_hash varchar(100) not null,
    usuario_id uuid not null references usuario(id) on update cascade on delete restrict,
    papel papel_usuario not null,
    ativo boolean not null default true,
    token_reset_senha varchar(120),
    expira_reset_em timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_credencial_email unique (email)
);
create index ix_credencial_token_reset on credencial (token_reset_senha) where token_reset_senha is not null;
