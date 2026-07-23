# Medical Queue — Technical Implementation Report

## 1. Database Migrations

### V9 — `V9__create_medical_queue_tables.sql`

**File:** `V9__create_medical_queue_tables.sql`

#### Table: `medical_queue`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BINARY(16)` | PK | UUID |
| `patient_id` | `BINARY(16)` | FK → `patients(id)`, NOT NULL | |
| `doctor_id` | `BINARY(16)` | FK → `users(id)` | null cho đến khi được gọi |
| `room_number` | `VARCHAR(10)` | | |
| `queue_number` | `INT` | NOT NULL | Số thứ tự trong ngày (reset mỗi ngày) |
| `status` | `VARCHAR(30)` | NOT NULL, CHECK | `WAITING`, `IN_PROGRESS`, `WAITING_FOR_RESULT`, `COMPLETED`, `CANCELLED` |
| `priority_level` | `VARCHAR(20)` | NOT NULL, DEFAULT 'REGULAR', CHECK | `EMERGENCY`, `REGULAR` |
| `notes` | `TEXT` | | |
| `checked_in_at` | `TIMESTAMP` | | null; set khi create (WAITING) |
| `called_at` | `TIMESTAMP` | | null; set khi call() |
| `started_at` | `TIMESTAMP` | | null; set khi call() |
| `waiting_for_result_at` | `TIMESTAMP` | | null; set khi sendToWaitingForResult() |
| `completed_at` | `TIMESTAMP` | | null; set khi complete() |
| `cancelled_at` | `TIMESTAMP` | | null; set khi cancel() |
| `cancel_reason` | `VARCHAR(500)` | | |
| `created_by` | `BINARY(16)` | FK → `users(id)`, NOT NULL | |
| `created_at` | `TIMESTAMP` | NOT NULL | |
| `updated_at` | `TIMESTAMP` | NOT NULL | |

#### Original Indexes (V9)

| Index | Columns | Purpose |
|-------|---------|---------|
| `idx_medical_queue_created_at` | `created_at` | Sort/filter by time |
| `idx_medical_queue_status` | `status` | Filter active queues |
| `idx_medical_queue_patient` | `patient_id` | Lookup by patient |
| `idx_medical_queue_doctor` | `doctor_id` | Lookup by doctor |
| `idx_medical_queue_created_by` | `created_by` | Audit trail |
| `idx_medical_queue_room_status` | `room_number, status` | **Composite** — primary query pattern |
| `idx_medical_queue_priority_status` | `priority_level, status` | Priority-based queuing |

---

### V10 — `V10__optimize_medical_queue.sql`

**File:** `V10__optimize_medical_queue.sql`

#### New Columns

| Column | Type | Notes |
|--------|------|-------|
| `version` | `BIGINT` | Default `0`. **Optimistic Locking** — tự động tăng khi update, chống lost update. |
| `queue_date` | `DATE` | **Generated column** — `CAST(created_at AS DATE) STORED`. Dùng cho UNIQUE constraint. |

#### New UNIQUE Constraint

| Constraint | Columns | Purpose |
|-----------|---------|---------|
| `uk_room_queue_date` | `(room_number, queue_number, queue_date)` | **Chống trùng số thứ tự** trong cùng một ngày tại một phòng khám. Khi 2 request đồng thời cùng lấy `max+1`, cái thứ 2 sẽ bị `DataIntegrityViolationException` → AddToQueueService retry. |

#### Optimized Indexes

| Index | Columns | Purpose | Type |
|-------|---------|---------|------|
| `idx_queue_sorting` | `(room_number, status, priority_level, checked_in_at)` | Query queue theo phòng, ưu tiên EMERGENCY trước, theo thời gian check-in | **New** |
| ~~`idx_medical_queue_room_status`~~ | ~~`(room_number, status)`~~ | Covered by `idx_queue_sorting` | **Dropped** |
| ~~`idx_medical_queue_priority_status`~~ | ~~`(priority_level, status)`~~ | Covered by `idx_queue_sorting` | **Dropped** |
| `idx_medical_queue_created_at` | `created_at` | Sort/filter by time | Kept |
| `idx_medical_queue_status` | `status` | Filter active queues | Kept |
| `idx_medical_queue_patient` | `patient_id` | Lookup by patient | Kept |
| `idx_medical_queue_doctor` | `doctor_id` | Lookup by doctor | Kept |
| `idx_medical_queue_created_by` | `created_by` | Audit trail | Kept |

---

## 2. REST API Endpoints

Base path: `/api/v1/queue`

