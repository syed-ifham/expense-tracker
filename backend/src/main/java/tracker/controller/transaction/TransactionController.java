package tracker.controller.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tracker.entity.db.Transaction;
import tracker.entity.page.PageResponse;
import tracker.service.transaction.TransactionService;


@RestController
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/recent")
    public PageResponse<Transaction> getTransactions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageResponse<Transaction> transactions = service.getTransactions(page, size);
        return transactions;
    }

}
