# Database Architecture Report

## Overview

This document analyzes the database handling architecture for the Smart Finance Tracker application, identifying all classes responsible for database creation and management, separating source and app databases, and highlighting design issues with suggested fixes.

---

## Database Classification

### 1. Source Database (Read-Only)

**Purpose**: External SMS sourceMessage database from Windows Phone Link application

| Category | Component | File Path | Description |
|----------|-----------|-----------|-------------|
| **Database File** | phone.db | `./db/source/phone.db` | SQLite database containing raw SMS sourceMessages |
| **Database File** | phone.db-shm | `./db/source/phone.db-shm` | SQLite shared memory file |
| **Database File** | phone.db-wal | `./db/source/phone.db-wal` | SQLite write-ahead log file |

### 2. Application/Persist Database (Read-Write)

**Purpose**: Application-managed database for processed transactions

| Category | Component | File Path | Description |
|----------|-----------|-----------|-------------|
| **Database File** | app.db | `C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\db\app\app.db` | SQLite database for processed data |

---

## Classes Responsible for Database Handling

### Configuration Layer

#### DataSource Configuration

| Class | File Path | Responsibility | Database Type |
|-------|-----------|----------------|---------------|
| `DataSourceConfig` | `tracker.config.db.DataSourceConfig` | Creates and configures DataSource beans | Both |
| &nbsp; | &nbsp; | `sourceDataSource()` - Creates source DB connection | Source |
| &nbsp; | &nbsp; | `sourceJdbcClient()` - Creates JdbcClient for source | Source |
| &nbsp; | &nbsp; | `persistDataSource()` - Creates persist DB connection | App |
| &nbsp; | &nbsp; | `persistJdbcClient()` - Creates JdbcClient for persist | App |

#### Schema Initialization

| Class | File Path | Responsibility | Database Type |
|-------|-----------|----------------|---------------|
| `InitializeSchema` | `tracker.config.db.InitializeSchema` | Initializes persist database schema | App |
| &nbsp; | &nbsp; | `persistSchema()` - Creates tables if not exist | App |
| &nbsp; | &nbsp; | Uses `persistDBSchema.sql` resource file | App |

### Repository Layer

#### Source Database Repositories

| Class | File Path | Uses | Database Type |
|-------|-----------|------|---------------|
| `SourceRepo` | `tracker.source.repository.SourceMessageRepository` | `@Qualifier("sourceJdbcClient")` | Source |
| &nbsp; | &nbsp; | `findAll()` - Queries SMS sourceMessages from SBI/UPI senders | Source |

#### Application Database Repositories

| Class | File Path | Uses | Database Type |
|-------|-----------|------|---------------|
| `PersistRepo` | `tracker.persistence.repository.RawMessageRepository` | `@Qualifier("persistJdbcClient")` | App |
| &nbsp; | &nbsp; | `saveRawMessage()` - Stores processed sourceMessages | App |
| &nbsp; | &nbsp; | `saveTransaction()` - Stores parsed transactions | App |
| &nbsp; | &nbsp; | `findAllRawMessages()` - Retrieves all raw sourceMessages | App |
| &nbsp; | &nbsp; | `findAllTransactions()` - Retrieves all transactions | App |
| &nbsp; | &nbsp; | `findRecentTransactions()` - Retrieves last 10 transactions | App |
| &nbsp; | &nbsp; | `findTransactionById()` - Retrieves specific transaction | App |
| `TransactionRepository` | `tracker.persistence.repository.TransactionRepository` | `@Qualifier("persistJdbcClient")` | App |
| &nbsp; | &nbsp; | `findPaginated()` - Paginated transaction retrieval | App |
| &nbsp; | &nbsp; | `countAll()` - Counts all transactions | App |
| &nbsp; | &nbsp; | `findLastMonthCredit()` - Recent credit transactions | App |
| &nbsp; | &nbsp; | `findLastMonthDebit()` - Recent debit transactions | App |

