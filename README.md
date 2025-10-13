# DAT250 Poll System – Backend (Spring Boot) + Dockerized MySQL + Test Profile

A backend for a simple **polling/voting system** (Users → Polls → Options → Votes) built with **Spring Boot 3**, **Spring Data JPA**, **MySQL** (for local/dev), and **H2** (for tests/CI).  
A SPA frontend (Svelte/Vite or similar) will be added next and can be served by Spring Boot or run separately during development.

---

## Features

- Create **Users**, **Polls**, **Options**, and **Votes**
- REST API with JSON
- Persistence via JPA (MySQL for dev, H2 for tests/CI)
- Docker Compose spins up **MySQL** + the **Spring Boot app**
- Clean separation of **Controller → Service/Manager → Repository → Entities**
- CORS friendly for local SPA development

---

## Architecture & Tech

- **Java 21**, **Gradle**, **Spring Boot 3.5**
- **Spring Web**, **Spring Data JPA**
- **MySQL 8** (local/dev) via Docker or host DB
- **H2** (in-memory for tests/CI)
- **Lombok** for boilerplate
- SPA frontend: **Svelte/Vite** 

---

## Entity Model

- **User** `(id, username, email)`  
  - 1..* **Poll** (creator)  
  - 1..* **Vote** (voter)

- **Poll** `(id, question, validUntil, isPublic, publishedAt, creator)`  
  - 1..* **VoteOption**  
  - 1..* **Vote**

- **VoteOption** `(id, caption, presentationOrder, poll)`  
  - 1..* **Vote**

- **Vote** `(id, voter, poll, voteOption, publishedAt)`

---

## Prerequisites

- **JDK 21+**
- **Gradle** (wrapper included: `./gradlew`)
- **Docker Desktop** (for Docker workflow)

---

## Configuration

### 1) `.env` & `.env.example`

Ship this template:

**`.env.example`**
```env
DB_PASSWORD=changeme
DB_ROOT_PASSWORD=changeme

Set own values
cp .env.example .env
# edit .env with your real passwords

Running project
./gradlew bootRun
# Backend on http://localhost:8080

Running with Docker
# 1) Build the Spring Boot JAR
./gradlew bootJar

# 2) Bring up services (reads .env for DB_PASSWORD / DB_ROOT_PASSWORD)
docker compose up --build

# Backend http://localhost:8080
# MySQL on host 3307 (connect with Workbench to localhost:3307)


API Endpoints
# Users
POST   /api/users                 # Create user
GET    /api/users                 # List users
GET    /api/users/{id}            # Get one user
DELETE /api/users/{id}            # Delete user (optional)

# Polls
POST   /api/polls                 # Create poll (DTO includes creatorId, question, validUntil, public flag, options[])
GET    /api/polls                 # List polls
GET    /api/polls/{pollId}        # Get poll
DELETE /api/polls/{pollId}        # Delete poll
POST   /api/polls/{pollId}/options # Add option
GET    /api/polls/{pollId}/options # List options
GET    /api/polls/{pollId}/results # (optionId, caption, vote count)

# Votes
POST   /api/polls/{pollId}/votes  # Cast vote (voterId, optionId)
GET    /api/polls/{pollId}/votes  # List votes (optional)



Curl commands for test:

Create user:
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@site.com"}'

Create poll:
curl -X POST http://localhost:8080/api/polls \
  -H "Content-Type: application/json" \
  -d '{
        "creatorId": 1,
        "question": "Vim or Emacs?",
        "validUntil": "2025-12-31",
        "public": true,
        "options": ["Vim", "Emacs"]
      }'

Vote:
curl -X POST http://localhost:8080/api/polls/1/votes \
  -H "Content-Type: application/json" \
  -d '{"voterId":1,"optionId":2}'


Build SPA
npm run build

Copy /dist into
backend/src/main/resources/static


Useful common commands:
# build & run backend (host DB)
./gradlew bootRun

# build jar
./gradlew bootJar

# run tests (H2)
./gradlew test

# docker up (MySQL + app). Any code changes, restart/rebuild
./gradlew bootJar or gradlew build
docker compose up -d --build

# docker down
docker compose down

# rebuild image (no cache)
docker compose build --no-cache

# tail logs
docker compose logs -f

# docker restart
docker compose restart app 
docker compose restart // both

# docker reset the DB
docker compose down -v     # removes the mysql volume

# other useful docker commands
docker compose ps              # status
docker compose logs -f app     # app logs
docker compose logs -f mysql   # db logs
docker compose down            # stop
docker compose down -v         # stop + wipe DB volume (resets data)

# quick API check
curl http://localhost:8080/api/polls