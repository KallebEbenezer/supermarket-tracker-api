create table venda (
    id uuid primary key default gen_random_uuid(),
    empresa_id uuid not null references empresa(id) on update cascade on delete restrict,
    loja_id uuid not null references loja(id) on update cascade on delete restrict,
    sessao_caixa_id uuid references sessao_caixa(id) on update cascade on delete restrict,
    usuario_id uuid not null references usuario(id) on update cascade on delete restrict,
    cliente_id uuid references cliente(id) on update cascade on delete restrict,
    numero bigint not null,
    subtotal numeric(14,2) not null,
    desconto numeric(14,2) not null default 0,
    acrescimo numeric(14,2) not null default 0,
    total numeric(14,2) not null,
    custo_total numeric(14,2) not null default 0,
    lucro numeric(14,2) not null default 0,
    prejuizo numeric(14,2) not null default 0,
    quantidade_itens numeric(14,3) not null,
    status status_venda not null default 'ABERTA',
    finalizada_em timestamptz,
    cancelada_em timestamptz,
    motivo_cancelamento text,
    observacao text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_venda_numero unique (empresa_id, numero),
    constraint ck_venda_valores check (subtotal >= 0 and desconto >= 0 and acrescimo >= 0 and total >= 0),
    constraint ck_venda_total check (total = subtotal - desconto + acrescimo),
    constraint ck_venda_quantidade check (quantidade_itens > 0),
    constraint ck_venda_cancelamento check (status <> 'CANCELADA' or cancelada_em is not null)
);
create index ix_venda_loja_data on venda (loja_id, created_at desc);
create index ix_venda_empresa_status_data on venda (empresa_id, status, created_at desc);
create index ix_venda_empresa_finalizada on venda (empresa_id, finalizada_em desc) where status = 'PAGA';
create index ix_venda_usuario_data on venda (usuario_id, created_at desc);
create index ix_venda_cliente_data on venda (cliente_id, created_at desc) where cliente_id is not null;

create table item_venda (
    id uuid primary key default gen_random_uuid(),
    venda_id uuid not null references venda(id) on update cascade on delete restrict,
    produto_id uuid references produto(id) on update cascade on delete set null,
    item_numero integer not null,
    produto_nome varchar(200) not null,
    codigo_barras varchar(100),
    unidade_medida varchar(10) not null,
    quantidade numeric(14,3) not null,
    preco_unitario numeric(14,2) not null,
    preco_compra_unitario numeric(14,2) not null,
    desconto numeric(14,2) not null default 0,
    acrescimo numeric(14,2) not null default 0,
    subtotal numeric(14,2) not null,
    custo_total numeric(14,2) not null,
    lucro numeric(14,2) not null default 0,
    created_at timestamptz not null default now(),
    constraint uk_item_venda_numero unique (venda_id, item_numero),
    constraint ck_item_venda_numero check (item_numero > 0),
    constraint ck_item_venda_quantidade check (quantidade > 0),
    constraint ck_item_venda_precos check (preco_unitario >= 0 and preco_compra_unitario >= 0 and subtotal >= 0)
);
create index ix_item_venda_venda on item_venda (venda_id);
create index ix_item_venda_produto_data on item_venda (produto_id, created_at desc) where produto_id is not null;
