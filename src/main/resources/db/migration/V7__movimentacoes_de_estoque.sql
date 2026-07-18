create table estoque_movimentacao (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    loja_id uuid not null references loja(id) on update cascade on delete restrict,
    produto_id uuid not null references produto(id) on update cascade on delete restrict,
    usuario_id uuid not null references usuario(id) on update cascade on delete restrict,
    venda_id uuid references venda(id) on update cascade on delete restrict,
    item_venda_id uuid references item_venda(id) on update cascade on delete restrict,
    tipo tipo_movimentacao_estoque not null,
    quantidade numeric(14,3) not null,
    estoque_anterior numeric(14,3) not null,
    estoque_posterior numeric(14,3) not null,
    custo_unitario numeric(14,2),
    motivo text,
    referencia_externa varchar(100),
    created_at timestamptz not null default now(),
    constraint ck_estoque_movimentacao_quantidade check (quantidade > 0),
    constraint ck_estoque_movimentacao_saldo check (estoque_anterior <> estoque_posterior),
    constraint ck_estoque_movimentacao_ajuste check (tipo <> 'AJUSTE' or motivo is not null)
);
create index ix_estoque_movimentacao_produto_data on estoque_movimentacao (produto_id, created_at desc);
create index ix_estoque_movimentacao_loja_data on estoque_movimentacao (loja_id, created_at desc);
create index ix_estoque_movimentacao_empresa_tipo_data on estoque_movimentacao (empresa_id, tipo, created_at desc);
create index ix_estoque_movimentacao_venda on estoque_movimentacao (venda_id) where venda_id is not null;
