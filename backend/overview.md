C:\Users\syedi\Desktop\Projects\smart-fin-tracker\backend> PROJECT OVERVIEW

# Smart Financial Tracker - Backend Architecture Overview

## 1. PROJECT CONTEXT

**Smart Financial Tracker** is a Spring Boot backend that automatically parses and tracks financial transactions from SMS messages. It ingests messages from a local SQLite database (YourPhone cache), processes them through multiple parsing strategies, and provides RESTful APIs.

---

## 2. NODES GRAPH - SYSTEM ARCHITECTURE

### 2.1 Layered Component Hierarchy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SMART FINANCIAL TRACKER BACKEND                          │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐    │
│  │   PRESENTATION    │    │     APPLICATION   │    │     DATA ACCESS   │    │
│  │      LAYER        │    │       LAYER      │    │       LAYER       │    │
│  │ • PublicController│    │ • MessageProcessor│    │ • SourceRepo      │    │
│  │ • LoginController │    │ • TransactionSvc  │    │ • PersistRepo     │    │
│  │ • TransactionCtrl │    │ • LoginService    │    │ • TransactionRepo │    │
│  └──────────┬────────┘    └──────────┬────────┘    └──────────┬────────┘    │
│             │                        │                        │               │
│             ▼                        ▼                        ▼               │
│  ┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐    │
│  │   CLIENT FACING   │    │   BUSINESS LOGIC  │    │   PERSISTENCE     │    │
│  │   REST ENDPOINTS  │    │   & ORCHESTRATION │    │   & INTEGRATION   │    │
│  └──────────────────┘    └──────────┬────────┘    └──────────────────┘    │
│                                          │                        │               │
│                                          ▼                        ▼               │
│                              ┌──────────────────┐    ┌──────────────────┐    │
│                              │   PARSING STRATEGY│    │   DATABASE layer  │    │
│                              │   PATTERN IMPLEMENT│   │                   │    │
│                              │ • SbiUpiStrategy  │    │ • phone.db        │    │
│                              │ • SbiDebitCard    │    │   (SOURCE/RO)     │    │
│                              │ • SmsParsing      │    │ • persist.db      │    │
│                              └──────────────────┘    │   (TARGET/RW)     │    │
│                                                    └──────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Data Flow Graph

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────────────────┐     ┌─────────────┐
│  Source DB   │────▶│  SourceRepo  │────▶│       MessageProcessor        │────▶│ Parsing     │
│  phone.db    │     │  (Read-Only) │     │ (Orchestrator & Normalizer)  │     │ Strategies  │
└─────────────┘     └─────────────┘     └─────────────────────────────┘     │  (Strategy   │
                                                                      │   Pattern)  │
                                                                      ▼            ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                     │
│  │ RawMessage   │◀────│  Normalize   │◀────│ Transaction  │                     │
│  │  Entity      │     │   & Parse    │     │  Entity      │                     │
│  └─────────────┘     └─────────────┘     └─────────────┘                     │
│            │                       │                       │                       │
│            ▼                       ▼                       ▼                       │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    PersistRepo (Write)                                │    │
│  │  ┌─────────────────┐    ┌─────────────────┐                            │    │
│  │  │ raw_messages     │    │ transactions     │                            │    │
│  │  │ table            │    │ table            │                            │    │
│  │  └─────────────────┘    └─────────────────┘                            │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                            │                                              │
│                            ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                 TransactionService                               │    │
│  │  • Business Logic  • Aggregation  • Pagination                     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                            │                                              │
│                            ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    API Controllers                              │    │
│  │  • PublicController  • LoginController  • TransactionController      │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                            │                                              │
│                            ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                    CLIENTS / FRONTEND                            │    │
│  │  • Web Dashboard  • Mobile App  • CLI Tools                            │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. HIGH-LEVEL DESIGN DIAGRAM

