# StockAndShop

> 🇫🇷 [Français](#français) · 🇬🇧 [English](#english)

---

## Français

Application full-stack de gestion de **stock domestique** et de **listes de courses**, partagée entre plusieurs membres d'un même foyer (« home »), avec génération de recettes assistée par IA (Claude/Anthropic) à partir des produits disponibles.

### Fonctionnalités

- **Authentification** JWT (access + refresh token) avec cookies sécurisés
- **Gestion de "homes"** : création d'un foyer, invitation/gestion des membres, rôles (`HomeRole`)
- **Stock** : suivi des produits disponibles par foyer (quantité, unité, catégorie)
- **Listes de courses** : création, partage, ajout de produits depuis une base existante, favoris
- **Recettes** :
  - Génération de recettes via l'API Anthropic (Claude) à partir du stock du foyer ou d'une liste de produits donnée
  - Suggestions de recettes existantes en fonction du stock disponible
  - Recettes favorites par utilisateur
- **Temps réel** : notifications WebSocket (STOMP) pour la synchronisation du stock/listes entre les membres d'un foyer
- **Documentation API** : Swagger / OpenAPI intégré

### Stack technique

| Couche | Technologies |
|---|---|
| **Frontend** | Angular 21 (standalone components, signals, `httpResource`), PrimeNG, Tailwind CSS, RxJS, Vitest |
| **Backend** | Spring Boot 4 (Java 25), Spring Security, Spring Data JPA, Spring WebSocket (STOMP), Springdoc OpenAPI |
| **Base de données** | PostgreSQL 18 |
| **IA** | SDK Anthropic (Claude) pour la génération de recettes |
| **Auth** | JWT (jjwt) |
| **Infra** | Docker / Docker Compose, Nginx (sert le frontend buildé) |

### Architecture

```
StockAndShop/
├── StockAndShop-backend/    # API Spring Boot (Java 25)
│   └── src/main/java/be/stockandshopbackend/
│       ├── pl/          # Présentation : controllers, DTOs, websocket
│       ├── bll/         # Logique métier : services
│       ├── dal/         # Accès aux données : repositories
│       ├── dl/          # Domaine : entités, enums
│       ├── config/      # Configuration Spring (sécurité, CORS, websocket...)
│       ├── filters/     # Filtres servlet (JWT...)
│       └── advisor/     # Gestion centralisée des exceptions
├── StockAndShop-frontend/   # Application Angular 21
│   └── src/app/
│       ├── core/        # Guards, intercepteurs, services transverses, layout
│       ├── features/    # Modules métier (auth, home, recipe, shopping-list)
│       └── shared/      # Composants, modèles, pipes, services réutilisables
└── docker-compose.yml       # Orchestration Postgres + backend + frontend
```

### Prérequis

- Java 25 (JDK)
- Node.js 20+ et npm
- PostgreSQL 18 (ou via Docker)
- Une clé API Anthropic (pour la génération de recettes)
- Docker & Docker Compose (optionnel, pour un déploiement conteneurisé)

### Démarrage rapide (Docker)

1. Créer un fichier `.env` à la racine du projet :

   ```env
   DB_USERNAME=stockandshop
   DB_PASSWORD=changeme
   JWT_SECRET=<secret_base64_32_octets_minimum>
   ANTHROPIC_SECRET_KEY=<votre_clé_api_anthropic>
   ```

2. Lancer l'ensemble des services :

   ```bash
   docker compose up --build
   ```

   - Frontend : http://localhost
   - Backend (API + Swagger) : http://localhost:8080/swagger-ui.html

### Démarrage en local (sans Docker)

**Backend**

```bash
cd StockAndShop-backend
cp .env.example .env   # puis renseigner DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET
./mvnw spring-boot:run
```

L'API démarre sur `http://localhost:8080`.

**Frontend**

```bash
cd StockAndShop-frontend
npm install
npm start
```

L'application est disponible sur `http://localhost:4200`.

### Tests

```bash
# Backend
cd StockAndShop-backend && ./mvnw test

# Frontend
cd StockAndShop-frontend && npm test
```

### Variables d'environnement

| Variable | Description |
|---|---|
| `DB_URL` | URL JDBC PostgreSQL (mode local uniquement) |
| `DB_USERNAME` / `DB_PASSWORD` | Identifiants PostgreSQL |
| `JWT_SECRET` | Secret de signature des JWT (base64, ≥ 32 octets) |
| `ANTHROPIC_SECRET_KEY` | Clé API Anthropic pour la génération de recettes |
| `CORS_ALLOWED_ORIGINS` | Origines autorisées en CORS (défaut : `http://localhost:4200`) |
| `JWT_COOKIE_SECURE` | Cookie JWT en HTTPS uniquement (défaut : `false`) |

---

## English

Full-stack application for managing **household stock** and **shopping lists**, shared across multiple members of the same household ("home"), with AI-powered recipe generation (Claude/Anthropic) based on available products.

### Features

- **Authentication**: JWT (access + refresh token) with secure cookies
- **Home management**: create a household, invite/manage members, role-based access (`HomeRole`)
- **Stock**: track available products per home (quantity, unit, category)
- **Shopping lists**: create, share, add products from an existing catalog, mark as favorite
- **Recipes**:
  - Generate recipes via the Anthropic (Claude) API from the home's stock or a given product list
  - Suggest existing recipes based on available stock
  - Per-user favorite recipes
- **Real-time updates**: WebSocket (STOMP) notifications to sync stock/lists across home members
- **API documentation**: built-in Swagger / OpenAPI

### Tech stack

| Layer | Technologies |
|---|---|
| **Frontend** | Angular 21 (standalone components, signals, `httpResource`), PrimeNG, Tailwind CSS, RxJS, Vitest |
| **Backend** | Spring Boot 4 (Java 25), Spring Security, Spring Data JPA, Spring WebSocket (STOMP), Springdoc OpenAPI |
| **Database** | PostgreSQL 18 |
| **AI** | Anthropic (Claude) SDK for recipe generation |
| **Auth** | JWT (jjwt) |
| **Infra** | Docker / Docker Compose, Nginx (serves the built frontend) |

### Architecture

```
StockAndShop/
├── StockAndShop-backend/    # Spring Boot API (Java 25)
│   └── src/main/java/be/stockandshopbackend/
│       ├── pl/          # Presentation: controllers, DTOs, websocket
│       ├── bll/         # Business logic: services
│       ├── dal/         # Data access: repositories
│       ├── dl/          # Domain: entities, enums
│       ├── config/      # Spring configuration (security, CORS, websocket...)
│       ├── filters/     # Servlet filters (JWT...)
│       └── advisor/     # Centralized exception handling
├── StockAndShop-frontend/   # Angular 21 application
│   └── src/app/
│       ├── core/        # Guards, interceptors, cross-cutting services, layout
│       ├── features/    # Business modules (auth, home, recipe, shopping-list)
│       └── shared/      # Reusable components, models, pipes, services
└── docker-compose.yml       # Orchestrates Postgres + backend + frontend
```

### Prerequisites

- Java 25 (JDK)
- Node.js 20+ and npm
- PostgreSQL 18 (or via Docker)
- An Anthropic API key (for recipe generation)
- Docker & Docker Compose (optional, for containerized deployment)

### Quick start (Docker)

1. Create a `.env` file at the project root:

   ```env
   DB_USERNAME=stockandshop
   DB_PASSWORD=changeme
   JWT_SECRET=<base64_secret_at_least_32_bytes>
   ANTHROPIC_SECRET_KEY=<your_anthropic_api_key>
   ```

2. Start all services:

   ```bash
   docker compose up --build
   ```

   - Frontend: http://localhost
   - Backend (API + Swagger): http://localhost:8080/swagger-ui.html

### Local development (without Docker)

**Backend**

```bash
cd StockAndShop-backend
cp .env.example .env   # then fill in DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

**Frontend**

```bash
cd StockAndShop-frontend
npm install
npm start
```

The app is available at `http://localhost:4200`.

### Tests

```bash
# Backend
cd StockAndShop-backend && ./mvnw test

# Frontend
cd StockAndShop-frontend && npm test
```

### Environment variables

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL (local mode only) |
| `DB_USERNAME` / `DB_PASSWORD` | PostgreSQL credentials |
| `JWT_SECRET` | JWT signing secret (base64, ≥ 32 bytes) |
| `ANTHROPIC_SECRET_KEY` | Anthropic API key for recipe generation |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins (default: `http://localhost:4200`) |
| `JWT_COOKIE_SECURE` | Restrict JWT cookie to HTTPS (default: `false`) |
