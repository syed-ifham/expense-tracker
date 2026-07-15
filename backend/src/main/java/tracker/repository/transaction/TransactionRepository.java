package tracker.repository.transaction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import tracker.entity.db.Transaction;

import java.util.List;

@Repository
public class TransactionRepository {
    private final JdbcClient jdbc;

    public TransactionRepository(@Qualifier("persistJdbcClient") JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Transaction> findPaginated(int page, int size) {
        int offset = page * size; // 0-indexed page
        String sql = """
                SELECT message_id, payment_method, direction, amount, remittance, transaction_date, category 
                FROM transactions 
                ORDER BY message_id DESC 
                LIMIT :limit OFFSET :offset
                """;
        return jdbc.sql(sql)
                .param("limit", size)
                .param("offset", offset)
                .query(Transaction.class)
                .list();

    }

    public long countAll() {
        return this.jdbc.sql("SELECT COUNT(*) FROM transactions")
                .query(Long.class)
                .single();
    }

    public List<Transaction> findLastMonthCredit() {
        String sql = """
    SELECT 
        message_id,
        payment_method,
        direction,
        amount,
        remittance,
        transaction_date,
        category
    FROM transactions
    WHERE transaction_date >= DATE('now', '-30 days')
    AND direction = 'CREDIT'
    ORDER BY message_id DESC;
    """;

        return jdbc.sql(sql)
                .query(Transaction.class)
                .list();
    }

    public List<Transaction> findLastMonthDebit() {
        String sql = """
                SELECT 
                    message_id,
                    payment_method,
                    direction,
                    amount,
                    remittance,
                    transaction_date,
                    category
                FROM transactions
                WHERE transaction_date >= DATE('now', '-30 days')
                AND direction = 'DEBIT'
                ORDER BY message_id DESC;
                """;

        return jdbc.sql(sql)
                .query(Transaction.class)
                .list();
    }

}
