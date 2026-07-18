create function set_updated_at() returns trigger language plpgsql as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create trigger tg_empresa_updated_at before update on empresa for each row execute function set_updated_at();
create trigger tg_usuario_updated_at before update on usuario for each row execute function set_updated_at();
create trigger tg_empresa_usuario_updated_at before update on empresa_usuario for each row execute function set_updated_at();
create trigger tg_loja_updated_at before update on loja for each row execute function set_updated_at();
create trigger tg_caixa_updated_at before update on caixa for each row execute function set_updated_at();
create trigger tg_conta_bancaria_updated_at before update on conta_bancaria for each row execute function set_updated_at();
create trigger tg_produto_updated_at before update on produto for each row execute function set_updated_at();
create trigger tg_cliente_updated_at before update on cliente for each row execute function set_updated_at();
create trigger tg_venda_updated_at before update on venda for each row execute function set_updated_at();
create trigger tg_pagamento_updated_at before update on pagamento for each row execute function set_updated_at();

-- A aplicação deve chamar esta função em vez de alterar produto.estoque_atual diretamente.
create function registrar_movimentacao_estoque(
    p_empresa_id uuid,
    p_loja_id uuid,
    p_produto_id uuid,
    p_usuario_id uuid,
    p_tipo tipo_movimentacao_estoque,
    p_quantidade numeric,
    p_venda_id uuid default null,
    p_item_venda_id uuid default null,
    p_custo_unitario numeric default null,
    p_motivo text default null,
    p_referencia_externa varchar default null
) returns uuid language plpgsql as $$
declare
    v_anterior numeric(14,3);
    v_posterior numeric(14,3);
    v_permite_negativo boolean;
    v_movimentacao_id uuid;
begin
    select estoque_atual, permite_estoque_negativo
      into v_anterior, v_permite_negativo
      from produto
     where id = p_produto_id and empresa_id = p_empresa_id
       for update;

    if not found then
        raise exception 'Produto não encontrado para a empresa informada';
    end if;
    if p_quantidade <= 0 then
        raise exception 'Quantidade deve ser positiva';
    end if;

    v_posterior := v_anterior + case when p_tipo in ('ENTRADA', 'CANCELAMENTO') then p_quantidade else -p_quantidade end;
    if v_posterior < 0 and not v_permite_negativo then
        raise exception 'Estoque insuficiente';
    end if;

    insert into estoque_movimentacao (
        empresa_id, loja_id, produto_id, usuario_id, venda_id, item_venda_id,
        tipo, quantidade, estoque_anterior, estoque_posterior, custo_unitario,
        motivo, referencia_externa
    ) values (
        p_empresa_id, p_loja_id, p_produto_id, p_usuario_id, p_venda_id, p_item_venda_id,
        p_tipo, p_quantidade, v_anterior, v_posterior, p_custo_unitario,
        p_motivo, p_referencia_externa
    ) returning id into v_movimentacao_id;

    update produto set estoque_atual = v_posterior where id = p_produto_id;
    return v_movimentacao_id;
end;
$$;
