# Smart Finance Tracker - Project Structure Redesign

**Version**: 2.0 | **Date**: 2026-08-12 | **Status**: Proposed

---

## OVERVIEW

**Purpose**: Redesign project structure for better separation of concerns, maintainability, and scalability.

**Technology Stack**: Spring Boot 3.x, Java 17+, SQLite, Maven

**Databases**:
- `phone.db` (source) - Read-only SMS sourceMessages from Windows Phone Link
- `app.db` (application) - Read-write processed transactions

---

## PROPOSED STRUCTURE

```
smart-fin-tracker/
├── backend/
│   ├── src/main/java/com/smartfinancetracker/
│   │   ├── SmartFinanceTrackerApplication.java
│   │   │
│   │   ├── config/                          # Configuration
│   │   │   ├── DatabaseConfig.java
│   │   │   └── WebConfig.java
│   │   │
│   │   ├── startup/                        # Startup orchestration
│   │   │   ├── StartUpRunner.java
│   │   │   └── StartUpService.java
│   │   │
│   │   ├── reload/                         # Data reloading (source -> app)
│   │   │   ├── service/
│   │   │   │   ├── ReloadService.java
│   │   │   │   ├── DatabaseCopyService.java
│   │   │   │   ├── DatabaseVerificationService.java
│   │   │   │   └── DataSyncService.java
│   │   │   └── exception/
│   │   │       ├── ReloadException.java
│   │   │       └── SourceDatabaseException.java
│   │   │
│   │   ├── source/                         # Source DB access
│   │   │   └── repository/
│   │   │       └── SourceMessageRepository.java
│   │   │
│   │   ├── core/                           # Business logic
│   │   │   ├── service/
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── MessageProcessingService.java
│   │   │   │   └── CategoryService.java
│   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── TransactionDto.java
│   │   │   │   │   └── TransactionSummaryDto.java
│   │   │   │   └── mapper/
│   │   │   │       └── TransactionMapper.java
│   │   │   └── exception/
│   │   │       ├── TransactionNotFoundException.java
│   │   │       └── InvalidTransactionException.java
│   │   │
│   │   ├── persistence/                     # Application DB access
│   │   │   ├── repository/
│   │   │   │   ├── RawMessageRepository.java
│   │   │   │   └── TransactionRepository.java
│   │   │   └── schema/
│   │   │       ├── AppSchemaInitializer.java
│   │   │       └── migrations/
│   │   │
│   │   ├── entity/                          # Domain entities
│   │   │   └── db/
│   │   │       ├── SourceMessage.java
│   │   │       ├── RawMessage.java
│   │   │       └── Transaction.java
│   │   │
│   │   └── web/                            # Web layer
│   │       ├── controller/
│   │       │   ├── TransactionController.java
│   │       │   └── HealthController.java
│   │       ├── response/
│   │       │   ├── ApiResponse.java
│   │       │   └── ApiError.java
│   │       └── exception/
│   │           └── GlobalExceptionHandler.java
│   │
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       ├── application-prod.yaml
│   │       └── persistDBSchema.sql
│   │
│   └── db/
│       ├── source/
│       │   ├── phone.db
│       │   ├── phone.db-shm
│       │   └── phone.db-wal
│       └── app/
│           └── app.db
│
├── doc/
├── scripts/
│   ├── startup.bat
│   ├── startup.sh
│   └── backup_db.sh
│
├── .gitignore
├── README.md
└── pom.xml
```

---

## KEY IMPROVEMENTS

### 1. Separation of Concerns

| Concern | Old Package | New Package | Benefit |
|---------|-------------|-------------|---------|
| Reloading logic | `startup/service/` | `reload/service/` | Clear ownership |
| Source DB access | `repository/source/` | `source/repository/` | Isolated from app logic |
| Transaction logic | Mixed in `repository/` | `core/service/` + `persistence/` | Business logic separated |
| Web endpoints | N/A (mixed) | `web/controller/` | Clean API layer |

### 2. Missing Pieces Added

| Component | Location |
|-----------|----------|
| Service layer | `core/service/` |
| DTO layer | `core/model/dto/` |
| Mapper layer | `core/model/mapper/` |
| Exception handling | `web/exception/` + custom exceptions |
| Data synchronization | `reload/service/DataSyncService.java` |
| Message processing | `core/service/MessageProcessingService.java` |
| Schema migrations | `persistence/schema/migrations/` |