### 3.1 Layered Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           LAYERED ARCHITECTURE                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        PRESENTATION LAYER (HTTP)                       │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                     │   │
│  │  │ PublicCtrl   │  │ LoginCtrl    │  │ TxnCtrl      │                     │   │
│  │  │ GET /        │  │ POST /login │  │ GET /txns    │                     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                  │                                              │
│                                  ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         APPLICATION LAYER                               │   │
│  │  ┌─────────────────────────┐  ┌─────────────────────────┐              │   │
│  │  │      Services             │  │   Message Processing      │              │   │
│  │  │  • TransactionService     │  │  • MessageProcessor       │              │   │
│  │  │  • LoginService           │  │  • Reload Services        │              │   │
│  │  └─────────────────────────┘  └─────────────────────────┘              │   │
│  │                                                                          │   │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │   │
│  │  │    Strategy Pattern: SmsParsingStrategy Implementation            │   │   │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐               │   │   │
│  │  │  │SbiUpi       │  │SbiDebitCard │  │SmsParsing   │               │   │   │
│  │  │  │Strategy     │  │Strategy     │  │Strategy     │               │   │   │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘               │   │   │
│  │  └─────────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                  │                                              │
│                                  ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         DATA ACCESS LAYER                                │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                     │   │
│  │  │ SourceRepo  │  │ PersistRepo  │  │ Transaction  │                     │   │
│  │  │ (Read)      │  │ (Write)      │  │ Repository   │                     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                  │                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        INFRASTRUCTURE LAYER                            │   │
│  │  Spring Boot 4.1.0 | Java 17 | Maven | SQLite (HikariCP pool=1)        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. KEY DESIGN POINTS

### 4.1 Architectural Patterns

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **Strategy Pattern** | `SmsParsingStrategy` interface + 3 concrete implementations | Extensible message parsing for different bank formats |
| **Repository Pattern** | Spring Data JDBC repositories | Separates business logic from data access |
| **Layered Architecture** | Controllers → Services → Repositories → DB | Clear separation of concerns |
| **Dependency Injection** | Spring IoC container | Loose coupling, testability |

### 4.2 Core Component Relationships

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    COMPONENT RELATIONSHIP MAP                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│  CENTRAL ORCHESTRATOR: MessageProcessor                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Dependencies: PersistRepo, SourceRepo, List<SmsParsingStrategy>     │   │
│  │                                                                         │   │
│  │  PROCESS FLOW:                                                           │   │
│  │  1. SourceRepo.findAll() → List<Message>                                  │   │
│  │  2. mapToRawMessage() → List<RawMessage>                                  │   │
│  │  3. For each RawMessage:                                                   │   │
│  │     a. persistRepo.saveRawMessage()                                        │   │
│  │     b. For each Strategy: isApplicable() → parse() → Optional<Transaction>│   │
│  │     c. persistRepo.saveTransaction()                                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  DATA STORE: Dual SQLite Database System                                     │
│  ┌─────────────────┐         HikariCP         ┌─────────────────┐              │
│  │  Source DB       │◀───────────────────────▶│  Persist DB      │              │
│  │  phone.db        │    maxPoolSize: 1       │  persist.db      │              │
│  │  • Read-Only     │    (conservative)       │  • Read-Write    │              │
│  │  • YourPhone     │                         │  • Schema Managed│              │
│  │    Cache         │                         │    (persistDBSchema.sql)│              │
│  └─────────────────┘                         └─────────────────┘              │
│                                                                              │
│  RELATIONSHIP: raw_messages.message_id ──▶ transactions.message_id (FK)      │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 Entity Data Model

```
SOURCE DB ENTITY:                           TRANSFORMED TO:                PARSED TO:
┌─────────────────────┐         ┌─────────────────────┐         ┌─────────────────────┐
│      Message         │         │     RawMessage        │         │     Transaction       │
├─────────────────────┤         ├─────────────────────┤         ├─────────────────────┤
│ message_id (PK)      │────────▶│ message_id (PK)      │────────▶│ message_id (PK, FK)  │
│ from_address         │         │ sender_id            │         │ payment_method       │
│ body                 │         │ raw_body             │         │ direction            │
│ timestamp            │         │ sms_timestamp        │         │ amount               │
└─────────────────────┘         │ processed_at         │         │ remittance           │
                             └─────────────────────┘         │ transaction_date     │
                                                         │ category           │
                                                         └─────────────────────┘
```

