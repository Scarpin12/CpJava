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
  "erro": "Unprocessable Content",
  "mensagem": "Esse serviço não está ativo e não pode ser contratado",
  "caminho": "/api/contratacoes"
}
```

Se o CEP informado no cadastro não existir, a API responde `400` com uma mensagem clara; se o serviço externo de CEP estiver fora do ar, responde `503`.

## Evidência de teste manual

Testes feitos rodando a aplicação de ponta a ponta (Postgres real via Docker), cobrindo o fluxo completo, as regras de negócio e um caso de acesso negado por papel.

**Cenário:** Maria se cadastra e publica um serviço; João se cadastra e tenta indevidamente encerrar o serviço de Maria (nem é dono, nem é ADMIN); depois contrata o serviço corretamente.

#### Cadastro com CEP válido (consulta automática de cidade/UF via ViaCEP)

```bash
$ curl -X POST http://localhost:8080/api/auth/cadastro -H "Content-Type: application/json" \
  -d '{"nome":"Maria Silva","email":"maria@fiap.com.br","senha":"senha123","cep":"01310-100"}'

{"id":1,"nome":"Maria Silva","email":"maria@fiap.com.br","cidade":"São Paulo","uf":"SP","papel":"USER"}
```

#### Cadastro com CEP inexistente → 400

```bash
$ curl -X POST http://localhost:8080/api/auth/cadastro -H "Content-Type: application/json" \
  -d '{"nome":"Teste Cep","email":"teste.cep@fiap.com.br","senha":"senha123","cep":"00000-000"}'

{"timestamp":"2026-09-15T19:55:49.985389","status":400,"erro":"Bad Request","mensagem":"O CEP 00000-000 não foi encontrado","caminho":"/api/auth/cadastro"}
```

#### Login com senha errada → 401

```bash
$ curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"maria@fiap.com.br","senha":"senhaErrada"}'

{"timestamp":"2026-09-15T19:56:04.261634","status":401,"erro":"Unauthorized","mensagem":"E-mail ou senha inválidos","caminho":"/api/auth/login"}
```

#### Login correto → token JWT

```bash
$ curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"maria@fiap.com.br","senha":"senha123"}'

{"token":"eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJtYXJpYUBmaWFwLmNvbS5iciIsInBhcGVsIjoiVVNFUiIs..."}
```

#### Publicar serviço sem token → 401

```bash
$ curl -X POST http://localhost:8080/api/servicos -H "Content-Type: application/json" \
  -d '{"titulo":"Aula de Cálculo 1","descricao":"Reforço para provas","categoria":"Aulas","preco":50.00}'

{"status":401,"erro":"Unauthorized","mensagem":"É necessário autenticação para acessar esse recurso"}
```

#### Maria publica o serviço (autenticada)

```bash
$ curl -X POST http://localhost:8080/api/servicos -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-maria>" \
  -d '{"titulo":"Aula de Cálculo 1","descricao":"Reforço para provas","categoria":"Aulas","preco":50.00}'

{"id":1,"titulo":"Aula de Cálculo 1","descricao":"Reforço para provas","categoria":"Aulas","preco":50.00,"situacao":"ATIVO","prestadorNome":"Maria Silva"}
```

#### Listar serviços (público, sem token)

```bash
$ curl http://localhost:8080/api/servicos

[{"id":1,"titulo":"Aula de Cálculo 1","descricao":"Reforço para provas","categoria":"Aulas","preco":50.00,"situacao":"ATIVO","prestadorNome":"Maria Silva"}]
```

#### Acesso negado por papel — João (não é dono nem ADMIN) tenta encerrar o serviço de Maria → 403

```bash
$ curl -X PATCH http://localhost:8080/api/servicos/1/encerrar \
  -H "Authorization: Bearer <token-joao>"

{"timestamp":"2026-09-15T19:57:10.666994","status":403,"erro":"Forbidden","mensagem":"Você só pode encerrar os próprios serviços","caminho":"/api/servicos/1/encerrar"}
```

#### Maria tenta contratar o próprio serviço → 422

```bash
$ curl -X POST http://localhost:8080/api/contratacoes -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-maria>" -d '{"servicoId":1}'

{"timestamp":"2026-09-15T19:57:10.736620","status":422,"erro":"Unprocessable Content","mensagem":"Você não pode contratar o próprio serviço","caminho":"/api/contratacoes"}
```

#### João contrata o serviço de Maria (sucesso)

```bash
$ curl -X POST http://localhost:8080/api/contratacoes -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-joao>" -d '{"servicoId":1}'

{"id":1,"servicoId":1,"servicoTitulo":"Aula de Cálculo 1","contratanteNome":"João Souza","situacao":"SOLICITADA","dataContratacao":"2026-09-15T19:57:10.764541"}
```

#### Maria (dona) encerra o próprio serviço (sucesso)

```bash
$ curl -X PATCH http://localhost:8080/api/servicos/1/encerrar \
  -H "Authorization: Bearer <token-maria>"

{"id":1,"titulo":"Aula de Cálculo 1","descricao":"Reforço para provas","categoria":"Aulas","preco":50.00,"situacao":"ENCERRADO","prestadorNome":"Maria Silva"}
```

#### João tenta contratar o serviço já encerrado → 422

```bash
$ curl -X POST http://localhost:8080/api/contratacoes -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-joao>" -d '{"servicoId":1}'

{"timestamp":"2026-09-15T19:57:10.948982","status":422,"erro":"Unprocessable Content","mensagem":"Esse serviço não está ativo e não pode ser contratado","caminho":"/api/contratacoes"}
```
