package tracker.controller.summary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/transactions/summary")
public class TransactionSummaryController {

    @GetMapping("/weekly")
    public ResponseEntity<String> getWeeklySummary() {
        log.info("GET /transactions/summary/weekly requested");
        log.warn("GET /transactions/summary/weekly is currently a template endpoint");
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/monthly")
    public ResponseEntity<String> getMonthlySummary() {
        log.info("GET /transactions/summary/monthly requested");
        log.warn("GET /transactions/summary/monthly is currently a template endpoint");
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/range")
    public ResponseEntity<String> getSummaryByRange() {
        log.info("GET /transactions/summary/range requested");
        log.warn("GET /transactions/summary/range is currently a template endpoint");
        return ResponseEntity.ok("OK");
    }
}
