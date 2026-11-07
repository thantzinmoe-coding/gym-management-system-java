# Gym Management System — Backend

Spring Boot REST API (with WebSocket chat) for the gym management system. The React frontend lives in `../gym-management-system-react`.

## Tech stack

- Java 17, Spring Boot 3.5.3, Maven (wrapper included)
- Spring Web, Security (JWT via jjwt), Data JPA / Hibernate, Validation
- MySQL (primary database) and Redis (sessions / caching); the MongoDB starter is declared in `pom.xml` but no Mongo documents are defined in the code
- WebSocket (STOMP + SockJS), Spring Mail (HTML templates in `src/main/resources/templates/mailTemplates`)
- springdoc OpenAPI (Swagger UI), ModelMapper, Lombok

## Prerequisites

- JDK 17
- MySQL and Redis instances reachable from the app
- An SMTP account for outgoing mail

## Configuration

`application.properties` activates the `prod` profile by default (`spring.profiles.active=prod`) and sets the API base path to `/api/v1`. `application-prod.yaml` reads its settings from environment variables:

| Variable | Purpose |
| --- | --- |
| `AIVEN_DB_URL`, `AIVEN_DB_USERNAME`, `AIVEN_DB_PASSWORD` | MySQL connection |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL` | Redis connection |
| `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD` | SMTP server |
| `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH`, `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE` | SMTP options |
| `JWT_SECRET` | Secret used to sign JWTs |
| `PORT` | HTTP port (default `8080`) |

Check `src/main/resources/application-prod.yaml` for the full list.

For local development, put your own values in `src/main/resources/application-dev.properties` (every line there is currently commented out as a template) and run with the `dev` profile. Never commit real passwords, SMTP credentials or JWT secrets.

`compose.yaml` runs the backend and Mailpit in Docker. It joins the shared `gym-net` network, where MySQL and Redis come from their own stacks (`../database`, `../redis`); see the root README for the startup steps. Spring Boot's own Docker Compose integration stays disabled (`spring.docker.compose.enabled=false`).

### Access control

Access is enforced in two layers:

1. **Route rules** in `config/beans/SecurityConfig.java` decide which roles may call a route:
   - **Public:** sign-up and login, the one-time profile and detail creation during sign-up, and read-only home stats, reviews and equipment.
   - **Admin only:** `/super_admin/**` except the trainer lists, salaries, attendance management, notification sending, and all package, schedule, equipment and feedback changes.
   - **Everything else:** needs a login.
2. **Ownership checks** with `common/util/CurrentUser`. A route that takes a user, member, trainer or booking ID calls `currentUser.requireSelfOrAdmin(id)`, so a non-admin only reaches their own records. Chats use `requireSelf`, so they are private even from admins.

Other users' profiles are returned without NRC, phone, address or date of birth. Sign-up only creates MEMBER or TRAINER accounts.

When you add an endpoint, add a route rule if it isn't for every logged-in user, and an ownership check if it takes a user ID.

### Analytics (churn and recommendations)

`features/analytics` forwards requests to the internal Python service in `../gym-ml-service`, adding role checks and member names. Configure it with:

| Variable | Default | Purpose |
| --- | --- | --- |
| `ML_SERVICE_URL` | `http://localhost:8000` | Base URL of `gym-ml-serve` |
| `ML_API_KEY` | *(empty)* | Shared secret, sent as `X-ML-API-Key` |

If the ML service is down, only `/api/v1/analytics/*` returns 503. The rest of the API is unaffected.

The `RoleSeeder` inserts the MEMBER, TRAINER and ADMIN roles on startup if they're missing, so registration works on a fresh database.

## Run

```bash
# Linux / macOS
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

```powershell
# Windows
$env:SPRING_PROFILES_ACTIVE = "dev"; .\mvnw.cmd spring-boot:run
```

The API is served under `http://localhost:8080/api/v1`. With the default springdoc paths, Swagger UI is at `http://localhost:8080/swagger-ui.html`.

The schema is created and updated by Hibernate from the JPA entities (`spring.jpa.hibernate.ddl-auto=update`); roles are inserted by `RoleSeeder` at startup. The WebSocket (SockJS/STOMP) endpoint is `/ws`.

## Build and test

```bash
./mvnw test
./mvnw clean package
```

## Docker

```bash
docker build -t gym-management-system .
docker run -p 8080:8080 --env-file .env gym-management-system
```

The `Dockerfile` is a two-stage build (Maven build, then a slim Temurin 17 JRE) with JVM flags tuned for a 512 MB container. The metaspace cap defaults to 96 MB for that small plan but this app can exceed it (`OutOfMemoryError: Metaspace`), so set `MAX_METASPACE=256m` (the backend `compose.yaml` already does) on anything larger.

## Project structure

```
.
├── Dockerfile
├── compose.yaml
├── pom.xml
├── mvnw, mvnw.cmd
├── upload-dir/                     # uploaded files (local storage)
└── src
    ├── main
    │   ├── java/org/java_proj/gym_management_system
    │   │   ├── GymManagementSystemApplication.java
    │   │   ├── common/             # constants, converters, events, JPA naming, middleware, storage, utils
    │   │   ├── config/             # beans (ModelMapper, Swagger, security...), exceptions, response wrappers
    │   │   ├── model/              # JPA entities
    │   │   ├── home/               # dashboard summary
    │   │   └── features/           # one package per feature (controller / service / repository / dto)
    │   │       ├── assignedGymPackage   ├── bookPackage      ├── feedback
    │   │       ├── files                ├── forgotPassword   ├── manageAttendance
    │   │       ├── manageEquipment      ├── managePackage    ├── manageSchedule
    │   │       ├── message (chat)       ├── notification     ├── profile
    │   │       └── salary               superAdmin  userDetailInfo  users
    │   └── resources
    │       ├── application.properties, application-dev.properties, application-prod.yaml
    │       ├── json/               # sample request payloads
    │       └── templates/          # logo and HTML mail templates
    └── test/java/org/_java_proj/gym_management_system
        └── GymManagementSystemApplicationTests.java
```
