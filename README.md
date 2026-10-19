# ReserveFlow

**Ticket reservation backend built with Java 21, Spring Boot 4.1.1, Spring Data JPA, Hibernate and PostgreSQL 17.**

ReserveFlow is a backend portfolio project focused on three engineering challenges: preventing ticket overselling under concurrent requests, keeping reservations consistent during event cancellation, and controlling SQL query counts during paginated reads.

## Engineering highlights

| Challenge | Implementation | Evidence |
| --- | --- | --- |
| Concurrent reservations for limited stock | Optimistic locking with `@Version` on `Ticket` | Concurrency tests check conflicts, committed reservations and final inventory |
| Reservation operations racing with event cancellation | Pessimistic read/write locks on `Event` and service-layer transactions | Four event-cancellation concurrency scenarios |
| N+1 queries and reservation pagination | To-one fetch join, Hibernate batch fetching, database-level pagination | Historical Event–Ticket experiment: **51 vs 1 SELECT**; reservation page test: **20 rows with 4 SQL statements** in a recorded run |

## 1. Transaction and concurrency design

### Optimistic locking: prevent overselling

A simple stock check cannot prevent two transactions from reading the same quantity and both attempting to reserve it. `Ticket` has a JPA `@Version` field so conflicting updates cannot silently overwrite each other.

The tests use a ticket with **one available unit** and two competing operations. They verify the successful update, the optimistic locking conflict, the remaining quantity and, for the reservation-level test, the number of reservations persisted. An optimistic locking conflict is mapped to HTTP `409 Conflict`.

**Code:** [Ticket.java](src/main/java/com/yejun/ticketreservation/domain/Ticket.java) · [ReservationService.java](src/main/java/com/yejun/ticketreservation/service/ReservationService.java)
**Tests:** [TicketConcurrencyTest.java](src/test/java/com/yejun/ticketreservation/concurrency/TicketConcurrencyTest.java) · [ReservationConcurrencyTest.java](src/test/java/com/yejun/ticketreservation/concurrency/ReservationConcurrencyTest.java)

### Pessimistic locking: coordinate event cancellation

A reservation must not be created while its event is being cancelled. The service uses a `PESSIMISTIC_READ` lock on `Event` when creating or cancelling an individual reservation and a `PESSIMISTIC_WRITE` lock when cancelling the entire event.

Event cancellation updates the event status, cancels its confirmed reservations and restores their ticket inventory within a transaction. Four integration-test scenarios cover competing operation orders and verify the final reservation and stock state.

**Code:** [EventRepository.java](src/main/java/com/yejun/ticketreservation/repository/EventRepository.java) · [EventService.java](src/main/java/com/yejun/ticketreservation/service/EventService.java)
**Test:** [EventCancellationLockTest.java](src/test/java/com/yejun/ticketreservation/concurrency/EventCancellationLockTest.java)

### Transaction boundaries and OSIV

State-changing operations run within service-layer `@Transactional` methods. Entity-to-DTO mapping occurs before the service returns.

`spring.jpa.open-in-view=false` is a deliberate boundary decision: lazy loading and database queries should be handled in the service layer rather than being triggered during controller response serialization. **Disabling OSIV is not claimed as a performance improvement.**

## 2. JPA query optimisation

### Historical Event–Ticket N+1 investigation

An earlier experiment exposed the N+1 pattern when loading events and lazily accessing their tickets. The saved SQL traces contain:

| Query pattern | SELECT statements in the recorded trace |
| --- | ---: |
| Event query followed by lazy ticket loading | **51** |
| Event–Ticket `LEFT JOIN FETCH` | **1** |

**Evidence:** [Before SQL](docs/performance/n-plus-one-before.txt) · [After SQL](docs/performance/n-plus-one-after.txt) · [Before screenshot](docs/performance/n-plus-one-before.png) · [After screenshot](docs/performance/n-plus-one-after.png)

This is a comparison of two recorded Event–Ticket loading patterns. It is **not** a response-time benchmark and is **not** a claimed before/after measurement of the current paginated reservation endpoint.

### Paginated reservation listing

A pageable query that fetch-joins a **collection** can interfere with database pagination. The reservation listing instead fetch-joins its to-one `Event` relationship and uses Hibernate batch fetching (`default_batch_fetch_size: 20`) for related reservation items and tickets. The main reservation query remains paginated in PostgreSQL.

On **19 October 2026**, a PostgreSQL-backed run of `ReservationPaginationPerformanceTest` produced:

| Measurement | Recorded result |
| --- | ---: |
| Reservations visible in that test transaction | **85**, including 20 created by the test |
| Reservations returned on the requested page | **20** |
| Prepared SQL statements during the measured read | **4** |

The measured read consisted of a paginated `Reservation`/`Event` query, a count query, a batch query for reservation items and a batch query for tickets. The test creates 20 reservations, flushes and clears the persistence context, resets Hibernate Statistics, checks the returned reservation IDs and asserts **at most five SQL statements**. Its own data is rolled back afterward.

