# Sensum

Sensum is a modular system for collecting, processing, and visualizing measurement data obtained from an external SOAP-based service (SWS).

The system is designed as a multi-component architecture that separates data acquisition, processing, and presentation across different services.

---

## System Components

The project consists of the following main components:

- `apps/eltratec-gm`  
  Grafana Middleware responsible for communication with the external SWS system

- `apps/backend-core`  
  Core backend responsible for data processing and storage (MongoDB)

- `apps/api-gateway`  
  REST API layer used by frontend applications (e.g. Next.js)

- `apps/desktop-demo`  
  Desktop application for data visualization using Lets-Plot

---

## Shared Libraries

- `libs/sws-client` – SOAP client for SWS communication
- `libs/shared-auth` – authentication and session management
- `libs/shared-models` – API request/response models
- `libs/logging` – centralized logging utilities

---

## Technologies

- Kotlin (JVM)
- Ktor (backend services)
- MongoDB (planned data storage)
- SOAP (external integration)
- REST API (internal communication)
- Next.js (planned frontend)
- Jetpack Compose (desktop application)
- Lets-Plot (data visualization)

---

## System Flow

1. GM communicates with SWS via SOAP
2. Backend Core processes and stores data (MongoDB)
3. API Gateway exposes data via REST API
4. Clients (Next.js, desktop app, Grafana) consume the data

---

## Current Status

- GM service implemented
- SWS SOAP login integrated
- Session handling implemented (in-memory)
- Basic project structure established

---

## Notes

- The architecture is designed to support multiple clients (Grafana, web, desktop)
- Some components (Backend Core, API Gateway, Desktop App) are planned but not yet fully implemented