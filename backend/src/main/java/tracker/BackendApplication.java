package tracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tracker.entity.db.Transaction;
import tracker.repository.persist.PersistRepo;
import tracker.service.MessageProcessor;
import tracker.service.reload.ReloadPersistDBFile;


@Slf4j
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        log.info("Application Context Loaded and App Started");
    }


}