### 3. Configuration Fixes

**Before:**
```yaml
app:
  db:
    persist:
      jdbcUrl: jdbc:sqlite:C:\Users\syedi\Desktop\...\app.db
```

**After:**
```yaml
app:
  db:
    persist:
      jdbcUrl: jdbc:sqlite:./db/app/app.db
      metadata:
        path: ./db/app/app.db
        name: app.db
        schema: persistDBSchema.sql
  path:
    windowsPhoneLinkSourceDir: ${SMART_FIN_SOURCE_DIR:./db/source}
    appSourceDatabaseDir: ./db/source
    appPersistDatabaseDir: ./db/app
```

---

## CLASS RESPONSIBILITIES

### Startup Layer
- **StartUpRunner.java**: Spring Boot entry point (CommandLineRunner), calls StartUpService
- **StartUpService.java**: Orchestrates startup sequence, calls ReloadService and AppSchemaInitializer

### Reload Layer
- **ReloadService.java**: Main orchestrator, coordinates verification -> copy -> sync
- **DatabaseCopyService.java**: Copies phone.db files if source is newer
- **DatabaseVerificationService.java**: Verifies source DB files exist
- **DataSyncService.java**: Synchronizes data from source DB to app DB

### Core Layer
- **TransactionService.java**: Business logic for transaction operations
- **MessageProcessingService.java**: Parses SMS sourceMessages to extract transaction data
- **CategoryService.java**: Categorizes transactions based on remittance

### Persistence Layer
- **RawMessageRepository.java**: CRUD for raw_messages table
- **TransactionRepository.java**: CRUD for transactions table
- **AppSchemaInitializer.java**: Initializes database schema

### Web Layer
- **TransactionController.java**: REST endpoints for transactions
- **HealthController.java**: Health check endpoints
- **GlobalExceptionHandler.java**: Exception handling for API

---

## DATA FLOW

### Startup Flow
```
Spring Boot Start
    │
    ▼
StartUpRunner.run()
    │
    ▼
StartUpService.initializeApplication()
    ├─▶ ReloadService.reloadSourceDatabase()
    │     ├─▶ DatabaseVerificationService.verifySourceFiles()
    │     ├─▶ DatabaseCopyService.copyIfNewer()
    │     └─▶ DataSyncService.syncNewMessages()
    │           ├─▶ SourceMessageRepository.findUnprocessedMessages()
    │           ├─▶ MessageProcessingService.processMessage()
    │           ├─▶ RawMessageRepository.save()
    │           └─▶ TransactionRepository.save()
    │
    └─▶ AppSchemaInitializer.initializeSchema()
          └─▶ SQLite JDBC creates app.db + executes persistDBSchema.sql
```

### Web Request Flow
```
HTTP GET /api/v1/transactions
    │
    ▼
TransactionController.getAllTransactions()
    │
    ▼
TransactionService.getTransactions(page, size)
    │
    ▼
TransactionRepository.findPaginated(page, size)
    │
    ▼
TransactionMapper.toDtoList()
    │
    ▼
ApiResponse.success()
    │
    ▼
HTTP 200 Response
```

---

## MIGRATION PLAN

### Phase 1: Package Restructuring (1-2 days)
- Create new package structure under `com.smartfinancetracker`
- Move existing classes to appropriate packages
- Update all imports

**Files to Move:**
```
DatabaseCopyService → reload/service/
DatabaseVerificationService → reload/service/
DataSynchronizationService → reload/service/ (rename to DataSyncService)
SourceRepo → source/repository/ (rename to SourceMessageRepository)
PersistRepo → persistence/repository/ (rename to RawMessageRepository)
InitializeSchema → persistence/schema/ (rename to AppSchemaInitializer)
```

### Phase 2: Create Missing Classes (2-3 days)
- Service layer (TransactionService, MessageProcessingService, CategoryService)
- DTO layer (TransactionDto, TransactionSummaryDto)
- Mapper layer (TransactionMapper)
- Web layer (ApiResponse, ApiError, GlobalExceptionHandler)
- Exception classes

### Phase 3: Implement Missing Logic (3-5 days)
- DataSyncService.syncNewMessages()
- MessageProcessingService
- Update StartUpService
- Update controllers to use services