### Startup & Management Layer

#### Startup Services

| Class | File Path | Responsibility | Database Type |
|-------|-----------|----------------|---------------|
| `StartUpService` | `tracker.startup.service.db.StartUpService` | Orchestrates database verification on startup | Both |
| &nbsp; | &nbsp; | `verifySourceDatabase()` - Validates source DB files | Source |
| &nbsp; | &nbsp; | `verifyAppDatabase()` - Placeholder for app DB validation | App |
| `StartUpRunner` | `tracker.startup.service.StartUpRunner` | Spring CommandLineRunner entry point | Both |
| &nbsp; | &nbsp; | Calls `verifySourceDatabase()` and `verifyAppDatabase()` | Both |

#### Database Management Services

| Class | File Path | Responsibility | Database Type |
|-------|-----------|----------------|---------------|
| `DatabaseCopyService` | `tracker.startup.service.db.DatabaseCopyService` | Copies source DB files from Phone Link | Source |
| &nbsp; | &nbsp; | `copyDatabaseFiles()` - Copies phone.db, phone.db-shm, phone.db-wal | Source |
| `DatabaseVerificationService` | `tracker.startup.service.db.DatabaseVerificationService` | Verifies DB file existence and freshness | Source |
| &nbsp; | &nbsp; | `verifyExists()` - Checks if DB file exists | Source |
| &nbsp; | &nbsp; | `isSourceNewer()` - Compares file modification times | Source |
| `DataSynchronizationService` | `tracker.startup.service.db.DataSynchronizationService` | **INCOMPLETE** - Placeholder for sync logic | Both |
| &nbsp; | &nbsp; | `syncNewMessages()` - Empty implementation | Both |
| &nbsp; | &nbsp; | `mergeTransactions()` - Empty implementation | Both |

#### Exception Handling

| Class | File Path | Purpose | Database Type |
|-------|-----------|---------|---------------|
| `DatabaseNotFoundException` | `tracker.startup.exception.DatabaseNotFoundException` | Exception for missing database files | Source |
| `SourceFileLockedException` | `tracker.startup.exception.SourceFileLockedException` | Exception for file lock issues | Source |

### Entity Classes

| Class | File Path | Database Type | Description |
|-------|-----------|---------------|-------------|
| `Message` | `tracker.entity.db.SourceMessage` | Source | Represents raw SMS sourceMessage from source DB |
| `RawMessage` | `tracker.entity.db.RawMessage` | App | Represents processed sourceMessage in app DB |
| `Transaction` | `tracker.entity.db.Transaction` | App | Represents parsed transaction in app DB |

---

## Configuration Files

| File | File Path | Purpose |
|------|-----------|---------|
| `application.yaml` | `src/main/resources/application.yaml` | Spring Boot configuration for both databases |
| `persistDBSchema.sql` | `src/main/resources/persistDBSchema.sql` | Schema definition for app database |

### application.yaml Configuration

```yaml
app:
  db:
    source:
      jdbcUrl: jdbc:sqlite:./db/source/phone.db
      driverClassName: org.sqlite.JDBC
      metadata:
        path: ./db/source/phone.db
        name: phone.db
      hikari:
        maximumPoolSize: 1
    persist:
      jdbcUrl: jdbc:sqlite:C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\db\app\app.db
      driverClassName: org.sqlite.JDBC
      metadata:
        schema: persistDBSchema.sql
        path: ./db/app/app.db
        name: app.db
      hikari:
        maximumPoolSize: 1
```

---

## Design Issues, Fixes, and Naming Problems

### Critical Issues

