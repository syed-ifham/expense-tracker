//initialize()↓check()↓validate()↓populate()↓verify()↓ready()

package tracker.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tracker.startup.exception.db.AppDatabaseNotFoundException;
import tracker.startup.exception.db.AppSourceDatabaseNotFoundException;
import tracker.startup.exception.db.SourceDatabaseNotFoundException;
import tracker.startup.step.CheckDBExistStep;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class StartUpService {

    @Value("${app.path.sourceDBPath}")
    private Path sourceDbPath;

    @Value("${app.path.appSourceDBPath}")
    private Path appSourceDBPath;

    @Value("${app.path.appDBPath}")
    private Path appDBPath;

    private final String[] dbNames = {"phone.db", "phone.db-shm", "phone.db-wal"};


    private final CheckDBExistStep checkDBExistStep;

    public StartUpService(CheckDBExistStep checkDBExistStep) {
        this.checkDBExistStep = checkDBExistStep;
    }

    public void startUp() {

        // --- source reloading checkup
        // 1. check for phone link datasource
        try {
            checkDBExistStep.checkSourceDB(sourceDbPath.resolve("phone.db"));
        } catch (SourceDatabaseNotFoundException notFoundException) {
            log.warn("Source DB not found Exiting...");
            System.exit(0);
        }

        //2. check for (i) app source db (ii) compare last modify time
        try {
            checkDBExistStep.checkAppSourceDB(appSourceDBPath.resolve("phone.db"));
            for (String db : dbNames) {
                if (checkDBExistStep.checkLastModify(sourceDbPath.resolve(db), appSourceDBPath.resolve(db))) {
                    throw new AppSourceDatabaseNotFoundException("Triggering a reload to latest SMS");
                }
            }
        } catch (AppSourceDatabaseNotFoundException e) {
            log.info("Triggering Reload the App DataSource.");
            checkDBExistStep.reloadAppSourceDB(sourceDbPath, appSourceDBPath, dbNames);
        }

        // ---- source reloading done ----
        // --- start app.db checkup

        try {
            if(!Files.exists(appDBPath))
                throw new AppDatabaseNotFoundException("Triggering app.db reload cause not found");
            if(checkDBExistStep.checkLastModify(appSourceDBPath,appDBPath))
                throw new AppDatabaseNotFoundException("Triggering app.db reload cause lastest data found");
        } catch (AppDatabaseNotFoundException e){

        }

        //--- end app.db checkup

    }
}