### Phase 4: Configuration Cleanup (1 day)
- Fix hardcoded paths
- Create profile-specific configs
- Add environment variable support

### Phase 5: Testing (2-3 days)
- Unit tests for services
- Integration tests for repositories
- End-to-end tests for controllers

**Total**: 10-14 days

---

## NAMING CONVENTIONS

| Type | Convention | Example |
|------|------------|---------|
| Package | lowercase | `com.smartfinancetracker.core.service` |
| Service | `XxxService` | `TransactionService` |
| Repository | `XxxRepository` | `TransactionRepository` |
| Entity | PascalCase | `Transaction` |
| DTO | `XxxDto` | `TransactionDto` |
| Mapper | `XxxMapper` | `TransactionMapper` |
| Controller | `XxxController` | `TransactionController` |
| Exception | `XxxException` | `TransactionNotFoundException` |

---

## KEY FILES CONTENT

### DatabaseConfig.java
```java
@Configuration
public class DatabaseConfig {
    @Bean("sourceDataSource")
    @ConfigurationProperties(prefix = "app.db.source")
    public DataSource sourceDataSource() { return DataSourceBuilder.create().build(); }

    @Bean("sourceJdbcClient")
    public JdbcClient sourceJdbcClient(@Qualifier("sourceDataSource") DataSource ds) {
        return JdbcClient.create(ds);
    }

    @Bean("persistDataSource")
    @ConfigurationProperties(prefix = "app.db.persist")
    public DataSource persistDataSource() { return DataSourceBuilder.create().build(); }

    @Bean("persistJdbcClient")
    public JdbcClient persistJdbcClient(@Qualifier("persistDataSource") DataSource ds) {
        return JdbcClient.create(ds);
    }
}
```

### StartUpService.java
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class StartUpService {
    private final ReloadService reloadService;
    private final AppSchemaInitializer schemaInitializer;

    @Value("${app.path.windowsPhoneLinkSourceDir}")
    private Path windowsPhoneLinkSourceDir;

    @Value("${app.path.appSourceDatabaseDir}")
    private Path appSourceDatabaseDir;

    public void initializeApplication() {
        log.info("Starting initialization...");
        reloadService.reloadSourceDatabase(windowsPhoneLinkSourceDir, appSourceDatabaseDir);
        schemaInitializer.initializeSchema();
        log.info("Initialization complete");
    }
}
```

### DataSyncService.java
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DataSyncService {
    private final SourceMessageRepository sourceMessageRepository;
    private final RawMessageRepository rawMessageRepository;
    private final MessageProcessingService messageProcessingService;

    @Transactional
    public void syncNewMessages() {
        List<SourceMessage> sourceMessages = sourceMessageRepository.findUnprocessedMessages();
        int processed = 0;
        for (SourceMessage msg : sourceMessages) {
            try {
                messageProcessingService.processMessage(msg)
                    .ifPresent(rawMessageRepository::saveTransaction);
                rawMessageRepository.save(messageProcessingService.toRawMessage(msg));
                processed++;
            } catch (Exception e) {
                log.warn("Failed to process sourceMessage: {}", msg.messageId());
            }
        }
        log.info("Synchronized {} sourceMessages ({} processed)", sourceMessages.size(), processed);
    }
}
```

### TransactionService.java
```java
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public PageResponse<TransactionDto> getTransactions(int page, int size) {
        return new PageResponse<>(
            transactionMapper.toDtoList(transactionRepository.findPaginated(page, size)),
            new PageMeta(page, size, transactionRepository.count())
        );
    }
    public Optional<TransactionDto> getById(long id) {
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }
}
```

### TransactionController.java
```java
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ApiResponse<PageResponse<TransactionDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(transactionService.getTransactions(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionDto> getById(@PathVariable long id) {
        return ApiResponse.success(transactionService.getById(id)
            .orElseThrow(() -> new TransactionNotFoundException(id)));
    }
}
```

---

## SUMMARY

**Current Issues:** Mixed concerns, hardcoded paths, missing service layer, empty implementations, no DTO layer, poor error handling

**Proposed Solution:** Clean package separation, proper layering, complete implementations, configuration improvements

**Benefits:** Better maintainability, easier testing, clearer responsibilities, improved scalability, better security

---

**Version**: 2.0 | **Updated**: 2026-08-12 | **File**: `doc/projectStructure.md`