| Issue | Location | Type | Severity | Fix | Status |
|-------|----------|------|----------|-----|--------|
| **Hardcoded absolute path** | `application.yaml:18` | Configuration | HIGH | Use relative path or environment variable | **NEEDS FIX** |
| **Inconsistent path references** | `application.yaml` | Configuration | HIGH | `jdbcUrl` uses absolute path, `metadata.path` uses relative path | **NEEDS FIX** |
| **Missing app.db initialization** | `StartUpService.verifyAppDatabase()` | Implementation | HIGH | Empty method - should call `InitializeSchema.persistSchema()` | **NEEDS FIX** |
| **DataSynchronizationService is empty** | `DataSynchronizationService` | Implementation | HIGH | Complete sync logic between source and app databases | **NEEDS FIX** |
| **Inconsistent exception symbol** | `InitializeSchema:38,43` | Code Style | MEDIUM | Uses checkmark symbols (✓, ✗) in logs - non-standard | **NEEDS FIX** |

### Naming Issues

| Current Name | Location | Issue | Severity | Suggested Fix | Status |
|--------------|----------|-------|----------|---------------|--------|
| `appSourceDatabaseDir` | `application.yaml:29` | Misleading - points to source DB | MEDIUM | Rename to `sourceDatabaseDir` | **NEEDS FIX** |
| `appPersistDatabaseDir` | `application.yaml:30` | Misleading - should be `persistDatabaseDir` | MEDIUM | Rename to `persistDatabaseDir` | **NEEDS FIX** |
| `persistSchemaPath` | `InitializeSchema:20` | Wrong property reference | HIGH | Should be `${app.db.persist.metadata.schema}` | **NEEDS FIX** |
| `InitializeSchema` | Class name | Misleading | MEDIUM | Should be `PersistSchemaInitializer` or `AppSchemaInitializer` | **OPTIONAL** |
| `PersistRepo` | Class name | Inconsistent with Spring conventions | LOW | Should be `RawMessageRepository` or `PersistRepository` | **OPTIONAL** |
| `SourceRepo` | Class name | Inconsistent with Spring conventions | LOW | Should be `MessageRepository` | **OPTIONAL** |

### Path Inconsistencies

| Component | Current Path | Expected Path | Issue | Fix |
|-----------|--------------|---------------|-------|-----|
| Source DB jdbcUrl | `./db/source/phone.db` | Relative | OK | - |
| Source DB metadata.path | `./db/source/phone.db` | Relative | OK | - |
| Persist DB jdbcUrl | Absolute Windows path | Should be relative | **INCONSISTENT** | Use `./db/app/app.db` |
| Persist DB metadata.path | `./db/app/app.db` | Relative | OK | - |

### Schema Issues

| Issue | Location | Type | Fix | Status |
|-------|----------|------|-----|--------|
| Schema path property mismatch | `InitializeSchema:20` | Configuration | Property `app.db.persist.metadata.schema` exists but class uses wrong injection | **NEEDS FIX** |
| Schema file not loaded via @Value | `InitializeSchema:20` | Implementation | `@Value("${app.db.persist.metadata.schema}")` declared but not used correctly | **NEEDS FIX** |

### Functional Issues

| Issue | Location | Description | Fix | Status |
|-------|----------|-------------|-----|--------|
| Schema initialization not called | `StartUpService.verifyAppDatabase()` | Empty method, schema never initialized | Call `InitializeSchema.persistSchema()` | **NEEDS FIX** |
| No validation for app.db | `StartUpService.verifyAppDatabase()` | No file existence check for app.db | Add validation similar to source DB | **NEEDS FIX** |
| DataSynchronizationService unused | `DataSynchronizationService` | Empty service, never called | Complete implementation and integrate | **NEEDS FIX** |

---

## Summary Statistics

### Source Database (phone.db)
- **Configuration Classes**: 1 (`DataSourceConfig`)
- **Repository Classes**: 1 (`SourceRepo`)
- **Service Classes**: 3 (`StartUpService`, `DatabaseCopyService`, `DatabaseVerificationService`)
- **Exception Classes**: 2 (`DatabaseNotFoundException`, `SourceFileLockedException`)
- **Entity Classes**: 1 (`Message`)