| Method | Endpoint | Auth (Role) | Request Body / Params | Response | Description |
|--------|----------|-------------|-----------------------|----------|-------------|
| **POST** | `/api/v1/queue` | ADMIN, RECEPTIONIST | `AddToQueueRequest` | `201` → `MedicalQueueResponse` | Thêm bệnh nhân vào hàng đợi, auto-số thứ tự |
| **POST** | `/api/v1/queue/call-next` | ADMIN, DOCTOR | `CallNextRequest` | `200` → `MedicalQueueResponse` | Gọi bệnh nhân kế tiếp từ WAITING lên IN_PROGRESS |
| **PUT** | `/api/v1/queue/{id}/status` | ADMIN, DOCTOR, NURSE | `UpdateQueueStatusRequest` | `200` → `MedicalQueueResponse` | Chuyển trạng thái queue |
| **GET** | `/api/v1/queue/room/{roomNumber}` | Authenticated | `?status=&page=&size=` | `200` → `PageResponse<MedicalQueueResponse>` | DS hàng đợi theo phòng (phân trang) |
| **GET** | `/api/v1/queue/doctor/{doctorId}` | ADMIN, DOCTOR | `?status=&page=&size=` | `200` → `PageResponse<MedicalQueueResponse>` | DS hàng đợi theo bác sĩ (phân trang) |
| **GET** | `/api/v1/queue/count` | Authenticated | `?roomNumber=&doctorId=&status=` | `200` → `Long` | Đếm số lượng |

### Request DTOs

#### `AddToQueueRequest`
```java
public record AddToQueueRequest(
    @NotNull UUID patientId,
    @NotNull PriorityLevel priorityLevel,
    @NotNull String roomNumber
) {}
```

#### `CallNextRequest`
```java
public record CallNextRequest(
    @NotNull UUID doctorId,
    @NotNull String roomNumber
) {}
```

#### `UpdateQueueStatusRequest`
```java
public record UpdateQueueStatusRequest(
    @NotNull QueueStatus newStatus,
    UUID doctorId,
    String cancelReason
) {}
```

#### `PageResponse<T>` (Generic Pagination DTO)
```java
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static <T> PageResponse<T> of(
            List<T> content, int page, int size, long totalElements
    ) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }
}
```

#### `MedicalQueueResponse`
```java
public record MedicalQueueResponse(
    UUID id, UUID patientId, String patientName,
    UUID doctorId, String doctorName,
    String roomNumber, int queueNumber,
    QueueStatus status, PriorityLevel priorityLevel,
    String notes,
    Instant checkedInAt, Instant calledAt, Instant startedAt,
    Instant waitingForResultAt, Instant completedAt,
    Instant cancelledAt, String cancelReason,
    Instant createdAt, Instant updatedAt
) {}
```

---

## 3. State Machine (Workflow)

### State Diagram

```
                    ┌──────────────────────────┐
                    │         WAITING           │ ◄── Khởi tạo (create)
                    │  (checked_in_at = now)    │
                    └───────────┬──────────────┘
                                │
                    ┌───────────▼──────────────┐
                    │       IN_PROGRESS         │
                    │  (called_at, started_at)  │
                    └───┬───────────────┬───────┘
                        │               │
              ┌─────────▼───┐     ┌─────▼────────┐
              │WAITING_FOR   │     │   COMPLETED   │
              │_RESULT       │     │(completed_at) │
              └───┬──────────┘     └──────────────┘
                  │ (resume)
                  └──► IN_PROGRESS (quay lại)

    WAITING ──────────────────────────────────► CANCELLED
    IN_PROGRESS ──────────────────────────────► CANCELLED
    WAITING_FOR_RESULT ──────────────────────► CANCELLED
```

### Allowed Transitions

| From ↓ / To → | IN_PROGRESS | WAITING_FOR_RESULT | COMPLETED | CANCELLED |
|---------------|:-----------:|:------------------:|:---------:|:---------:|
| **WAITING** | ✅ (call) | ❌ | ❌ | ✅ (cancel) |
| **IN_PROGRESS** | — | ✅ (sendToResult) | ✅ (complete) | ✅ (cancel) |
| **WAITING_FOR_RESULT** | ✅ (resume) | — | ✅ (complete) | ✅ (cancel) |
| **COMPLETED** | ❌ | ❌ | — | ❌ |
| **CANCELLED** | ❌ | ❌ | ❌ | — |

### Business Rules enforced via `validateTransition()` + `cancel()` checks

