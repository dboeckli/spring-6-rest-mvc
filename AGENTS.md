# AGENTS.md

Spring Boot 4 (parent 4.1.0) / Spring Framework 6 REST MVC backend on **Java 25** (enforced by the
maven-enforcer plugin). Single Maven module, package `ch.dboeckli.spring.restmvc`. It is an OAuth2
JWT resource server exposing a Beer / Customer / BeerOrder REST API.

## Build & test commands

- Full build: `./mvnw clean verify` — format checks, unit (`*Test`, surefire) + IT (`*IT`, failsafe)
  tests, Helm lint/template, OpenAPI generation. `./mvnw verify` also runs the unit tests.
- Unit tests only: `./mvnw test`. Single test: `./mvnw test -Dtest=BeerControllerTest#methodName`.
- `./mvnw clean install` additionally builds the Docker image and packages the Helm chart into
  `target/helm/repo/`. Skip the Docker build with `-Dskip.docker.build=true`.
- `-Dskip.start.stop.springboot=true` skips the in-build app boot (spring-boot:start/stop) and the
  springdoc OpenAPI generation that depends on it.
- Start locally: `./mvnw spring-boot:run` (app on `:8081`).

After changing code, always verify: run the relevant Maven goal above and report its output
(evidence, not just "done").

## Sandbox build quirk (required)

This sandbox mounts the repo via filesystem passthrough, which blocks symlinks. Spotless's
`npm install` (prettier) therefore fails with `EPERM` unless npm skips bin links:

```bash
export npm_config_bin_links=false    # export BEFORE running ./mvnw
```

Without it, `./mvnw validate` / `./mvnw verify` fail in the spotless step. On a normal host
(Windows/CI) this is not needed.

## Formatting is enforced (fails the `validate` phase)

- Java: Spring Java Format → fix with `./mvnw spring-javaformat:apply`.
- Everything else (pom.xml, `**/*.md`, json, `src/main/resources/application*.yaml`, `**/*.sh`):
  Spotless → fix with `./mvnw spotless:apply`.
- Spotless flexmark also formats markdown, so this file and any `.md` edits must stay flexmark-clean;
  run `./mvnw spotless:apply` after editing markdown.

## External dependency gotcha

- All DTOs (`BeerDTO`, `BeerStyle`, `OrderPlacedEvent`, ...) come from the external module
  `ch.dboeckli.guru.springframework:spring-6-rest-mvc-api` (package `ch.guru.springframework.spring6restmvcapi`),
  resolved from GitHub Packages (`maven.pkg.github.com`). Without a PAT in `~/.m2/settings.xml`
  (server id `github`) the build cannot resolve dependencies.

## Test conventions

- Naming matters: `*Test` = unit (surefire), `*IT` = integration (failsafe). A `*Test` class will
  not run during `verify`'s failsafe phase and vice versa.
- Controller unit tests: `@WebMvcTest` + `@Import(SpringSecurityConfigRest.class)` +
  `@ActiveProfiles("test")`. Authenticate with a mocked JWT
  (`SecurityMockMvcRequestPostProcessors.jwt()` with scopes `message.read`/`message.write`); use
  `httpBasic(wrong...)` to assert 401.
- Mutating ITs are `@SpringBootTest` + `@ActiveProfiles("test")` + `@Transactional @Rollback(true)`
  so tests do not pollute each other.
- `verify` needs **Docker**: the `test` profile auto-starts `compose-h2.yaml` (Kafka + auth-server on
  `:9000`) during ITs, and the MySQL ITs (`MySqlIT`, `MySql2IT`) use Testcontainers.
- The Kafka test (`OrderPlacedListenerTest`) uses `@EmbeddedKafka` + profile `embedded_kafka_test`
  (compose skipped) and is designed to run last.
- A custom `TestClassOrderer` sorts test classes (unit → IT → Kafka test last) and `LocaleExtension`
  is auto-registered to force `Locale.US`. Do not add a global locale again.
- Bean-validation message order is nondeterministic, so tests assert with `Matchers.either(...)`.

## Architecture

- Layered flow: `rest/controller` → `service` (interface + `*JpaImpl`, marked `@Primary`) →
  `repository` → `entity`.
- Legacy `*ServiceImpl` classes are still `@Service` beans but serve as test fixtures only (unit
  tests instantiate them directly); do not wire them into controllers.
- Mappers are MapStruct with the Lombok binding (`-Amapstruct.defaultComponentModel=spring`);
  generated sources land in `target/generated-sources`.
- Spring application events (`event/events` + `event/listener`) and Kafka (topics in `KafkaConfig`)
  decouple Beer write operations from side effects.
- Endpoint base paths are config-driven (`controllers.beer-controller.request-path` etc.), not
  hardcoded — keep them in `application*.yaml` and bind via `@Value`.
- Security (`SpringSecurityConfigRest`): swagger, h2-console and actuator are `permitAll`; everything
  else requires a JWT with issuer `http://localhost:9000`.

## Running locally

- Default profile: `spring.docker.compose` auto-starts `compose-h2.yaml` (Kafka + auth-server) on
  boot. Auth-server must be reachable on `:9000` for the app to start cleanly.
- Profile `mysql`: uses `compose.yaml` (MySQL + Kafka + auth-server) and enables Flyway; DDL is
  validated against `db/migration` scripts.
- Manual API testing: IntelliJ HTTP files in `restRequest/` with environments in
  `http-client.env.json` (`local` 8081, `k8s` 30081); client-credentials `messaging-client`/`secret`.

## Deploy / CI

- Deployment is Helm-only: chart in `helm-charts/`, packaged to `target/helm/repo/`, release name =
  artifactId, namespace `spring-6-rest-mvc`. The README's raw `kubectl` flow references a `k8s/`
  source dir that no longer exists — ignore those sections.
- CI (`.github/workflows/`): `maven-build.yml` builds + deploys snapshots and triggers
  `deploy-and-test-cluster.yml`; `release.yml` runs `mvn release:prepare release:perform` on
  main/master only (version must be `-SNAPSHOT`); SonarCloud analysis runs in the `analyze` job.
- Dependency updates are managed via `.github/dependabot.yml` and `.github/renovate.json`; validate
  changes with `renovate-config-validator`.