### Application Database (app.db)
- **Configuration Classes**: 2 (`DataSourceConfig`, `InitializeSchema`)
- **Repository Classes**: 2 (`PersistRepo`, `TransactionRepository`)
- **Service Classes**: 1 (`StartUpService` - partial)
- **Entity Classes**: 2 (`RawMessage`, `Transaction`)
- **Schema Files**: 1 (`persistDBSchema.sql`)

---

## Recommended Fixes Priority

### P0 - Critical (Must Fix)
1. Fix hardcoded absolute path in `application.yaml` for persist DB
2. Implement `StartUpService.verifyAppDatabase()` to initialize schema
3. Complete `DataSynchronizationService` implementation

### P1 - High Priority
1. Fix property reference in `InitializeSchema` for schema path
2. Ensure consistent path usage (all relative or all absolute)
3. Fix naming inconsistencies in application.yaml

### P2 - Medium Priority
1. Rename configuration properties for clarity
2. Improve class naming for better conventions
3. Remove non-standard logging symbols

### P3 - Low Priority (Optional)
1. Rename repository classes to follow Spring conventions
2. Consider renaming `InitializeSchema` for clarity

---

## Architecture Diagram (Text)

```
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────┐   │
│  │ StartUpRunner│───▶│StartUpService│───▶│ DatabaseCopyService │   │
│  └─────────────┘    └─────────────┘    └─────────────────────┘   │
│                          │                                           │
│                          ▼                                           │
│              ┌───────────────────────┐                               │
│              │DatabaseVerificationService│                           │
│              └───────────────────────┘                               │
│                          │                                           │
│          ┌───────────────┬───────────────┐                           │
│          ▼               ▼               ▼                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │
│  │ DataSource  │  │ DataSource  │  │  DataSource  │                │
│  │  Config     │  │  Config     │  │   Config     │                │
│  │             │──│─────────────┤──│             │                │
│  │ sourceData  │  │             │  │ persistData │                │
│  │  Source()   │  │             │  │  Source()   │                │
│  └─────────────┘  └─────────────┘  └─────────────┘                │
│          │                     │                    │                │
│  ┌───────▼───────┐    ┌─────────▼──────────┐    ┌────────▼────────┐ │
│  │ sourceJdbc   │    │ persistJdbc       │    │ Initialize       │ │
│  │ Client       │    │ Client            │    │ Schema          │ │
│  └───────┬───────┘    └─────────┬──────────┘    └────────┬────────┘ │
│          │                     │                    │                │
│  ┌───────▼───────┐    ┌─────────▼──────────┐                        │
│  │ SourceRepo    │    │ PersistRepo        │                        │
│  │               │    │ Transaction        │                        │
│  │               │    │ Repository         │                        │
│  └───────────────┘    └────────────────────┘                        │
│          │                     │                                   │
│  ┌───────▼───────┐    ┌─────────▼──────────┐                       │
│  │ Message       │    │ RawMessage         │                       │
│  │ (Entity)      │    │ Transaction        │                       │
│  └───────────────┘    │ (Entity)           │                       │
│                        └────────────────────┘                       │
│                                                                      │
└─────────────────────────────────────────────────────────────────┘
                              │              │
                              ▼              ▼
                ┌─────────────────────┐  ┌─────────────┐
                │  ./db/source/        │  │ ./db/app/   │
                │  phone.db            │  │ app.db      │
                │  phone.db-shm        │  └─────────────┘
                │  phone.db-wal        │
                └─────────────────────┘
                              │
                ┌─────────────────────┐
                │ Windows Phone Link   │
                │ Source Directory     │
                └─────────────────────┘
```

---

## File References

- **Source Database**: `C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\db\source\phone.db`
- **App Database**: `C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\db\app\app.db`
- **Configuration**: `C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\src\main\java\tracker\config\db\`
- **Repositories**: `C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\src\main\java\tracker\repository\`
- **Startup**: `C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend\src\main\java\tracker\startup\`
