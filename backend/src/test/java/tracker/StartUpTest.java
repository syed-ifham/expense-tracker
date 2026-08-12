package tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;
import tracker.reload.exception.DatabaseNotFoundException;
import tracker.reload.exception.SourceFileLockedException;
import tracker.reload.service.DatabaseCopyService;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

class StartUpTest {

    private DatabaseCopyService databaseCopyService;
    private final String[] dbNames = {"phone.db", "phone.db-shm", "phone.db-wal"};

    @TempDir
    Path sandboxDir;
    private Path mockSourceDir;
    private Path mockTargetDir;

    @BeforeEach
    void setUp() throws IOException {
        databaseCopyService = new DatabaseCopyService();
        mockSourceDir = sandboxDir.resolve("source_system_db");
        mockTargetDir = sandboxDir.resolve("target_app_db");

        Files.createDirectories(mockSourceDir);
        Files.createDirectories(mockTargetDir);
    }

    @Test
    void shouldReturnTrue_WhenSourceIsNewerThanTarget() throws IOException {
        Path source = sandboxDir.resolve("source.db");
        Path target = sandboxDir.resolve("target.db");
        Files.createFile(source);
        Files.createFile(target);

        Files.setLastModifiedTime(source, FileTime.from(Instant.parse("2026-01-01T00:00:00Z")));
        Files.setLastModifiedTime(target, FileTime.from(Instant.parse("2025-01-01T00:00:00Z")));

        boolean result = databaseCopyService.checkLastModify(source, target);
        Assertions.assertTrue(result, "Should return true because source is newer than target");
    }

    @Test
    void shouldReturnFalse_WhenSourceIsNotNewerThanTarget() throws IOException {
        Path source = sandboxDir.resolve("source.db");
        Path target = sandboxDir.resolve("target.db");
        Files.createFile(source);
        Files.createFile(target);

        Files.setLastModifiedTime(source, FileTime.from(Instant.parse("2026-01-01T00:00:00Z")));
        Files.setLastModifiedTime(target, FileTime.from(Instant.parse("2026-01-01T00:00:00Z")));

        boolean result = databaseCopyService.checkLastModify(source, target);
        Assertions.assertFalse(result, "Should return true because source is newer than target");
    }

    @Test
    void shouldSuccessfullyCopyAllExistingDatabaseFiles() throws IOException {
        Files.createDirectories(mockTargetDir);
        for (String db : dbNames) {
            Files.writeString(mockTargetDir.resolve(db), "writing mocking data");
        }
        Assertions.assertDoesNotThrow(() -> databaseCopyService.reloadAppSourceDB(mockSourceDir, mockTargetDir, dbNames));

        Assertions.assertTrue(Files.exists(mockTargetDir));

        for (String db : dbNames) {
            Assertions.assertTrue(Files.exists(mockTargetDir.resolve(db)));
        }
    }

    @Test
    void shouldSkipGracefullyIfSomeFilesAreMissingInSource() throws IOException {
        Files.writeString(mockSourceDir.resolve("phone.db"), "core-data");

        Assertions.assertTrue(Files.exists(mockTargetDir.resolve("phone.db"))); // it exists create copy
        Assertions.assertTrue(!Files.exists(mockTargetDir.resolve("phone.db-shm"))); // doesn't exist skip it
    }

    @Test
    void shouldThrowReloadSourceDatabaseExceptionOnWhenFileLocked() throws IOException {
        Path sourceFile = mockSourceDir.resolve("phone.db");
        Files.writeString(sourceFile, "some database data");

        try(RandomAccessFile raf  = new RandomAccessFile(sourceFile.toFile(),"rw"); FileChannel channel = raf.getChannel(); FileLock lock = channel.lock()){
            if (lock != null) {
                Assertions.assertThrows(SourceFileLockedException.class, () -> {
                    databaseCopyService.reloadAppSourceDB(mockSourceDir, mockTargetDir, dbNames);
                });
            } else {
                Assertions.fail("Could not acquire an exclusive lock to perform the test.");
            }
        }

    }

    @Test
    void shouldNotThrowWhenSourceDatabaseExists() throws IOException {
        Path fakePhoneDb = sandboxDir.resolve("phone.db");
        Files.createFile(fakePhoneDb);
        Assertions.assertDoesNotThrow(() ->
                databaseCopyService.checkSourceDB(fakePhoneDb)
        );
    }

    @Test
    void shouldThrowWhenSourceDatabaseDoesNotExist() {
        Path nonExistentDb = sandboxDir.resolve("missing_phone.db");

        Assertions.assertThrowsExactly(SourceDatabaseNotFoundException.class, () ->
                databaseCopyService.checkSourceDB(nonExistentDb)
        );
    }


    @Test
    void shouldNotThrowWhenAppSourceDatabaseExists() throws IOException {
        Path fakeAppSourceDir = sandboxDir.resolve("db/source");
        Files.createDirectories(fakeAppSourceDir);
        Assertions.assertDoesNotThrow(() ->
                databaseCopyService.checkAppSourceDB(fakeAppSourceDir)
        );
    }

    @Test
    void shouldThrowWhenAppSourceDatabaseDoesNotExist() {
        Path nonExistentDir = sandboxDir.resolve("missing_app_dir");

        Assertions.assertThrowsExactly(DatabaseNotFoundException.class, () ->
                databaseCopyService.checkAppSourceDB(nonExistentDir)
        );
    }
}