---

## 5. API SURFACE

### 5.1 RESTful Endpoints

```
BASE URL: http://localhost:8080

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  /              │    │ /login           │    │ /transactions    │
│  PublicController│    │ LoginController  │    │ TransactionCtrl  │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ GET /           │    │ POST /login     │    │ GET /recent     │
│ → Health Check  │    │ → Authenticate  │    │ ?page=0&size=10 │
└─────────────────┘    └─────────────────┘    ├─────────────────┤
                                              │ GET /last-month- │
                                              │ income          │
                                              ├─────────────────┤
                                              │ GET /last-month- │
                                              │ expense         │
                                              └─────────────────┘
```

### 5.2 Sample Responses

```json
// GET /transactions/recent?page=0&size=10
{
  "data": [{"message_id": 12345, "payment_method": "UPI", "direction": "DEBIT", 
            "amount": 500.00, "remittance": "Amazon India", 
            "transaction_date": "2024-01-15 14:30", "category": "Uncategorized"}],
  "meta": {"page": 0, "size": 10, "total_rows": 150, "total_pages": 15}
}

// GET /transactions/last-month-expense
{ "amount": 25000.50 }

// GET /
{ "status": "OK", "message": "I am OK" }
```

---

## 6. TECHNICAL STACK

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| Runtime | Java | 17 | Core language |
| Framework | Spring Boot | 4.1.0 | Application framework |
| Build | Maven | - | Dependency management |
| Database | SQLite | - | Embedded database |
| JDBC Driver | sqlite-jdbc | - | SQLite connectivity |
| ORM | Spring Data JDBC | - | Data access abstraction |
| Connection Pool | HikariCP | - | Connection pooling (maxPoolSize=1) |
| Code Generation | Lombok | - | Boilerplate reduction |

---

## 7. STRATEGY PATTERN WORKFLOW

```
MessageProcessor.processRawMessage(RawMessage rm) {
    for (SmsParsingStrategy strategy : strategies) {
        if (strategy.isApplicable(rm.sender_id(), rm.raw_body())) {
            Optional<Transaction> tx = strategy.parse(
                rm.message_id(), rm.raw_body(), rm.getFormattedTransactionDate()
            );
            if (tx.isPresent()) { return tx; }
        }
    }
    return Optional.empty();
}

IMPLEMENTATIONS:
┌─────────────────────────┐  ┌─────────────────────┐  ┌─────────────────┐
│  SbiUpiParsingStrategy   │  │ SbiDebitCardParsing  │  │ SmsParsing       │
│  Pattern: "A/C (?<account>│  │ Strategy             │  │ Strategy         │
│  \S+) (?<direction>      │  │ Pattern: (custom)    │  │ Pattern: (custom)│
│  debited|credited) by... │  │                     │  │                 │
└─────────────────────────┘  └─────────────────────┘  └─────────────────┘

BENEFIT: Adding new bank parser = Implement SmsParsingStrategy interface
```

---

## 8. DATABASE SCHEMA

```sql
-- persistDBSchema.sql
CREATE TABLE IF NOT EXISTS raw_messages (
    message_id    INTEGER PRIMARY KEY,
    sender_id     TEXT    NOT NULL,
    raw_body      TEXT    NOT NULL,
    sms_timestamp INTEGER NOT NULL,
    processed_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    message_id       INTEGER PRIMARY KEY,
    payment_method   TEXT     NOT NULL,   -- 'UPI', 'CARD'
    direction        TEXT     NOT NULL,   -- 'DEBIT' or 'CREDIT'
    amount           REAL     NOT NULL,
    remittance       TEXT,                -- The other party
    transaction_date DATETIME NOT NULL,
    category         TEXT DEFAULT 'Uncategorized',
    FOREIGN KEY (message_id) REFERENCES raw_messages (message_id)
);
```

