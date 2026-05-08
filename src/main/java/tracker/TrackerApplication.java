package tracker;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tracker.config.source.SourceDatabaseValidator;
import tracker.config.persistence.PersistenceDatabaseValidator;
import tracker.controller.transaction.TransactionController;
import tracker.service.tracker.TransactionSyncService;

@Slf4j
@SpringBootApplication
public class TrackerApplication implements CommandLineRunner {

    private final SourceDatabaseValidator sourceDatabaseValidator;
    private final PersistenceDatabaseValidator persistenceDatabaseValidator;
    private final TransactionSyncService transactionSyncService;

    @Value("${app.db.persistent.table-name}")
    private String tableName;

    public TrackerApplication(
            SourceDatabaseValidator sourceDatabaseValidator,
            PersistenceDatabaseValidator persistenceDatabaseValidator,
            TransactionSyncService transactionSyncService) {
        this.sourceDatabaseValidator = sourceDatabaseValidator;
        this.persistenceDatabaseValidator = persistenceDatabaseValidator;
        this.transactionSyncService = transactionSyncService;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        log.info("===============================================");
        log.info("Application startup initiated");
        log.info("===============================================");

        try {
            // Validate database connections
            sourceDatabaseValidator.validateSourceDatabaseConnection();
            persistenceDatabaseValidator.validatePersistenceDatabaseConnection();

            // Initialize persistence database schema
            persistenceDatabaseValidator.initializePersistenceDatabaseSchema(tableName);

            // Start message extraction and sync pipeline
            int count = transactionSyncService.syncMessagesToTracker();

            log.info("Messages sync completed : {}", count);


            log.info("✓ Application completed successfully");

        } catch (Exception e) {
            log.error("✗ Application failed during execution [error={}]", e.getMessage(), e);
            throw new RuntimeException("Application execution failed", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TrackerApplication.class, args);
    }

}