1. **WAITING** → only `IN_PROGRESS` (call) or `CANCELLED` (cancel)
2. **IN_PROGRESS** → `WAITING_FOR_RESULT`, `COMPLETED`, or `CANCELLED`
3. **WAITING_FOR_RESULT** → `IN_PROGRESS` (resume), `COMPLETED`, or `CANCELLED`
4. **COMPLETED / CANCELLED** are terminal states — no transitions allowed
5. `complete()` and `sendToWaitingForResult()` call `validateTransition()` generic validation
6. `cancel()` has explicit check: **cannot cancel COMPLETED**
7. `resumeFromWaitingForResult()` checks that current status is exactly `WAITING_FOR_RESULT`
8. All invalid transitions throw `InvalidStatusTransitionException` (extends `RuntimeException`)

### Timestamp Mapping

| Trigger | Fields Set |
|---------|-----------|
| `create()` | `checked_in_at`, `created_at`, `updated_at` |
| `call(doctorId)` | `doctorId`, `called_at`, `started_at`, `updated_at` |
| `sendToWaitingForResult()` | `waiting_for_result_at`, `updated_at` |
| `resumeFromWaitingForResult()` | `updated_at` |
| `complete()` | `completed_at`, `updated_at` |
| `cancel(reason)` | `cancel_reason`, `cancelled_at`, `updated_at` |

---

## 4. Concurrency & Locking Strategy

Hệ thống sử dụng **3 cơ chế** để đảm bảo dữ liệu hàng đợi luôn nhất quán dưới concurrent access:

| Cơ chế | Nơi áp dụng | Mục tiêu |
|--------|-------------|----------|
| **PESSIMISTIC_WRITE (`FOR UPDATE`)** | `CallNextService` — `findNextWaiting()` | Đảm bảo chỉ một doctor lấy được bệnh nhân WAITING kế tiếp. Lock row cho đến khi transaction kết thúc. |
| **Optimistic Locking (`@Version`)** | `MedicalQueueEntity` + `MedicalQueue.restore()` | Chống **lost update** khi nhiều transaction cùng update cùng một queue record. JPA/Hibernate tự động kiểm tra version khi flush; nếu version mismatch → `OptimisticLockException`. |
| **UNIQUE Constraint + Retry** | `AddToQueueService.addToQueue()` | Chống duplicate `queue_number` trong cùng ngày/phòng. Khi 2 request đồng thời cùng tính `max+1`, cái thứ 2 bị `DataIntegrityViolationException` → service tự động retry tối đa **3 lần** với `findMaxQueueNumberForToday()` mới. |

**Flow retry:**
```
Client ──► AddToQueueService.addToQueue()
               │
               ├── doAddToQueue()  (attempt 1)
               │       │
               │       ├── findMaxQueueNumberForToday() → 5
               │       ├── save(new queue #6)
               │       └── ❌ DataIntegrityViolationException (trùng UNIQUE)
               │
               ├── doAddToQueue()  (attempt 2)
               │       │
               │       ├── findMaxQueueNumberForToday() → 6 (đã có queue #6)
               │       ├── save(new queue #7)
               │       └── ✅ Thành công
               │
               └── Return QueueResult
```

---

## 5. Implementation Architecture (Hexagonal)

```
Controller (REST)
    │  ◄── MedicalQueueRestMapper
    ▼
UseCase Interface (Port Inbound)
    ▲
    │
Service (Use Case Implementation)
    │  ◄── MedicalQueue (Domain Entity) — state machine + factory
    ▼
MedicalQueueRepository (Port Outbound)
    ▲
    │
RepositoryAdapter (Persistence Adapter)
    │  ◄── JpaMedicalQueueRepository (Spring Data JPA)
    ▼
MySQL (medical_queue table)
```

### Files Created

