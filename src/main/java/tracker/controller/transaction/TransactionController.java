package tracker.controller.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tracker.dto.MessageDto;
import tracker.service.tracker.TransactionService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<MessageDto>> getAllTransaction() {
        log.info("GET /transactions requested");
        List<MessageDto> list = transactionService.getAllTransactions();
        log.info("GET /transactions completed [count={}]", list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/transactions/recent/{Id}")
    public ResponseEntity<List<MessageDto>> getLimitedMessages(@PathVariable Integer Id) {
        log.info("GET /transactions/recent/{} requested", Id);
        List<MessageDto> list = transactionService.getLimitedTransactions(Id);
        log.info("GET /transactions/recent/{} completed [count={}]", Id, list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/transactions/recent")
    public ResponseEntity<List<MessageDto>> getLatestTransactions() {
        log.info("GET /transactions/recent requested [defaultLimit=10]");
        List<MessageDto> limitedTransactions = transactionService.getLimitedTransactions(10);
        log.info("GET /transactions/recent completed [count={}]", limitedTransactions.size());
        return ResponseEntity.ok(limitedTransactions);
    }

    @GetMapping("/transactions/range")
    public ResponseEntity<List<MessageDto>> getTransactionByDateRange(@RequestParam LocalDate startDate,
                                                                       @RequestParam LocalDate endDate) {
        log.info("GET /transactions/range requested [startDate={}, endDate={}]", startDate, endDate);
        log.warn("GET /transactions/range is currently a template endpoint");
        return ResponseEntity.ok(List.of());
    }
}
