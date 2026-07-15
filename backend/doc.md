Smart Expense Tracker — Backend (High-level design)

Overview

The backend ingests SMS-like messages from a local "source" SQLite DB, parses them into transactions using interchangeable parsing strategies, persists raw messages and normalized transactions into a "persist" SQLite DB, and exposes REST APIs for clients.

Core components

- MessageProcessor: orchestrates reading raw messages and applying parsing strategies.
- Parsing strategies (Strategy pattern): SmsParsingStrategy, SbiUpiParsingStrategy, SbiDebitCardParsingStrategy. Each implements a common interface to detect and normalize transactions from different message formats.
- Services: TransactionService handles business logic and aggregation.
- Repositories / DB layer: SourceRepo (reads source DB), PersistRepo (manages persist DB), TransactionRepository (CRUD for transactions).
- Controllers: LoginController, TransactionController, PublicController — expose endpoints for health, transaction lists, summaries.

API surface (examples)

- GET /health — health
- POST /login — obtain session (basic flow)
- GET /transactions — list transactions
- GET /transactions/recent[?limit=10] — recent transactions
- GET /transactions/summary/weekly — weekly aggregate

DB access & schema

- source DB (db/source/phone.db): read-only, provided by platform (YourPhone cache). Reloaded via ReloadSourceDBFile/ReloadPersistDBFile utilities.
- persist DB (db/persist/persist.db): application-managed; schema in src/main/resources/persistDBSchema.sql. Key tables: raw_messages, transactions.

Visual flow (ASCII)

A compact, mono-space friendly flow (top → bottom). Use as a quick reference.

```
  [Source DB: phone.db]            <-- read-only platform DB
          |
          v
  (1) ReloadSourceDBFile  --->  [SourceRepo]   <-- reads new messages
          |
          v
  +---------------------------------------------+
  |  MessageProcessor (orchestration & routing) |
  +---------------------------------------------+
          |
          v
  [ParsingStrategy (interface)]   -- defines: detect(), parse(), normalize()
          |
    -------------------------------
    |              |              |
  [SbiUpi]     [SmsParsing]    [SbiDebit]
  (concrete strategies - each returns a NormalizedTransaction)
    -------------------------------
          |
          v
      [Normalized RawMessage]
          |
          v
      [PersistRepo]  --->  [persist DB: transactions table]
          |
          v
  [TransactionService]  -->  [TransactionRepository]
          |
          v
  [API Controllers]  -->  Frontend / Clients (GET /transactions, summaries...)
```

Readable step summary:
1. Reload source DB and let SourceRepo surface unread messages.
2. MessageProcessor applies appropriate ParsingStrategy to each message.
3. Strategy returns normalized transaction data (and raw message persisted).
4. PersistRepo stores raw + normalized records in persist DB.
5. TransactionService provides business views exposed via API.

Notes:
- Arrows indicate data flow; boxed components are modules/classes.
- This layout is intended for Markdown rendering (code block keeps spacing).


Important points

- Strategy pattern enables adding new bank/message parsers without changing orchestration logic.
- Keep DB access in repository classes; services should be transactionally safe and lightweight.
- Schema defined in persistDBSchema.sql — migrations are manual (simple app scope).
- SQLite pool size is conservative (hikari max 1) — tuned for local single-writer usage.
- Enable Lombok annotation processing in IDE.

Deployment / Run notes

- Java 17 and Maven (use mvnw.cmd on Windows).
- Ensure app.yaml paths point to the desired source and persist folders.

Further enhancements

- Add automated schema migration (Flyway/Liquibase) if schema evolution is expected.
- Add unit tests for each parsing strategy and integration tests for DB flows.
- Harden auth for login endpoints when exposing externally.
