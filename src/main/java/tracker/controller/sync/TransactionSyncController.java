package tracker.controller.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sync")
public class TransactionSyncController {

    @PostMapping("/transactions")
    public ResponseEntity<String> syncTransactions() {
        log.info("POST /sync/transactions requested");
        log.warn("POST /sync/transactions is currently a template endpoint");
        return ResponseEntity.ok("OK");
    }
}
