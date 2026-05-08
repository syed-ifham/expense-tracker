Potential issues and recommended fixes — scan report

Date: 2026-05-07

This file summarizes potential bugs, configuration problems, and future pitfalls found in the current codebase. Each item explains the issue, potential runtime symptoms, and recommended fixes.

1) Table name mismatch (HIGH)✅✅
- Problem: The configured persistent table name in `application.yaml` is `app.db.persistent.table-name: messages` (plural), but `MessageEntity` uses `@Table(name = "message")` (singular) and `PersistenceDatabaseValidator` creates/looks for a table named `message`.
- Symptom: JPA queries or manual SQL may fail with "no such table" or write to a different table than expected.
- Fix: Choose a single canonical table name (recommended: `messages`) and update:
  - `MessageEntity @Table(name = "messages")`
  - `PersistenceDatabaseValidator.initializePersistenceDatabaseSchema` to use the `tableName` argument and the configured `app.db.persistent.table-name` when creating/checking the schema.
  - Call `initializePersistenceDatabaseSchema` from `TrackerApplication` with the configured value instead of the hard-coded string.

2) JPA programmatic config overriding YAML (MEDIUM)
- Problem: `PersistenceJpaConfig` programmatically sets JPA properties; those override `application.yaml` values (previously caused SQL to print despite YAML settings).
- Symptom: Inconsistent behavior between expected YAML configuration and runtime; e.g. SHOW_SQL forced on.
- Fix: Make programmatic config read values from `Environment`/`@Value` rather than hard-coding, or remove the bean and use Spring Boot autoconfiguration if you can consolidate to a single DataSource.

3) Duplicate-check `existsById` causes repeated `SELECT COUNT(*)` (PERFORMANCE)
- Problem: `TransactionSyncService` checks `trackerPersistentRepository.isDuplicate(...)` before insert which triggers `SELECT COUNT(*)` for each message.
- Symptom: High number of extra DB queries; visible SQL volume and slower sync.
- Fix: Replace pre-check with optimistic save-and-catch duplicate exception (DataIntegrityViolationException) or use bulk `saveAll()` with unique constraint handling. Ensure `message_id` is a primary key/unique.

4) equals()/hashCode() NPE risk (LOW)
- Problem: Earlier `equals()` used direct `.equals` on fields that could be null.
- Action taken: Updated `equals()` to use `Objects.equals(...)` and added `hashCode()`.
- Recommendation: Keep using `Objects.equals` to avoid NPE and ensure consistent `hashCode` when overriding `equals`.

5) created_at column mapping semantics (INFO)
- Problem: `MessageEntity.createdAt` is mapped but DB sets default `CURRENT_TIMESTAMP`. JPA entity won't automatically receive DB-generated default value unless reloaded or explicitly configured.
- Symptom: `createdAt` null in entity after save or not populated.
- Fix: Either mark column `insertable=false, updatable=false` and reload after insert, or use `@CreationTimestamp` if you want Hibernate to manage timestamps.

6) Multiple DataSources and JPA auto-configuration (MEDIUM)
- Problem: Two `DataSource` beans (`sourceDataSource` and `persistenceDataSource`) require explicit JPA wiring. Without explicit `EntityManagerFactory` tied to `persistenceDataSource`, Spring cannot create repositories.
- Symptom: Bean creation errors like inability to resolve `entityManagerFactory` for JPA repo.
- Fix: Keep `PersistenceJpaConfig` (explicit EMF and transaction manager) or reduce to a single DataSource and mark it `@Primary` to allow auto-config.

7) Logging and Hibernate SQL printing ✅✅
- Problem: Hibernate `SHOW_SQL` can be enabled programmatically and via logging categories.
- Action taken: Programmatic SHOW_SQL changed to false and logging categories were suppressed in `application.yaml` and mirrored into `target/classes`.
- Note: If SQL still prints, ensure you restart the app after rebuild and there are no other logger configs (e.g., logback) overriding it.

8) Validator method `initializePersistenceDatabaseSchema` inconsistency (BUG) ✅✅
- Problem: Method accepts `tableName` but checks/creates `message` table with a hard-coded name, ignoring parameter.
- Symptom: Confusing behavior when different table names are configured.
- Fix: Use the parameter `tableName` for checks and creation SQL. Prefer string formatting with the configured name.

9) Concurrency / transaction boundaries in sync (MEDIUM)
- Problem: `syncMessagesToTracker()` processes messages in a loop without explicit transactions; each save is independent.
- Symptom: If partial failures occur you may get partially applied changes; performance may be poor for large batches.
- Fix: Decide atomicity semantics: either per-message transactional handling or batch transactions with chunked `saveAll()`.

10) Use of `Collections.synchronizedList` where not needed (LOW)
- Problem: `errors` is a synchronized list but sync loop is single-threaded.
- Recommendation: Use `ArrayList` unless multi-threaded operations are introduced.

11) IntelliJ / IDE linking of entity to DB schema (EXPLANATION)
- Problem: IDE cannot "resolve" table/column names in code because JPA annotations are not linked to a live DB schema in the IDE.
- Explanation/Fix: Configure a Database connection in the IDE pointing at your SQLite file (`./store/tracker.db`) and enable the JPA facet/inspection. This will allow schema-aware features such as navigation and validation.

12) Repository generic type mismatch (FIXED)
- Problem: `AppPersistentRepository` previously declared `JpaRepository<MessageEntity, Long>` while `MessageEntity` has `Integer` id. This caused type mismatch and bean creation issues.
- Action taken: Fixed to `JpaRepository<MessageEntity, Integer>`.

13) Potential classpath/dialect issues for SQLite (INFO)
- Problem: Hibernate doesn't ship a perfect official SQLite dialect; you included `hibernate-community-dialects` which is appropriate.
- Recommendation: Keep dialect dependency and verify version compatibility with your Hibernate/Spring Boot versions.


End of report.