**Code:** [ReservationRepository.java](src/main/java/com/yejun/ticketreservation/repository/ReservationRepository.java) · [ReservationService.java](src/main/java/com/yejun/ticketreservation/service/ReservationService.java)
**Test:** [ReservationPaginationPerformanceTest.java](src/test/java/com/yejun/ticketreservation/performance/ReservationPaginationPerformanceTest.java)

The **4-statement count is a result from one actual test run**, not a guarantee for every dataset or a latency/throughput measurement. The pagination test can be rerun to inspect current SQL counts; its console output is not stored in `docs/performance`.

## 3. Domain model and REST API

The model has five entities: `Event`, `Ticket`, `User`, `Reservation` and `ReservationItem`.

- An `Event` has one or two ticket types (`GENERAL` and/or `VIP`).
- A `Ticket` holds its price, available quantity and optimistic-lock version. `(event_id, ticket_type)` is unique.
- A `Reservation` belongs to one user and one event and contains one or more `ReservationItem` records.
- Reservation creation validates event availability, ticket ownership, duplicate ticket selections and available stock.
- Cancelling a reservation restores its quantities; cancelling an event also cancels affected confirmed reservations.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/users` | Create a user |
| `GET` | `/users`, `/users/{id}` | List or retrieve users |
| `DELETE` | `/users/{id}` | Delete a user when allowed |
| `POST` | `/events` | Create an event and its ticket types |
| `GET` | `/events`, `/events/{id}` | Paginated list or event details |
| `PUT` | `/events/{id}` | Update event details |
| `PATCH` | `/events/{id}/cancel` | Cancel an event |
| `POST` | `/reservations` | Create a reservation |
| `GET` | `/reservations`, `/reservations/{id}` | Paginated list or reservation details |
| `PATCH` | `/reservations/{id}/cancel` | Cancel a reservation and restore stock |

Example pagination: `GET /reservations?page=0&size=20&sort=id,asc`

## 4. Run with Docker Compose

**Prerequisite:** Docker Desktop or Docker Engine with Docker Compose.

From the project root, supply a password for your **own local PostgreSQL container**. No `.env` file is required.

**Windows PowerShell**

```powershell
$env:DB_PASSWORD="choose_your_own_local_password"
docker compose up --build
```

**macOS / Linux**

```bash
export DB_PASSWORD="choose_your_own_local_password"
docker compose up --build
```

The application listens on **http://localhost:8080**. The Compose database is separate from any existing development database and is not published on a host database port. Its data persists in a named Docker volume.

A new database starts without seeded users or events: `GET http://localhost:8080/events` will initially return an empty page. Automatic SQL seeding is disabled (`spring.sql.init.mode=never`), while local schema updates use Hibernate `ddl-auto: update`.

### Example reservation flow

**1. Create a user — `POST /users`**

```json
{"name":"Demo User","email":"demo@example.com"}
```

**2. Create an event — `POST /events`**

```json
{
  "title": "Demo Event",
  "venue": "Demo Hall",
  "startTime": "2030-01-01T19:00:00",
  "tickets": [
    {"ticketType": "GENERAL", "price": 60.00, "totalQuantity": 10}
  ]
}
```

**3. Reserve a ticket — `POST /reservations`**

```json
{
  "userId": 1,
  "eventId": 1,
  "items": [{"ticketId": 1, "quantity": 1}]
}
```

Replace the example IDs with the IDs returned by the previous requests. Set the event start time to a future date when trying the API.

## 5. Tests

The integration tests use PostgreSQL to exercise database locking and JPA behaviour.

| Test class | What it verifies |
| --- | --- |
| [TicketConcurrencyTest](src/test/java/com/yejun/ticketreservation/concurrency/TicketConcurrencyTest.java) | Optimistic-lock conflict and final ticket quantity |
| [ReservationConcurrencyTest](src/test/java/com/yejun/ticketreservation/concurrency/ReservationConcurrencyTest.java) | Competing reservations, persisted reservation count and inventory |
| [EventCancellationLockTest](src/test/java/com/yejun/ticketreservation/concurrency/EventCancellationLockTest.java) | Four event-cancellation concurrency scenarios |
| [ReservationPaginationPerformanceTest](src/test/java/com/yejun/ticketreservation/performance/ReservationPaginationPerformanceTest.java) | Paginated results and a bound on SQL statement count |
| [TicketReservationApplicationTests](src/test/java/com/yejun/ticketreservation/TicketReservationApplicationTests.java) | Spring application context loads |

All tests were run successfully through Gradle on **19 October 2026** with an accessible PostgreSQL database and the required `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables.

**Windows PowerShell** (with those environment variables already set):

```powershell
.\gradlew.bat test --no-daemon
```

**macOS / Linux:** `./gradlew test --no-daemon`

Run against a **dedicated test database**. Some concurrency tests persist their fixtures; the pagination test rolls back its changes. The Compose database is not exposed to host-side Gradle by default.

## Technology stack

- Java 21, Spring Boot 4.1.1, Spring MVC, Spring Data JPA, Hibernate 7.4.5.Final
- PostgreSQL 17
- Gradle Wrapper, Docker, Docker Compose
- JUnit 5, Spring Boot Test, AssertJ

---

*Scope: This is a backend engineering portfolio project. SQL counts refer to the recorded local test conditions, not production throughput, response-time improvements or a production deployment.*
