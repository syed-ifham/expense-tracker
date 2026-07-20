package tracker.startup;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartUpRunner implements CommandLineRunner {
    private final StartUpService service;
    public StartUpRunner(StartUpService service){
        this.service = service;
    }


    @Override
    public void run(String @NonNull ... args) throws Exception {

        service.startUp();

    }
}
