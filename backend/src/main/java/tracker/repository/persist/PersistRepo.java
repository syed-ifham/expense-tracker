package tracker.repository.persist;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import tracker.entity.db.RawMessage;
import tracker.entity.db.Transaction;

import java.util.List;

@Repository
public class PersistRepo {
    private final JdbcClient jdbc;

    public PersistRepo(@Qualifier("persistJdbcClient") JdbcClient jdbcClient) {
        this.jdbc = jdbcClient;
    }

    public int saveRawMessage(RawMessage rawMessage) {
        String sql = "INSERT OR IGNORE INTO raw_messages (message_id, sender_id, raw_body, sms_timestamp)\n" +
                "VALUES (:message_id, :sender_id, :raw_body, :sms_timestamp)";
        return jdbc.sql(sql)
                .paramSource(rawMessage)
                .update();
    }

    public int saveTransaction(Transaction tx) {
        String sql = "INSERT INTO transactions (message_id, payment_method, direction, amount, remittance, transaction_date, category)\n" +
                "VALUES (:message_id, :payment_method, :direction, :amount, :remittance, :transaction_date, :category)";
        return jdbc.sql(sql)
                .paramSource(tx)
                .update();
    }

    public List<RawMessage> findAllRawMessages() {
        String sql = "SELECT * FROM raw_messages";
        return jdbc.sql(sql).query(RawMessage.class).list();
    }

    public List<Transaction> findAllTransactions() {
        String sql = "SELECT * FROM transactions";
        return jdbc.sql(sql).query(Transaction.class).list();
    }

    public List<Transaction> findRecentTransactions() {
        String sql = "SELECT * FROM transactions ORDER BY message_id DESC LIMIT 10";
        return jdbc.sql(sql).query(Transaction.class).list();
    }

    public Transaction findTransactionById(long id) {
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
        WHERE message_id = ?
        """;
        return jdbc.sql(sql)
                .params(id)
                .query(Transaction.class).single();
    }
}
