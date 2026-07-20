# Exemplos de API

Os exemplos usam `http://localhost:8080` e UUIDs ilustrativos. As rotas de negócio usam Basic Auth; informe `-u user:<senha-exibida-no-log>` ao `curl`. No Swagger, clique em **Authorize**, selecione `basicAuth` e informe as mesmas credenciais.

```bash
export API_URL=http://localhost:8080
export AUTH='user:sua-senha-temporaria'
```

## Criar empresa

```bash
curl -u "$AUTH" -X POST "$API_URL/api/v1/empresas" \
  -H 'Content-Type: application/json' \
  -d '{
    "razaoSocial": "Mercado Exemplo Ltda",
    "nomeFantasia": "Mercado Exemplo",
    "cnpj": "12345678000190"
  }'
```

Resposta HTTP `201`:

```json
{
  "timestamp": "2026-07-19T12:00:00Z",
  "status": 200,
  "success": true,
  "data": {
    "id": "4e1e8ceb-9f77-4f2f-859e-5663694c4778",
    "razaoSocial": "Mercado Exemplo Ltda",
    "nomeFantasia": "Mercado Exemplo",
    "cnpj": "12345678000190",
    "status": "ATIVA"
  }
}
```

> O código HTTP é a fonte de verdade. O campo `status` do envelope de sucesso é atualmente `200`, inclusive para operações HTTP `201`.

## Criar loja e produto

```bash
export EMPRESA_ID=4e1e8ceb-9f77-4f2f-859e-5663694c4778

curl -u "$AUTH" -X POST "$API_URL/api/v1/lojas" \
  -H 'Content-Type: application/json' \
  -d "{\"empresaId\": \"$EMPRESA_ID\", \"codigo\": \"CENTRO\", \"nome\": \"Loja Centro\", \"cnpj\": null, \"telefone\": \"85999990000\"}"

curl -u "$AUTH" -X POST "$API_URL/api/v1/produtos" \
  -H 'Content-Type: application/json' \
  -d "{
    \"empresaId\": \"$EMPRESA_ID\",
    \"codigoBarras\": \"7891000100103\",
    \"nome\": \"Arroz Tipo 1 5kg\",
    \"unidadeMedida\": \"UN\",
    \"precoCompra\": 20.00,
    \"precoVenda\": 28.50,
    \"estoqueMinimo\": 5.000,
    \"permiteEstoqueNegativo\": false
  }"
```

Exemplo de resposta de produto:

```json
{
  "timestamp": "2026-07-19T12:01:00Z",
  "status": 200,
  "success": true,
  "data": {
    "id": "de89eb5e-8a52-4e62-81e2-8522b2a12331",
    "empresaId": "4e1e8ceb-9f77-4f2f-859e-5663694c4778",
    "codigoBarras": "7891000100103",
    "nome": "Arroz Tipo 1 5kg",
    "precoVenda": 28.50,
    "estoqueAtual": 0.000,
    "status": "ATIVO"
  }
}
```

## Registrar entrada de estoque

```bash
curl -u "$AUTH" -X POST "$API_URL/api/v1/estoque/movimentacoes" \
  -H 'Content-Type: application/json' \
  -d '{
    "empresaId": "4e1e8ceb-9f77-4f2f-859e-5663694c4778",
    "lojaId": "7e118ceb-9f77-4f2f-859e-5663694c4778",
    "produtoId": "de89eb5e-8a52-4e62-81e2-8522b2a12331",
    "usuarioId": "30bf7046-c152-481d-9017-112d2f864659",
    "tipo": "ENTRADA",
    "quantidade": 20.000,
    "custoUnitario": 20.00,
    "motivo": "Recebimento do fornecedor",
    "referenciaExterna": "NF-1234"
  }'
```

## Finalizar venda com dinheiro

```bash
curl -u "$AUTH" -X POST "$API_URL/api/v1/vendas/finalizar" \
  -H 'Content-Type: application/json' \
  -d '{
    "empresaId": "4e1e8ceb-9f77-4f2f-859e-5663694c4778",
    "lojaId": "7e118ceb-9f77-4f2f-859e-5663694c4778",
    "usuarioId": "30bf7046-c152-481d-9017-112d2f864659",
    "numero": 1001,
    "desconto": 0.00,
    "acrescimo": 0.00,
    "itens": [{
      "produtoId": "de89eb5e-8a52-4e62-81e2-8522b2a12331",
      "numero": 1,
      "produtoNome": "Arroz Tipo 1 5kg",
      "codigoBarras": "7891000100103",
      "unidadeMedida": "UN",
      "quantidade": 2.000,
      "precoUnitario": 28.50,
      "precoCompraUnitario": 20.00,
      "desconto": 0.00,
      "acrescimo": 0.00
    }],
    "pagamentos": [{
      "tipo": "DINHEIRO",
      "valor": 57.00
    }]
  }'
```

Resposta resumida:

```json
{
  "timestamp": "2026-07-19T12:03:00Z",
  "status": 200,
  "success": true,
  "data": {
    "id": "8716b581-eb3d-44e5-97df-eac8905d08d7",
    "numero": 1001,
    "total": 57.00,
    "quantidadeItens": 2.000,
    "status": "PAGA"
  }
}
```

Para PIX, informe `"tipo": "PIX"`. Para cartão, informe `"tipo": "CARTAO"`, `"modalidadeCartao": "DEBITO"` ou `"CREDITO"`, e `"parcelas"`; débito aceita somente uma parcela.

## Consultar dashboard

```bash
curl -u "$AUTH" "$API_URL/api/v1/dashboard?empresaId=4e1e8ceb-9f77-4f2f-859e-5663694c4778&limite=10"
```

## Resposta de erro

Uma regra de negócio violada retorna HTTP `422`:

```json
{
  "timestamp": "2026-07-19T12:04:00Z",
  "status": 422,
  "success": false,
  "code": "BUSINESS_RULE_VIOLATION",
  "message": "O total dos pagamentos deve ser igual ao total da venda",
  "path": "/api/v1/vendas/finalizar",
  "traceId": "f7c243ed-bb8e-4f34-ad8a-4f34cee23baf",
  "validationErrors": {}
}
```

Envie `X-Trace-Id` para correlacionar logs; se omitido, a API devolve um identificador gerado no mesmo cabeçalho.
