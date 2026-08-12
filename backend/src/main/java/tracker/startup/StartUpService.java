//initialize()↓check()↓validate()↓populate()↓verify()↓ready()

package tracker.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tracker.persistence.schema.AppSchemaInitializer;
import tracker.reload.exception.DatabaseNotFoundException;
import tracker.reload.service.DataSynchronizationService;
import tracker.reload.service.DatabaseCopyService;
import tracker.reload.service.DatabaseVerificationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class StartUpService {

    @Value("${app.path.windowsPhoneLinkSourceDir}")
    private Path windowsPhoneLinkSourceDir;

    @Value("${app.path.appSourceDatabaseDir}")
    private Path appSourceDatabaseDir;

    @Value("${app.path.appPersistDatabaseDir}")
    private Path appPersistDatabaseDir;

    @Value("${app.db.persist.metadata.name}")
    private String appDatabaseName;

    private final String[] SOURCE_DATABASE_FILES = {"phone.db", "phone.db-shm", "phone.db-wal"};

    private final DatabaseVerificationService verify;
    private final DatabaseCopyService databaseCopyService;
    private final AppSchemaInitializer appSchemaInitializer;
    private final DataSynchronizationService dataSynchronizationService;

    public StartUpService(DatabaseCopyService databaseCopyService, DatabaseVerificationService verify, AppSchemaInitializer appSchemaInitializer, DataSynchronizationService dataSynchronizationService) {
        this.databaseCopyService = databaseCopyService;
        this.verify = verify;
        this.appSchemaInitializer = appSchemaInitializer;
        this.dataSynchronizationService = dataSynchronizationService;
    }

    public void initializeSourceDatabase() {

        // 1. check for phone link datasource
        try {
            for (String databaseFile : SOURCE_DATABASE_FILES) {
                verify.verifyExists(windowsPhoneLinkSourceDir.resolve(databaseFile));
            }
        } catch (DatabaseNotFoundException e) {
            log.error("Windows Phone Link Source DB not found Exiting...");
            System.exit(0);
        }

        //2. check for (i) windows phone.db & source/phone.db (ii) compare last modify time
        try {
            for (String databaseFile : SOURCE_DATABASE_FILES) {
                Path source = windowsPhoneLinkSourceDir.resolve(databaseFile);
                Path target = appSourceDatabaseDir.resolve(databaseFile);
                verify.verifyExists(target);

                if (verify.isSourceNewer(source, target)) {
                    throw new DatabaseNotFoundException("Source is newer, Triggering Reload...");
                }
            }

        } catch (DatabaseNotFoundException e) {
            log.error("Source Database Triggering Reloading...");
            databaseCopyService.copyDatabaseFiles(windowsPhoneLinkSourceDir, appSourceDatabaseDir, SOURCE_DATABASE_FILES);
        }

    }

    public void initializeAppDatabase() {

        try {
            // Create directory
            if (Files.notExists(appPersistDatabaseDir)) {
                Files.createDirectories(appPersistDatabaseDir);
                log.error("App Database not found: Created app database directory: {}", appPersistDatabaseDir);
            }

            // Initialize schema
            appSchemaInitializer.initializeSchema();

        } catch (IOException e) {
            log.error("Failed to initialize application database", e);
            throw new RuntimeException("Application database initialization failed", e);
        }
    }

    public void populateAppDatabase() {
        dataSynchronizationService.syncNewMessages();
    }

}
