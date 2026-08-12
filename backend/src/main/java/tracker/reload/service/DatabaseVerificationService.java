package tracker.reload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.reload.exception.DatabaseNotFoundException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

@Slf4j
@Service
public class DatabaseVerificationService {
    public boolean verifyExists(Path path) {
        if (!Files.exists(path)) {
            log.error("Database not found: {}", path);
            throw new DatabaseNotFoundException("Database not Found");
        }
        log.info("Database found : {}", path);
        return true;
    }

    public boolean isSourceNewer(Path source, Path target) {
        Objects.requireNonNull(source, "Source path must not be null");
        Objects.requireNonNull(target, "Target path must not be null");

        if (Files.notExists(source)) {
            return false;
        }

        if (Files.notExists(target)) {
            return true;
        }

        try {
            FileTime sourceLastModifiedTime = Files.getLastModifiedTime(source);
            FileTime targetLastModifiedTime = Files.getLastModifiedTime(target);
            return sourceLastModifiedTime.compareTo(targetLastModifiedTime) > 0;
        } catch (IOException e) {
            log.error("Error occurred while comparing modification times for source [{}] and target [{}]", source, target, e);
            throw new UncheckedIOException("Failed to read file modification attributes", e);
        }
    }
}
