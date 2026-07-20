package tracker.startup.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.startup.exception.db.AppSourceDatabaseNotFoundException;
import tracker.startup.exception.db.SourceDatabaseNotFoundException;
import tracker.startup.exception.db.SourceFileLockedException;

import java.io.IOException;

import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;

@Service
@Slf4j
public class CheckDBExistStep {

    //checks phone link database existence in its respective folder
    public void checkSourceDB(Path path) {
        if (!Files.exists(path)) {
            log.error("Windows Phone Link Source Database not found: {}", path);
            throw new SourceDatabaseNotFoundException("Source Database not Found");
        }
        log.info("Source Database found : {}", path);
    }

    // checks copied app database existence in app folder
    public void checkAppSourceDB(Path path) {
        if (!Files.exists(path)) {
            log.error("App Source Database not found: {}", path);
            throw new AppSourceDatabaseNotFoundException("Source Database not Found");
        }
        log.info("App Source Database found : {}", path);
    }

    public void reloadAppSourceDB(Path rawSourcePath, Path rawTargetPath, String[] dbNames) {
        try {
            if (rawTargetPath == null || rawSourcePath == null) {
                log.error("Reloading App Source DB Wrong Path Given");
                return;
            }

            if (!Files.exists(rawSourcePath)) {
                log.error("Phone Link Source DB Doesn't Exist");
                throw new SourceDatabaseNotFoundException("Phone Link Source DB Doesn't Exist");
            }

            if (!Files.exists(rawTargetPath)) {
                Files.createDirectories(rawTargetPath);
            }

            log.info("Initiating SQLite connection-level copy from {} to {}", rawSourcePath, rawTargetPath);
            for (String db : dbNames) {
                Path sourceFile = rawSourcePath.resolve(db);
                Path targetFile = rawTargetPath.resolve(db);

                if (!Files.exists(sourceFile)) {
                    log.info("Component {} not present in system directory, skipping.", db);
                    continue;
                }
                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (FileSystemException e) {
            //write handle mechanism
            log.error("Error Phone Link Source Database File/Files are LOCKED");
            throw new SourceFileLockedException("Error Phone Link Source Database File/Files are LOCKED");
        } catch (IOException e) {
            log.error("Error Occurred While Reloading the App Source Database");
        }
    }

    public boolean checkLastModify(Path source,Path target){
        if(source == null || target == null){
            log.error("Given Arguments are NULL");
            throw new RuntimeException("Given Arguments are NULL");
        }
        try {
            FileTime sourceLastModifiedTime = Files.getLastModifiedTime(source);
            FileTime targetLastModifiedTime = Files.getLastModifiedTime(target);
            return sourceLastModifiedTime.compareTo(targetLastModifiedTime) > 0;

        }catch (IOException e){
            log.error("Error Occurred while comparing database last modify");
            return true;
        }

    }

}
