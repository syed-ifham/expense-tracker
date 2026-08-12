package tracker.reload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.reload.exception.DatabaseNotFoundException;
import tracker.reload.exception.SourceFileLockedException;

import java.io.IOException;

import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
public class DatabaseCopyService {

    public void copyDatabaseFiles(Path sourceDir, Path targetDir, String[] SOURCE_DATABASE_FILES) {
        Objects.requireNonNull(sourceDir, "Source path must not be null");
        Objects.requireNonNull(targetDir, "Target path must not be null");
        Objects.requireNonNull(SOURCE_DATABASE_FILES, "Database names array must not be null");

        if (Files.notExists(sourceDir)) {
            log.error("Phone Link Source DB path does not exist: {}", sourceDir);
            throw new DatabaseNotFoundException("Phone Link Source DB path does not exist: " + sourceDir);
        }

        try {
            if (Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            log.info("Copying database files from {} to {}", sourceDir, targetDir);

            for (String db : SOURCE_DATABASE_FILES) {
                Path sourceFile = sourceDir.resolve(db);
                Path targetFile = targetDir.resolve(db);

                if (Files.notExists(sourceFile)) {
                    log.info("Component {} not present in system directory, skipping.", db);
                    continue;
                }

                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (FileSystemException e) {
            log.error("File system error during database copy from {} to {}", sourceDir, targetDir, e);
            // Throw with 'e' attached so root cause isn't lost
            throw new SourceFileLockedException("Database file access issue or file locked: " + e.getReason());
        } catch (IOException e) {
            log.error("Error occurred while copying database files from {} to {}", sourceDir, targetDir, e);
            throw new UncheckedIOException("Failed to copy database files", e);
        }
    }

}