---

## 9. FILE STRUCTURE

```
backend/
├── db/
│   ├── persist/persist.db
│   └── source/phone.db
├── src/
│   ├── main/
│   │   ├── java/tracker/
│   │   │   ├── BackendApplication.java
│   │   │   ├── config/
│   │   │   │   └── db/DataSourceConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── login/LoginController.java
│   │   │   │   ├── transaction/TransactionController.java
│   │   │   │   └── PublicController.java
│   │   │   ├── entity/db/
│   │   │   │   ├── Message.java
│   │   │   │   ├── RawMessage.java
│   │   │   │   └── Transaction.java
│   │   │   ├── repository/
│   │   │   │   ├── persist/PersistRepo.java
│   │   │   │   ├── source/SourceRepo.java
│   │   │   │   └── transaction/TransactionRepository.java
│   │   │   └── service/
│   │   │       ├── MessageProcessor.java
│   │   │       ├── strategy/
│   │   │       │   ├── SmsParsingStrategy.java
│   │   │       │   ├── SbiUpiParsingStrategy.java
│   │   │       │   ├── SbiDebitCardParsingStrategy.java
│   │   │       │   └── SmsParsingStrategy.java
│   │   │       ├── transaction/TransactionService.java
│   │   │       └── reload/
│   │   │           ├── ReloadPersistDBFile.java
│   │   │           └── ReloadSourceDBFile.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── persistDBSchema.sql
│   └── test/java/tracker/
│       └── BackendApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
├── doc.md
└── overview.md
```

---

## 10. KEY ARCHITECTURAL DECISIONS

| Decision | Rationale | Impact |
|----------|-----------|--------|
| SQLite for Persistence | Embedded, zero-config, file-based | Simple deployment, limited concurrency |
| Dual Database Approach | Separate read-only source from writable persist | Data integrity, clear separation |
| Strategy Pattern for Parsing | Multiple bank formats, extensible | Easy to add new parsers |
| Spring Data JDBC | Lightweight ORM, good for simple CRUD | Less overhead than JPA |
| HikariCP Pool Size = 1 | SQLite doesn't support high concurrency | Optimal for single-writer |
| Record Types for Entities | Immutable data objects | Thread-safe, concise code |

---

## 11. DEPLOYMENT

```bash
# Build
.\mvnw.cmd clean package

# Run
java -jar target\backend-0.0.1-SNAPSHOT.jar

# Verify
curl http://localhost:8080/
```

---

## 12. VISUAL CHEATSHEET

```
╔═══════════════════════════════════════════════════════════════════════════╗
║                    SMART FINANCIAL TRACKER - AT A GLANCE                      ║
╚═══════════════════════════════════════════════════════════════════════════╝

  [YourPhone SMS] → [phone.db] → [SourceRepo] → [MessageProcessor] 
                                         ↓
                                  [Strategy Pattern]
          ┌─────────────────────┬─────────────────────┐
          │ SbiUpiParsing       │ SbiDebitCardParsing  │ SmsParsing
          └──────────┬──────────┴──────────┬──────────┘
                     │                       │
                     └──────────┬───────────┘
                                │
                                ▼
                         [Transaction]
                                │
                ┌───────────────┼───────────────┐
                ▼               ▼               ▼
          [PersistRepo]    [TransactionRepo]   [TransactionService]
                                │
                                ▼
                         [persist.db]
                                │
                ┌───────────────┴───────────────┐
                ▼                               ▼
          [raw_messages]                [transactions]

  API: /health, /login, /transactions/recent, /transactions/last-month-*

  Stack: Spring Boot 4.1.0 | Java 17 | SQLite | Strategy Pattern
└─────────────────────────────────────────────────────────────────────────────┘
```

---

*Generated: 2026-07-15 | Project Version: 1.0.0 | Document Version: 1.0*
