package tracker.service.strategy;

import org.springframework.stereotype.Service;
import tracker.entity.db.Transaction;

import java.util.Optional;

@Service
public interface SmsParsingStrategy {
    boolean isApplicable(String senderId, String body);
    Optional<Transaction> parse(Long messageId, String body,String transactionDate);



}
