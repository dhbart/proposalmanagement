# 📋 Proposal Management — Spring Security Study with Role-Based Access Control

Study project built during the **Santander Bootcamp on [DIO](https://www.dio.me/)**, focused on exploring Spring Security concepts in practice — from in-memory authentication to a scalable role-based authorization architecture using design patterns.

---

## 🧭 Overview

The application simulates a proposal management system for an influencer marketplace, where two types of users interact with the API in distinct ways:

| Role | Permissions |
|---|---|
| `INFLUENCER` | Create proposals and view only their own |
| `BRAND` | View all proposals from all influencers |

Security is handled entirely by **Spring Security**, with HTTP session-based authentication and role-based authorization using `@PreAuthorize`.

---

## 🏗️ Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters) principles:

- **Domain** — pure entities (`Proposal`, `Owner`, `ProposalId`, `OwnerId`) with no framework dependencies
- **Application** — use cases (`CreateProposalUseCase`, `ListProposalsUseCase`) and Strategy Pattern for listing
- **Infrastructure** — concrete implementations: JPA, security filters, HTTP controllers

```
src/main/java/estudojava/proposalManagement/
├── auth/
│   ├── domain/                    # UserRole enum
│   └── infrastructure/
│       ├── http/                  # Access test controller
│       ├── persistency/           # User entity + UserRepository
│       └── security/              # SecurityConfig, Login filter, JpaUserDetailsService
└── proposal/
    ├── domain/                    # Proposal, Owner, ProposalId, OwnerId, ProposalRepository
    ├── application/
    │   ├── list/                  # Strategy Pattern: AllStrategy, OwnStrategy, Factory
    │   ├── input/                 # CreateProposalInput
    │   └── output/                # ProposalOutput
    └── infrastructure/
        └── persistence/
            ├── entity/            # ProposalEntity (JPA mapping)
            ├── http/              # ProposalController + Request/Response
            └── repository/        # JpaProposalRepository + ProposalEntityRepository
```

---

## 🔧 Tech Stack

- **Java 25** with Virtual Threads
- **Spring Boot 4.1**
- **Spring Security** — authentication and authorization
- **Spring Data JPA** — persistence with Hibernate
- **MySQL 9.6** via Docker Compose
- **Lombok** to reduce boilerplate
- **BCrypt** for password hashing

---

## 🔐 Security in Layers

One of the most important aspects of this project is the progressive evolution of security, reflected in the code's own comments:

### 1. In-Memory Authentication (starting point)
The `InMemoryUserDetailsManager` bean (commented out in `SecurityConfig`) was the starting point — simple for testing, but without persistence.

### 2. Database Authentication
The `User` entity implements `UserDetails` directly, and `JpaUserDetailsService` implements `UserDetailsService` to load users from MySQL by username. Spring Security now uses the database as the source of truth.

### 3. Custom REST Login Filter
`RestUsernamePasswordAuthenticationFilter` extends Spring Security's default filter to accept credentials as **JSON in the request body** (instead of the default HTML form), making the API compatible with REST clients.

```
POST /api/auth/login
Body: { "username": "fitness_vibe", "password": "password" }
→ Authentication via HTTP session
→ 200 OK (session created)
```

### 4. Role-Based Authorization with `@PreAuthorize`
Endpoints are protected by declarative annotations:
```java
@PreAuthorize("hasRole('INFLUENCER')")              // influencers only
@PreAuthorize("hasAnyRole('INFLUENCER', 'BRAND')")  // both roles
```

---

## 🎯 Strategy Pattern for Scalable Listing

The `ListProposalsUseCase` implements the **Strategy Pattern** to handle different listing scopes per role — without `if/else` chains in the controller.

**Flow:**
```
GET /proposals
  → ProposalController identifies the authenticated user's role
  → Determines the AccessScope (OWN or ALL)
  → ListProposalsUseCase.execute(scope, ownerId)
  → Factory.getStrategy(scope) → returns OwnStrategy or AllStrategy
  → Strategy.getProposals(ownerId)
```

| Class | Role | Behavior |
|---|---|---|
| `OwnStrategy` | INFLUENCER | `findAllByOwnerId(ownerId)` |
| `AllStrategy` | BRAND | `findAll()` |
| `Factory` | — | `AccessScope → Strategy` map, built via list injection |

The `Factory` receives a `List<Strategy>` injected by Spring and automatically builds a `Map<AccessScope, Strategy>` — any new role/strategy only needs to implement the interface and will be registered without modifying the factory.

---

## 🌐 Endpoints

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Authentication with username/password in JSON |
| `GET` | `/` | Any authenticated | Returns "Hello World {username}" |
| `GET` | `/influencer` | Any authenticated | Test endpoint for INFLUENCER role |
| `GET` | `/brand` | Any authenticated | Test endpoint for BRAND role |
| `POST` | `/proposals` | INFLUENCER | Creates a new proposal |
| `GET` | `/proposals` | INFLUENCER / BRAND | Lists proposals (filtered by role) |

---

## 🚀 Getting Started

**Prerequisites:** Docker, Java 25, Gradle

```bash
# Clone the repository
git clone <repo-url>
cd proposalmanagement

# Start the MySQL database
docker compose up -d

# Run the application
./gradlew bootRun
```

On startup, a `CommandLineRunner` automatically creates three users in the database if it's empty:

| Username | Password | Role |
|---|---|---|
| `fitness_vibe` | `password` | INFLUENCER |
| `tech_guru` | `password` | INFLUENCER |
| `logistics` | `password` | BRAND |

---

## 📚 Concepts Explored

- Progressive security evolution: in-memory → database → custom REST filter
- Implementing `UserDetails` directly on the JPA entity
- `UserDetailsService` with `JpaUserDetailsService` for database-backed authentication
- Custom REST authentication filter extending `UsernamePasswordAuthenticationFilter`
- Declarative authorization with `@PreAuthorize` and `@EnableMethodSecurity`
- **Strategy Pattern** for scalable role-based listing logic
- Strategy factory automatically built via Spring list injection
- `@JsonInclude(NON_NULL)` to omit optional fields from JSON responses
- `BCryptPasswordEncoder` for secure password hashing
- Hexagonal Architecture separating domain, application, and infrastructure
