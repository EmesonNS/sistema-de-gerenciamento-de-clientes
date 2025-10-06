# Sistema de Gerenciamento de Clientes (Backend)

> API em Java + Spring Boot para gerenciar clientes, fotos e autenticação via JWT.
> Observação: o front-end é um repositório separado — veja a seção **Front-end (repositório separado)** no final do README.

---

## Sumário

* Descrição
* Tecnologias
* Estrutura do projeto
* Requisitos
* Variáveis de ambiente (e valores default)
* Como rodar (local / jar / Docker)
* Endpoints principais (exemplos)
* Autenticação (JWT + refresh token)
* Upload de fotos
* Front-end (repositório separado)
* Contribuição

---

# Descrição

Este projeto é o backend para um sistema simples de gerenciamento de clientes (CRUD), com:

* armazenamento de clientes e usuários (JPA + MySQL),
* autenticação com JWT (access token + refresh token),
* upload/recuperação/exclusão de foto do cliente,
* paginação, busca e filtros por status,
* endpoint de resumo (dashboard).

---

# Tecnologias

* Java 21
* Spring Boot (parent 3.5.5)
* Spring Web, Spring Data JPA, Spring Validation
* Spring Security (filtragem JWT + BCrypt)
* MySQL
* Maven
* Dockerfile para construção de imagem
* Upload de arquivos via `MultipartFile` (limitado a 5MB)

---

# Estrutura do projeto (resumo)

```
sistema_gerenciamento_clientes/
├─ src/main/java/br/com/example/gerenciador/
│  ├─ controller/       # AuthController, ClienteController, UsuarioController, Health
│  ├─ dto/              # DTOs: ClienteDTO, LoginRequest, JWTResponse, DashboardResumoDTO, etc.
│  ├─ model/            # Entidades JPA: Cliente, Usuario, StatusCliente
│  ├─ repository/       # Repositories JPA
│  ├─ service/          # Regras de negócio (ClienteService, UsuarioService)
│  ├─ security/         # JWTTokenUtil, CustomFilter, SecurityConfiguration
│  └─ advice/           # GlobalExceptionHandler
├─ src/main/resources/
│  └─ application.properties
├─ Dockerfile
└─ pom.xml
```

---

# Requisitos

* Java 21 (JDK)
* Maven 3.x
* MySQL (ou usar container)
* (Opcional) Docker

---

# Variáveis de ambiente (e defaults)

As variáveis são lidas pelo `application.properties`. Valores padrões presentes no projeto:

| Variável                    | Descrição                  | Valor Padrão                                          |
| --------------------------- | -------------------------- | ----------------------------------------------------- |
| `DATASOURCE_URL`            | URL do banco de dados      | `jdbc:mysql://localhost:3306/db_gerenciador_clientes` |
| `DATASOURCE_USERNAME`       | Usuário do banco           | `root`                                                |
| `DATASOURCE_PASSWORD`       | Senha do banco             | `root`                                                |
| `JWT_SECRET`                | Chave secreta do JWT       | `84e691d8-6826-40dc-84e3-b8ef09e8`                    |
| `jwt.access-expiration-ms`  | Expiração do Access Token  | `900000`                                              |
| `jwt.refresh-expiration-ms` | Expiração do Refresh Token | `604800000`                                           |

---

# Como rodar

### 1. Localmente com Maven

```bash
mvn clean package
mvn spring-boot:run
```

### 2. Gerar e Executar o JAR

```bash
mvn clean package -DskipTests
java -jar target/*.jar
```

### 3. Com Docker

```bash
docker build -t gerenciador-clientes .
docker run -d -p 8080:8080 \
  -e DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/db_gerenciador_clientes" \
  -e DATASOURCE_USERNAME="root" \
  -e DATASOURCE_PASSWORD="root" \
  -e JWT_SECRET="chave_secreta_segura" \
  gerenciador-clientes
```

---

# Endpoints principais (resumo)

> Base path usado: `/api/v1`

## Autenticação

### POST `/api/v1/auth/login`

Request (JSON):

```json
{
  "usernameOrEmail": "usuario_ou_email",
  "senha": "suaSenha"
}
```

Response (200):

```json
{
  "accessToken": "<jwt_access_token>",
  "refreshToken": "<jwt_refresh_token>"
}
```

