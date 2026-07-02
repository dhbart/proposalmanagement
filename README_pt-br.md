# 📋 Proposal Management — Estudo de Spring Security com Controle de Acesso por Papéis

Projeto de estudo desenvolvido durante o **Santander Bootcamp na [DIO](https://www.dio.me/)**, focado em explorar na prática os conceitos de segurança com Spring Security — partindo de autenticação em memória até uma arquitetura de autorização escalável baseada em papéis e padrões de design.

---

## 🧭 Visão Geral

A aplicação simula um sistema de gerenciamento de propostas para um marketplace de influenciadores, onde dois tipos de usuário interagem com a API de formas distintas:

| Papel | Permissões |
|---|---|
| `INFLUENCER` | Criar propostas e visualizar apenas as próprias |
| `BRAND` | Visualizar todas as propostas de todos os influenciadores |

A segurança é gerenciada inteiramente pelo **Spring Security**, com autenticação via sessão HTTP e autorização por papel usando `@PreAuthorize`.

---

## 🏗️ Arquitetura

O projeto segue os princípios da **Arquitetura Hexagonal** (Ports & Adapters):

- **Domain** — entidades puras (`Proposal`, `Owner`, `ProposalId`, `OwnerId`) sem dependência de frameworks
- **Application** — casos de uso (`CreateProposalUseCase`, `ListProposalsUseCase`) e Strategy Pattern para listagem
- **Infrastructure** — implementações concretas: JPA, filtros de segurança, controllers HTTP

```
src/main/java/estudojava/proposalManagement/
├── auth/
│   ├── domain/                    # Enum UserRole
│   └── infrastructure/
│       ├── http/                  # Controller de teste de acesso
│       ├── persistency/           # Entidade User + UserRepository
│       └── security/              # SecurityConfig, Filtro de login, JpaUserDetailsService
└── proposal/
    ├── domain/                    # Proposal, Owner, ProposalId, OwnerId, ProposalRepository
    ├── application/
    │   ├── list/                  # Strategy Pattern: AllStrategy, OwnStrategy, Factory
    │   ├── input/                 # CreateProposalInput
    │   └── output/                # ProposalOutput
    └── infrastructure/
        └── persistence/
            ├── entity/            # ProposalEntity (mapeamento JPA)
            ├── http/              # ProposalController + Request/Response
            └── repository/        # JpaProposalRepository + ProposalEntityRepository
```

---

## 🔧 Tecnologias

- **Java 25** com Virtual Threads
- **Spring Boot 4.1**
- **Spring Security** — autenticação e autorização
- **Spring Data JPA** — persistência com Hibernate
- **MySQL 9.6** via Docker Compose
- **Lombok** para reduzir boilerplate
- **BCrypt** para hash de senhas

---

## 🔐 Segurança em Camadas

Uma das maiores evoluções documentadas no projeto é a progressão da segurança, refletida nos comentários do próprio código:

### 1. Autenticação em Memória (ponto de partida)
O bean `InMemoryUserDetailsManager` (comentado no `SecurityConfig`) foi o ponto de partida — simples para testes, mas sem persistência.

### 2. Autenticação com Banco de Dados
A entidade `User` implementa `UserDetails` diretamente, e o `JpaUserDetailsService` implementa `UserDetailsService` para carregar o usuário do MySQL pelo username. O Spring Security passa a usar o banco como fonte de verdade.

### 3. Filtro de Login REST customizado
O `RestUsernamePasswordAuthenticationFilter` estende o filtro padrão do Spring Security para aceitar credenciais em **JSON** no body (ao invés do formulário HTML padrão), tornando a API compatível com clientes REST.

```
POST /api/auth/login
Body: { "username": "fitness_vibe", "password": "password" }
→ Autenticação via sessão HTTP
→ 200 OK (sessão criada)
```

### 4. Autorização Baseada em Papéis com `@PreAuthorize`
Os endpoints são protegidos por anotações declarativas:
```java
@PreAuthorize("hasRole('INFLUENCER')")         // apenas influenciadores
@PreAuthorize("hasAnyRole('INFLUENCER', 'BRAND')") // ambos os papéis
```

---

## 🎯 Strategy Pattern para Listagem Escalável

O caso de uso `ListProposalsUseCase` implementa o **Strategy Pattern** para resolver o problema de listagem com escopo diferente por papel — sem `if/else` no controller.

**Fluxo:**
```
GET /proposals
  → ProposalController identifica o papel do usuário autenticado
  → Determina o AccessScope (OWN ou ALL)
  → ListProposalsUseCase.execute(scope, ownerId)
  → Factory.getStrategy(scope) → retorna OwnStrategy ou AllStrategy
  → Strategy.getProposals(ownerId)
```

| Classe | Papel | Comportamento |
|---|---|---|
| `OwnStrategy` | INFLUENCER | `findAllByOwnerId(ownerId)` |
| `AllStrategy` | BRAND | `findAll()` |
| `Factory` | — | Mapa `AccessScope → Strategy`, construído via injeção de lista |

A `Factory` recebe uma `List<Strategy>` injetada pelo Spring e constrói um `Map<AccessScope, Strategy>` automaticamente — qualquer novo papel/estratégia só precisa implementar a interface e será registrado sem alterar a factory.

---

## 🌐 Endpoints

| Método | Endpoint | Papel | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Autenticação com username/password em JSON |
| `GET` | `/` | Qualquer autenticado | Retorna "Hello World {username}" |
| `GET` | `/influencer` | Qualquer autenticado | Endpoint de teste para papel INFLUENCER |
| `GET` | `/brand` | Qualquer autenticado | Endpoint de teste para papel BRAND |
| `POST` | `/proposals` | INFLUENCER | Cria uma nova proposta |
| `GET` | `/proposals` | INFLUENCER / BRAND | Lista propostas (filtradas por papel) |

---

## 🚀 Como Rodar

**Pré-requisitos:** Docker, Java 25, Gradle

```bash
# Clonar o repositório
git clone <url-do-repo>
cd proposalmanagement

# Subir o banco MySQL
docker compose up -d

# Rodar a aplicação
./gradlew bootRun
```

Na inicialização, o `CommandLineRunner` cria automaticamente três usuários no banco se ele estiver vazio:

| Username | Senha | Papel |
|---|---|---|
| `fitness_vibe` | `password` | INFLUENCER |
| `tech_guru` | `password` | INFLUENCER |
| `logistics` | `password` | BRAND |

---

## 📚 Conceitos Explorados

- Evolução progressiva da segurança: memória → banco de dados → filtro REST customizado
- Implementação de `UserDetails` diretamente na entidade JPA
- `UserDetailsService` com `JpaUserDetailsService` para autenticação com banco
- Filtro de autenticação REST customizado estendendo `UsernamePasswordAuthenticationFilter`
- Autorização declarativa com `@PreAuthorize` e `@EnableMethodSecurity`
- **Strategy Pattern** para lógica de listagem escalável por papel
- Factory de estratégias construída automaticamente via injeção de lista Spring
- `@JsonInclude(NON_NULL)` para omitir campos opcionais na resposta JSON
- `BCryptPasswordEncoder` para hash seguro de senhas
- Arquitetura Hexagonal separando domínio, aplicação e infraestrutura
