# Módulos

## Cadastro comercial

Responsável por empresas, lojas, usuários, clientes, caixas e produtos.

- Empresa: razão social, nome fantasia e CNPJ opcional.
- Loja: pertence a uma empresa e possui código único por empresa.
- Produto: código de barras único por empresa, preço, estoque mínimo e indicação de estoque negativo permitido.
- Usuário: e-mail único e identificador de autenticação externo.
- Cliente: associado a uma empresa; documento e e-mail são opcionais.

## Caixa

Um caixa pertence a uma loja. Uma sessão pode estar `ABERTO`, `FECHADO` ou `CANCELADO`; há um índice único parcial que impede duas sessões abertas para o mesmo caixa. A abertura e o fechamento validam valores não negativos.

## Estoque

Movimentações aceitas: `ENTRADA`, `SAIDA`, `VENDA`, `AJUSTE` e `CANCELAMENTO`. Ajustes exigem motivo. O banco calcula o saldo posterior de modo concorrente e impede saldo negativo quando o produto não o permite.

## Vendas e pagamentos

Uma venda finalizada contém itens e pelo menos um pagamento. O checkout calcula subtotal, custo, lucro ou prejuízo, valida que a soma dos pagamentos é igual ao total e finaliza a venda como `PAGA`. PIX e cartão usam gateways; os adapters atuais são mocks aprovados para desenvolvimento.

## Dashboard

O dashboard é atualizado após venda paga. Há cache diário de vendas e agregação diária por produto, que alimentam resumo mensal, últimas vendas, ranking de produtos e estoque baixo.
