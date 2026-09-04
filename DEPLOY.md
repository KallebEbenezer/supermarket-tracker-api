# Deploy no Render - Passo a Passo

## 1. Preparar o Repositório GitHub

Se ainda não tiver, crie um repositório no GitHub e faça push do projeto:

```bash
cd /home/kalleb-rodrigues/Documents/supermarket-tracker
git init
git add .
git commit -m "Initial commit - Supermarket Tracker"
git branch -M main
git remote add origin https://github.com/SEU-USUARIO/supermarket-tracker.git
git push -u origin main
```

## 2. Criar Conta no Render

1. Acesse https://render.com
2. Clique em **"Get Started"** ou **"Sign Up"**
3. Escolha **"Sign up with GitHub"**
4. Autorize o Render a acessar seus repositórios

## 3. Criar o Web Service

1. No dashboard do Render, clique em **"New +"** → **"Web Service"**
2. Conecte seu repositório `supermarket-tracker`
3. Configure:
   - **Name:** `supermarket-tracker-api`
   - **Region:** Oregon (US West)
   - **Branch:** `main`
   - **Root Directory:** `backend`
   - **Runtime:** Docker
   - **Plan:** Free

## 4. Configurar Variáveis de Ambiente

No painel do Render, vá em **"Environment"** e adicione:

### Obrigatórias (copie do seu .env local):

```
SPRING_PROFILES_ACTIVE=prod
DB_HOST=aws-1-us-west-2.pooler.supabase.com
DB_PORT=5432
DB_NAME=postgres
DB_USERNAME=postgres.einombkxnxtoyttrcryc
DB_PASSWORD=supermarket_tracker@
PIX_MERCADOPAGO_ACCESS_TOKEN=APP_USR-4028230563443766-072519-5c7e4eb9ca7e75188774cee46c53dd2f-1384325652
MERCADOPAGO_ACCESS_TOKEN=APP_USR-4028230563443766-072519-5c7e4eb9ca7e75188774cee46c53dd2f-1384325652
PIX_MOCK_ENABLED=false
```

### Gerar automaticamente (clique em "Generate"):

```
APP_JWT_SECRET
MERCADOPAGO_WEBHOOK_SECRET
```

### Opcionais (email):

```
EMAIL_PROVIDER=smtp
EMAIL_FROM=noreply@supermarkettracker.com.br
```

## 5. Deploy

1. Clique em **"Create Web Service"**
2. Aguarde o build (5-10 minutos na primeira vez)
3. Quando aparecer "Live" com o status verde, anote a URL:
   ```
   https://supermarket-tracker-api.onrender.com
   ```

## 6. Testar o Backend

Abra no navegador:
```
https://supermarket-tracker-api.onrender.com/actuator/health
```

Deve retornar: `{"status":"UP"}`

## 7. Configurar Webhook no Mercado Pago

1. Acesse https://www.mercadopago.com.br/developers
2. Vá em **Suas integrações** → selecione seu app
3. Em **Webhooks**, adicione:
   ```
   URL: https://supermarket-tracker-api.onrender.com/api/v1/webhooks/pix
   Eventos: Pagamentos
   ```
4. Salve

## 8. Atualizar o App (Flutter)

Edite `app/lib/core/environment/.env.dev`:

```
API_BASE_URL=https://supermarket-tracker-api.onrender.com
```

Rebuild o app:
```bash
cd app
flutter build apk --flavor dev --debug
adb install -r build/app/outputs/flutter-apk/app-dev-debug.apk
```

## 9. Testar PIX Completo

1. Abra o app
2. Finalize uma venda com PIX
3. Pague o PIX no app do banco
4. **O app deve atualizar automaticamente** quando o pagamento for confirmado! ✅

---

## Troubleshooting

### Build falhou no Render
- Verifique os logs de build
- Confirme que o `Dockerfile` está no diretório `backend/`

### API retorna 500
- Verifique as variáveis de ambiente (principalmente `DB_*`)
- Teste a conexão com o Supabase

### Webhook não funciona
- Confirme que a URL do webhook está correta no painel do MP
- Verifique os logs da API no Render quando fizer um pagamento

### App não atualiza após pagamento
- Confirme que o WebSocket conectou (logs do backend)
- Verifique se a `API_BASE_URL` do app aponta para a URL do Render

---

## Custos

- **Render Free Plan**: 750h/mês grátis
- **Supabase**: 500MB PostgreSQL grátis
- **Total: R$ 0,00** 🎉

> ⚠️ **Limitação do plano gratuito**: o serviço "hiberna" após 15 min de inatividade. A primeira requisição após hibernar demora ~30s para acordar.
