-- Garante que cada venda produza no máximo uma movimentação de estoque do tipo VENDA.
-- Evita duplicação caso o webhook PIX seja entregue/processado mais de uma vez.
create unique index ux_estoque_movimentacao_venda_ven
    on estoque_movimentacao (venda_id)
    where tipo = 'VENDA' and venda_id is not null;
