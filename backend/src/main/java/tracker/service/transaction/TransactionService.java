package tracker.service.transaction;

import org.springframework.stereotype.Service;
import tracker.entity.db.Transaction;
import tracker.entity.page.PageMeta;
import tracker.entity.page.PageResponse;
import tracker.repository.transaction.TransactionRepository;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    public PageResponse<Transaction> getTransactions(int page, int size) {
        List<Transaction> transactions = repo.findPaginated(page, size);
        long totalRows = repo.countAll();
        int totalPages = (int) Math.ceil((double) totalRows / size);
        PageMeta meta = new PageMeta(page, size, totalRows, totalPages);

        return new PageResponse<>(transactions, meta);
    }

}
