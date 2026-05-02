# Sensum

Sensum is a data visualization and digital twin system developed in collaboration with Eltratec d.o.o. It integrates Grafana OSS with the existing SmartWebService (SWS) SOAP API to enable advanced visualization of field station data.

---

## Branching Strategy

This repository uses a dual-track branching strategy — one track for Eltratec, one for FERI.

```
main-eltratec  ← stable, production-ready code for Eltratec
main-feri      ← stable, production-ready code for FERI
dev-eltratec   ← active development for Eltratec
dev-feri       ← active development for FERI
```

---

## Prerequisites

- Java 21 (OpenJDK or equivalent)
- Docker Desktop or Docker Engine + Docker Compose v2
- IntelliJ IDEA (recommended)

---

## Local Setup

Clone the repository and set up tracking branches so each dev branch automatically pushes and pulls from its corresponding main branch.

```bash
git clone https://github.com/Altr0y/Sensum.git
cd Sensum
git checkout -b dev-eltratec origin/dev-eltratec
git checkout -b dev-feri origin/dev-feri
```

---

## Environment Setup

Copy the example environment file and fill in the required values:

```bash
cp .env.example .env
```

Required variables in `.env`:

| Variable | Description |
|---|---|
| `SWS_BASE_URL` | SWS SOAP endpoint URL |
| `SWS_USERNAME` | SWS login username |
| `SWS_PASSWORD` | SWS login password |
| `GM_API_AUTH_TOKEN` | Shared secret between API Gateway and GM |

---

## Running with Docker

After the initial `docker compose ... up --build`, subsequent runs can also be managed through Docker Desktop by starting and stopping the relevant container group.

### Eltratec Environment

Contains only the GM Middleware and Grafana — intended for deployment at Eltratec or client sites.

```bash
docker compose -f docker-compose.eltratec.yml -p eltratec up --build
```

| Service | Port | Description |
|---|---|---|
| `eltratec-gm` | 8081 | Grafana Middleware (SOAP wrapper) |
| `grafana` | 3000 | Grafana OSS dashboard |

### FERI Environment

Contains the full Sensum demo stack — intended for faculty demonstration and development.

```bash
docker compose -f docker-compose.feri.yml -p feri up --build
```

| Service | Port | Description |
|---|---|---|
| `postgres` | 5432 | PostgreSQL demo database |
| `eltratec-gm` | 8081 | Grafana Middleware (SOAP wrapper) |
| `backend-core` | 8082 | Backend Core service |
| `api-gateway` | 8080 | API Gateway |
| `grafana` | 3000 | Grafana OSS dashboard |

### Stopping

```bash
# Stop Eltratec stack
docker compose -f docker-compose.eltratec.yml -p eltratec down

# Stop FERI stack
docker compose -f docker-compose.feri.yml -p feri down

# Stop and remove volumes (resets database)
docker compose -f docker-compose.feri.yml -p feri down -v
```

> **Note:** Run only one environment at a time to avoid port conflicts.

---

## Running Locally (without Docker)

```bash
# Export environment variables
export $(cat .env | grep -v '^#' | xargs)

# Run individual services
./gradlew :apps:eltratec-gm:run
./gradlew :apps:backend-core:run
./gradlew :apps:api-gateway:run
```

---

## Workflow

**Eltratec track:**
```bash
git checkout dev-eltratec
# ... make changes ...
git add .
git commit -m "DEV-xx feat: your message here"
git push
# then open a Pull Request: dev-eltratec → main-eltratec
```

**FERI track:**
```bash
git checkout dev-feri
# ... make changes ...
git add .
git commit -m "DEV-xx feat: your message here"
git push
# then open a Pull Request: dev-feri → main-feri
```

For larger changes, create a feature branch off the appropriate dev branch:
```bash
git checkout dev-feri
git checkout -b DEV-xx-short-description
# ... make changes ...
git push origin DEV-xx-short-description
# then open a Pull Request: DEV-xx-short-description → dev-feri
```

For commit message format and naming conventions, refer to the [Conventional Commits specification](https://www.conventionalcommits.org/en/v1.0.0/) and [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).

---

## Pull Request Format

PR titles must include the Jira issue key so the PR is linked in Jira:

```
DEV-<number> <type>(<optional scope>): <description>
```

Example:
```
DEV-42 feat(api): add sensor data endpoint
```