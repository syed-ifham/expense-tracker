package tracker.service.reload;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;

//@Service
public class ReloadSourceDBFile {

    //@Value("${app.path.sourceDbPath}")
    private final Path sourceDbPath = Path.of("C:/Users/syedi/AppData/Local/Packages/Microsoft.YourPhone_8wekyb3d8bbwe/LocalCache/Indexed/fe43b75f-00e7-4bb9-b9c5-077218994971/System/Database");
    //@Value("${app.path.targetDbPath}")
    private final Path appDbPath = Path.of("C:/Users/syedi/Desktop/Projects/smart-fin-tracker/backend/db/source");

    private final String[] fileNames = {"phone.db", "phone.db-shm", "phone.db-wal"};

    public static void reloadSourceDb(ReloadSourceDBFile service) {
        boolean reload = false;
        for (String str : service.fileNames) {
            boolean val = service.checkLastModify(service.sourceDbPath.resolve(str), service.appDbPath.resolve(str));
            if (val) {
                reload = true;
                break;
            }
        }

        if (!reload) {
            return;
        }

        for (String str : service.fileNames) {
            service.copyFile(service.sourceDbPath.resolve(str), service.appDbPath.resolve(str));
            System.out.println("copying files done : " + str);
        }

    }

    private boolean checkLastModify(Path source, Path target) {
        try {
            if (!Files.exists(source))
                throw new RuntimeException("Source File doesn't exist");
            if (!Files.exists(target))
                return true;

            FileTime sourceLastModifiedTime = Files.getLastModifiedTime(source);
            FileTime targetLastModifiedTime = Files.getLastModifiedTime(target);

            System.out.println(sourceLastModifiedTime + " " + targetLastModifiedTime);

            return sourceLastModifiedTime.compareTo(targetLastModifiedTime) > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFile(Path source, Path target) {
        try {
            //  destination folder exists check
            if (target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileSystemException e) {
            // FileSystemException handles "The process cannot access the file because it is being used by another process"
            System.err.println("Copy failed: The file is currently LOCKED by another process.");

            // Handle the lock (e.g., wait and retry, or alert the user to close the app)
        } catch (IOException e) {
            System.err.println("Copy failed due to general I/O error: " + e.getMessage());
        }
    }

}
