# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Spring Boot 4 / Spring Framework 7 REST MVC backend ("Spring Framework 6: Beginner to Guru" course, upgraded to Spring Boot 4).
Exposes a beer / customer / beer-order REST API secured as an OAuth2 resource server. Requires
**Java 25**. Listens on port **8081** (NodePort **30081** in Kubernetes).

DTOs and event types are **not** in this repo — they come from the external Maven dependency
`ch.guru.springframework:spring-6-rest-mvc-api` (imported as `ch.guru.springframework.spring6restmvcapi.*`).
Entities and mappers convert between those DTOs and the JPA `entity` classes.

## Build & Test Commands

Use the Maven wrapper (`./mvnw`, or `mvnw.cmd` on Windows).

```bash
./mvnw clean verify          # full build: format check, unit + IT tests, jacoco, helm lint/template
./mvnw clean install         # verify + build local Docker image (skip.docker.build controls this) + helm package
./mvnw test                  # unit tests (*Test) via surefire
./mvnw verify                # integration tests (*IT) via failsafe
./mvnw test -Dtest=BeerControllerTest              # single test class
./mvnw test -Dtest=BeerControllerTest#methodName   # single test method
./mvnw spotless:apply        # auto-fix pom/markdown/json/yaml/shell formatting
./mvnw spring-javaformat:apply  # auto-fix Java code style
```

**Formatting is enforced at build time** (fails the `validate` phase):
- `spring-javaformat-maven-plugin` validates Java code style (config in `.springjavaformatconfig`).
- `spotless-maven-plugin` checks `pom.xml` (sortpom), `*.md`, `*.json`, `application*.yaml`, `*.sh`.
Run `./mvnw spring-javaformat:apply` and `./mvnw spotless:apply` before committing if the build complains.

## Running Locally

Two IntelliJ run configs in `.run/` cover both modes — Docker must be running:

- **"SpringRestMvcApplication With H2"** — default profile, in-memory H2 (Flyway disabled, schema via JPA).
  `spring-boot-docker-compose` auto-starts `compose-h2.yaml` which brings up **Kafka + Auth-Server**.
  H2 console at `/h2-console`.
- **"SpringRestMvcApplication mysql"** — `mysql` profile, Flyway migrations enabled
  (`src/main/resources/db/migration/V*.sql`). Auto-starts `compose.yaml` which brings up
  **MySQL + Kafka + Auth-Server**.

Both compose files start the **OAuth2 auth-server** (port 9000, `localhost:9000`) as a container —
it is the JWT issuer for tests and the running app. Kafka broker is exposed on `localhost:29092`.

## Architecture

### Layering (`src/main/java/ch/dboeckli/spring/restmvc/`)

- `rest/controller` — REST controllers. Request paths are **externalized** via config properties
  (`controllers.*.request-path`), not hard-coded in `@RequestMapping`. `ExceptionHandlerController` +
  `CustomErrorController` centralize error handling; `NotFoundException` → 404.
- `service` — business logic. Note there are **two implementations per service**: an in-memory map-based
  impl (e.g. `BeerServiceImpl`) and a JPA impl (`BeerServiceJpaImpl`). **The JPA impls are `@Primary`** and
  are what runs; the map-based impls exist mainly for controller unit tests.
- `repository` — Spring Data JPA repositories.
- `entity` — JPA entities; `mapper` — MapStruct mappers (component model `spring`, configured via the
  compiler plugin) that convert entity ↔ DTO.
- `bootstrap/BootstrapData` — `CommandLineRunner` seeding data (beers loaded from `csvdata/beers.csv` via
  `BeerCsvService` / opencsv).
- `config` — security, caching, Kafka topic names, OpenAPI, async/virtual-thread context propagation.
- `health` / `config/AuthServerHealthIndicator` — custom actuator health indicators (Kafka, auth-server).

### Messaging & Events

Two distinct event mechanisms — don't confuse them:
- **Spring `ApplicationEvent`s** (`event/events`, `event/listener/BeerCreatedListener`) — in-process
events published on beer create/update/patch/delete, handled asynchronously (virtual threads enabled via
`spring.threads.virtual.enabled=true`).
- **Kafka** — `OrderPlacedListener` publishes to the `order.placed` topic; `DrinkSplitterRouter` consumes it
and routes each order line to `drink.request.{icecold,cold,cool}` topics by beer style; `DrinkPreparedListener`
consumes `drink.prepared`. **All Kafka topic names are constants in `config/KafkaConfig`** — reference them,
don't inline strings.

### Security

`config/SpringSecurityConfigRest` — OAuth2 resource server (JWT). All endpoints require authentication
**except** actuator, swagger/openapi, and h2-console. CORS allowed origins are bound from
`security.cors.allowed-origins`. The `test-disabled-security` profile disables this chain for tests.

### Observability

Micrometer + Prometheus, OpenTelemetry (OTLP export disabled by default), actuator with all endpoints
exposed. OpenAPI via springdoc (`/swagger-ui/index.html`, `/v3/api-docs`); the OpenAPI JSON/YAML is
generated from the running app during the build.

## Testing Notes

- Test classes end in `Test` (unit, surefire) or `IT` (integration, failsafe).
- **Class execution is ordered** by `test/config/TestClassOrderer`: `*Test` → `*IT` → `OrderPlacedListenerTest` last.
- JUnit extension auto-detection is on (`junit-platform.properties`); `LocaleExtension` forces `Locale.US`.
- Test profiles: `test` (application-test.yaml) and `embedded_kafka_test` (uses `spring.embedded.kafka.brokers`
  instead of a real broker). MySQL integration tests (`MySqlIT`, `MySql2IT`) use **Testcontainers**.
- Controller tests build `MockMvc` manually and use `SecurityMockMvcRequestPostProcessors` for JWT; IT tests
  use `@SpringBootTest` with `@RecordApplicationEvents` to assert on published application events.

## Deployment

- Docker image built via `spring-boot:build-image` (buildpacks); `skip.docker.build` / `skip.docker.publish`
  properties gate build/publish. CI (`ci-cd` profile, auto-activated on GitHub Actions) publishes to GHCR and Docker Hub.
- Helm chart in `helm-charts/` — the `helm-maven-plugin` runs dependency-build/lint/template during `test` and
  package/dry-run during `install`. `values.yaml` + `dependencies-values.yaml` and the two `Chart.yaml` files are
  **merged at build time** (merge-yaml-plugin) into `target/helm-charts/`; the chart version is derived from the
  project version + git SHA. Raw k8s manifests are filtered into `target/k8s/`.
- See `README.md` for the full kubectl/helm deploy command sequences (they assume PowerShell).

## Conventions

- Nullability is annotated with **JSpecify** (`@NullMarked`, `@NonNull`) — preserve these on new code.
- Lombok is used throughout (`@RequiredArgsConstructor`, `@Slf4j`, `@Data`, `@Builder`); it is excluded from the
  final Spring Boot jar and paired with `lombok-mapstruct-binding` for MapStruct.
- Flyway migrations are **append-only and versioned** (`V{n}__description.sql`) — add a new file, never edit an
  existing migration. When changing `src/scripts/mysql-init.sql`, regenerate the k8s ConfigMap (see README).