| Layer | File | Purpose |
|-------|------|---------|
| **Domain** | `domain/queue/MedicalQueue.java` | Entity with state machine, create(), call(), complete(), cancel(), restore() |
| **Domain** | `domain/queue/enums/QueueStatus.java` | Enum: WAITING, IN_PROGRESS, WAITING_FOR_RESULT, COMPLETED, CANCELLED |
| **Domain** | `domain/queue/enums/PriorityLevel.java` | Enum: REGULAR, EMERGENCY |
| **Domain** | `domain/queue/exception/QueueNotFoundException.java` | Exception: queue not found |
| **Domain** | `domain/queue/exception/InvalidStatusTransitionException.java` | Exception: invalid state transition |
| **Port Inbound** | `port/inbound/queue/AddToQueueUseCase.java` | Interface for adding to queue |
| **Port Inbound** | `port/inbound/queue/CallNextUseCase.java` | Interface for calling next |
| **Port Inbound** | `port/inbound/queue/UpdateQueueStatusUseCase.java` | Interface for updating status |
| **Port Inbound** | `port/inbound/queue/GetQueueListUseCase.java` | Interface for listing queues |
| **Port Outbound** | `port/outbound/repository/crudRepository/queue/MedicalQueueRepository.java` | Repository interface (save, findMaxQueueNumberForToday, ...) |
| **Application** | `application/ucservice/queue/AddToQueueService.java` | Implements AddToQueueUseCase (auto-numbering + retry on conflict) |
| **Application** | `application/ucservice/queue/CallNextService.java` | Implements CallNextUseCase (find WAITING → call, PESSIMISTIC_WRITE lock) |
| **Application** | `application/ucservice/queue/UpdateQueueStatusService.java` | Implements UpdateQueueStatusUseCase (delegate to domain) |
| **Application** | `application/ucservice/queue/GetQueueListService.java` | Implements GetQueueListUseCase (filter + sort + paginate → `PageResponse`) |
| **Adapter REST** | `adapter/inbound/rest/controller/MedicalQueueController.java` | REST controller (6 endpoints) |
| **Adapter REST** | `adapter/inbound/rest/request/queue/AddToQueueRequest.java` | Request DTO (jakarta.validation) |
| **Adapter REST** | `adapter/inbound/rest/request/queue/CallNextRequest.java` | Request DTO |
| **Adapter REST** | `adapter/inbound/rest/request/queue/UpdateQueueStatusRequest.java` | Request DTO |
| **Adapter REST** | `adapter/inbound/rest/response/queue/MedicalQueueResponse.java` | Response DTO |
| **Adapter REST** | `adapter/inbound/rest/mapper/MedicalQueueRestMapper.java` | Mapper: request → command, domain → response (includes `toPageResponse()`) |
| **DTO** | `port/dto/result/PageResponse.java` | Generic pagination wrapper DTO |
| **Persistence** | `persistence/entity/queue/MedicalQueueEntity.java` | JPA entity mapping (`@Version` for optimistic locking) |
| **Persistence** | `persistence/jpaRepository/queue/JpaMedicalQueueRepository.java` | Spring Data JPA (native query with `FOR UPDATE`) |
| **Persistence** | `persistence/mapper/queue/MedicalQueuePersistenceMapper.java` | JPA Entity ↔ Domain mapping (maps `version`) |
| **Persistence** | `persistence/adapterRepository/queue/MedicalQueueRepositoryAdapter.java` | Repository implementation |
| **Migration** | `resources/db/migration/V9__create_medical_queue_tables.sql` | Flyway migration (V9) |
| **Migration** | `resources/db/migration/V10__optimize_medical_queue.sql` | Flyway migration (V10 — UNIQUE, index, version column) |

---

## 6. Test Results — `mvn test`

### Total: **103 tests — 0 failures, 0 errors, 0 skipped** ✅

| Test Suite | Tests | Type | Scope |
|-----------|:-----:|------|-------|
| **MedicalQueue - Domain State Machine Tests** | 15 | Unit | State transitions: creation, call, complete, cancel, resume, invalid transitions, restore (added `version` param) |
| **AddToQueueService Tests** | 3 (+1) | Unit | Queue with next number + first of day + **retry on DataIntegrityViolationException** |
| **CallNextService Tests** | 2 | Unit | Success + no WAITING queues (QueueNotFoundException) |
| **UpdateQueueStatusService Tests** | 11 | Unit | 7 valid transitions + 4 invalid transitions (queue not found, WAITING→COMPLETED, COMPLETED→CANCELLED, CANCELLED→IN_PROGRESS) |
| **GetQueueListService Tests** | 3 | Unit | Filter by room, by doctor, empty result — adapted to `PageResponse` |
| **MedicalQueueController - MockMvc Tests** | 9 | Integration | 6 endpoints with success + validation scenarios — adapted to `PageResponse` JSON structure |
| MedicalHistoryController - MockMvc Tests | 4 | Integration | Existing |
| GetPatientMedicalHistoryQueryHandler | 8 | Unit | Existing |
| Role-Permission Mapping Tests | 7 | Unit | Existing |
| UserSession - Session Security Tests | 6 | Unit | Existing |
| User Domain Tests | 10 | Unit | Existing |
| PermissionEvaluator Tests | 12 | Unit | Existing |
| User Repository & Service Integration Tests | 11 | Integration | Existing |

### Test Coverage Highlights

- **State Machine**: All 15 possible transitions (valid + invalid) covered
- **Service Layer**: Repository mocked — tested business logic (numbering, exceptions, filtering, retry)
- **Controller**: `@WebMvcTest` with `@MockitoBean` use cases + `@AutoConfigureMockMvc(addFilters = false)` — validates HTTP status codes + JSON response structure (`PageResponse`)
- **Test Isolation**: Each test suite loads only what it needs (no redundant Spring context)
