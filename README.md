# CampusGigs

API REST da plataforma de freelas entre alunos de uma universidade. Um aluno se cadastra e publica um serviço; outro aluno, autenticado, contrata esse serviço.

Stack: Spring Boot · Spring Security (JWT) · Flyway · PostgreSQL · Docker · HttpExchange (ViaCEP)

## Como executar

Pré-requisitos: JDK 17 e Docker.

```bash
./gradlew bootRun
```

O `spring-boot-docker-compose` sobe automaticamente o container do PostgreSQL definido em `compose.yaml` e configura o datasource da aplicação. As migrations do Flyway rodam no start e criam o schema (`usuario`, `servico`, `contratacao`).

A aplicação sobe em `http://localhost:8080`.

## Papéis e regras de autorização

- Qualquer usuário autenticado pode publicar e contratar serviços.
- Um usuário só edita ou encerra os próprios serviços — exceto um `ADMIN`, que pode encerrar qualquer um.
- Um usuário não pode contratar o próprio serviço.
- Um usuário não autenticado não acessa nenhuma operação que altera dados (apenas `POST /api/auth/**` e `GET /api/servicos` são públicos).

O papel do usuário recém-cadastrado é sempre `USER`; promover um usuário a `ADMIN` é feito diretamente no banco.

## Exemplo de uso

### 1. Cadastro

```bash
curl -X POST http://localhost:8080/api/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria Silva","email":"maria@fiap.com.br","senha":"senha123","cep":"01310-100"}'
```

### 2. Login (obter token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@fiap.com.br","senha":"senha123"}'
```

Resposta:

```json
{"token": "eyJhbGciOiJIUzI1NiJ9..."}
```

### 3. Chamada autenticada — publicar um serviço

```bash
curl -X POST http://localhost:8080/api/servicos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"titulo":"Aula de Cálculo 1","descricao":"Reforço para provas","categoria":"Aulas","preco":50.00}'
```

### 4. Contratar um serviço (usuário diferente do prestador)

```bash
curl -X POST http://localhost:8080/api/contratacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-do-contratante>" \
  -d '{"servicoId":1}'
```

## Tratamento de erros

Toda violação de validação, autenticação, autorização ou regra de negócio retorna um corpo padronizado:

```json
{
  "timestamp": "2026-09-15T10:00:00",
  "status": 422,
  "erro": "Unprocessable Entity",
  "mensagem": "Esse serviço não está ativo e não pode ser contratado",
  "caminho": "/api/contratacoes"
}
```

Se o CEP informado no cadastro não existir, a API responde `400` com uma mensagem clara; se o serviço externo de CEP estiver fora do ar, responde `503`.