* `accessToken` deve ser enviado nos requests autorizados no header:
  `Authorization: Bearer <accessToken>`

### POST `/api/v1/auth/refresh`

Request (JSON):

```json
{ "refreshToken": "<jwt_refresh_token>" }
```

Response (200):

```json
{ "accessToken": "<novo_access_token>", "refreshToken": "<mesmo_refresh_ou_novo>" }
```

> No projeto, o refresh token é também um JWT com validade maior (7 dias). A API valida esse JWT e emite um novo access token.

## Usuários (registro)

### POST `/api/v1/usuarios`

Request (JSON):

```json
{
  "username": "exemplo",
  "email": "ex@example.com",
  "senha": "senha123"
}
```

Response: `201 Created` com `UsuarioResponse`.

## Clientes

### POST `/api/v1/clientes`

Criar cliente (JSON — `ClienteDTO`):

```json
{
  "nome": "João",
  "telefone": "1199999-9999",
  "cpf": "000.000.000-00",
  "endereco": "Rua A, 123",
  "valor": 2500.0,
  "dataInicial": "2025-10-01",
  "dataFinal": "2025-10-30"
}
```

Retorna `201 Created` com o objeto criado.

### GET `/api/v1/clientes?page=0&size=10`

Lista paginada. Retorna o objeto `Page<ClienteDTO>` padrão do Spring Data (campos como `content`, `totalElements`, `totalPages`, `number`...).

### GET `/api/v1/clientes/{id}`

Retorna `ClienteDTO` ou `404`.

### GET `/api/v1/clientes/search?q=termo`

Busca por `nome`, `telefone` ou `endereco` contendo o `termo`.

### GET `/api/v1/clientes/filtro?page=0&size=10&status=PENDENTE&status=ATRASADO`

Filtra por status (o `status` é um parâmetro `List<String>`; você pode passar múltiplos `status` repetindo o parâmetro).

### PUT `/api/v1/clientes/{id}`

Atualiza cliente (Body `ClienteDTO`).

### DELETE `/api/v1/clientes/{id}`

Remove cliente.

### PUT `/api/v1/clientes/{id}/pagar`

Marca o cliente como pago (retorna o DTO atualizado).

### Upload de Foto

| Método   | Endpoint                     | Descrição                      |
| -------- | ---------------------------- | ------------------------------ |
| `POST`   | `/api/v1/clientes/{id}/foto` | Envia uma foto (MultipartFile) |
| `GET`    | `/api/v1/clientes/{id}/foto` | Retorna a foto do cliente      |
| `DELETE` | `/api/v1/clientes/{id}/foto` | Remove a foto do cliente       |

### Dashboard

* **GET** `/api/v1/clientes/resumo` — retorna `DashboardResumoDTO`:

```json
{
  "totalClientes": 10,
  "valorTotal": 5000.0,
  "pendentes": 4,
  "emAtraso": 2,
  "quitados": 4
}
```

---

# Fluxo de autenticação

1. Usuário faz `POST /api/v1/auth/login` -> recebe `accessToken` (curta duração) + `refreshToken` (maior duração).
2. Ao expirar `accessToken`, cliente chama `POST /api/v1/auth/refresh` com `refreshToken` para obter novo `accessToken`.
3. API valida tokens via `JWTTokenUtil` (assina/verifica com `jwt.secret`).

---

# Como integrar com o front-end (observações)

* O backend espera `Authorization: Bearer <accessToken>` para rotas protegidas.
* Endpoint público para login e refresh já existentes: `/api/v1/auth/login` e `/api/v1/auth/refresh`.
* CORS já permite `http://localhost:3000`.

---

# Front-end (repositório separado)

Este projeto backend tem o front-end em outro repositório.

```
Front-end (separado): https://github.com/...
```

Execução do frontend:

```bash
npm install
npm start
```

O frontend consome esta API em:

```
REACT_API_URL=http://localhost:8080/api/v1
```
---

# Contribuição

1. Faça um fork do projeto
2. Crie uma branch com sua feature (`git checkout -b minha-feature`)
3. Faça o commit (`git commit -m 'Adiciona nova feature'`)
4. Envie (`git push origin minha-feature`)
5. Abra um Pull Request

---
