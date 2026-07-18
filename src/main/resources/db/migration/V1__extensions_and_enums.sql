create extension if not exists pgcrypto;
create extension if not exists pg_trgm;

create type status_empresa as enum ('ATIVA', 'INATIVA', 'BLOQUEADA');
create type status_ativo as enum ('ATIVO', 'INATIVO');
create type papel_usuario as enum ('PROPRIETARIO', 'ADMIN', 'GERENTE', 'CAIXA', 'ESTOQUISTA', 'CONSULTA');
create type tipo_conta_bancaria as enum ('CORRENTE', 'POUPANCA', 'PAGAMENTO');
create type tipo_movimentacao_estoque as enum ('ENTRADA', 'SAIDA', 'VENDA', 'AJUSTE', 'CANCELAMENTO');
create type status_venda as enum ('ABERTA', 'AGUARDANDO_PAGAMENTO', 'PAGA', 'CANCELADA', 'ESTORNADA');
create type tipo_pagamento as enum ('DINHEIRO', 'PIX', 'CARTAO', 'CREDITO_LOJA', 'VALE', 'OUTRO');
create type status_pagamento as enum ('PENDENTE', 'PROCESSANDO', 'APROVADO', 'RECUSADO', 'CANCELADO', 'ESTORNADO');
create type modalidade_cartao as enum ('DEBITO', 'CREDITO');
