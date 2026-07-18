create table pagamento (
    id uuid primary key default gen_random_uuid(),
    venda_id uuid not null references venda(id) on update cascade on delete restrict,
    conta_bancaria_id uuid references conta_bancaria(id) on update cascade on delete restrict,
    tipo tipo_pagamento not null,
    valor numeric(14,2) not null,
    status status_pagamento not null default 'PENDENTE',
    recebido_em timestamptz,
    referencia varchar(120),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_pagamento_valor check (valor > 0)
);
create index ix_pagamento_venda_status on pagamento (venda_id, status);
create index ix_pagamento_financeiro on pagamento (tipo, status, recebido_em desc);
create index ix_pagamento_conta_data on pagamento (conta_bancaria_id, recebido_em desc) where conta_bancaria_id is not null;

create table pagamento_pix (
    pagamento_id uuid primary key references pagamento(id) on update cascade on delete restrict,
    qr_code text,
    copia_cola text,
    txid varchar(100),
    gateway varchar(80),
    gateway_transacao_id varchar(150),
    status_gateway varchar(50),
    pago_em timestamptz
);
create unique index uk_pagamento_pix_txid on pagamento_pix (txid) where txid is not null;
create unique index uk_pagamento_pix_gateway on pagamento_pix (gateway, gateway_transacao_id) where gateway is not null and gateway_transacao_id is not null;
create index ix_pagamento_pix_status_data on pagamento_pix (status_gateway, pago_em desc);

create table pagamento_cartao (
    pagamento_id uuid primary key references pagamento(id) on update cascade on delete restrict,
    modalidade modalidade_cartao not null,
    parcelas smallint not null default 1,
    bandeira varchar(30),
    nsu varchar(80),
    codigo_autorizacao varchar(80),
    terminal_id varchar(80),
    gateway varchar(80),
    gateway_transacao_id varchar(150),
    status_gateway varchar(50),
    constraint ck_pagamento_cartao_parcelas check (parcelas >= 1)
);
create unique index uk_pagamento_cartao_gateway on pagamento_cartao (gateway, gateway_transacao_id) where gateway is not null and gateway_transacao_id is not null;
create index ix_pagamento_cartao_nsu on pagamento_cartao (nsu) where nsu is not null;
