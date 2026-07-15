package tracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.entity.db.Message;
import tracker.entity.db.RawMessage;
import tracker.entity.db.Transaction;
import tracker.repository.persist.PersistRepo;
import tracker.repository.source.SourceRepo;
import tracker.service.strategy.SmsParsingStrategy;


import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MessageProcessor {

    private final PersistRepo persistRepo;
    private final SourceRepo sourceRepo;
    private final List<SmsParsingStrategy> strategies;

    public MessageProcessor(PersistRepo persistRepo, SourceRepo sourceRepo, List<SmsParsingStrategy> strategies) {
        this.persistRepo = persistRepo;
        this.sourceRepo = sourceRepo;
        this.strategies = strategies;
    }

    public List<RawMessage> mapMessageToRawMessage() {
        List<Message> messages = sourceRepo.findAll();
        return messages.stream()
                .map(this::messageToRawMessage)
                .toList();
    }

    private RawMessage messageToRawMessage(Message message) {
        return new RawMessage(message.message_id(), message.from_address(), message.body(), message.timestamp());
    }

    public void processRawMessages(List<RawMessage> rawMessages) {
        log.info("Processing raw messages");
        int count =0;
        for (RawMessage rm : rawMessages) {
            int rowEffect = persistRepo.saveRawMessage(rm);
            if (rowEffect == 0) {
                log.trace("RawMsg: Already Exists : {rm}", rm.message_id());
                continue;
            }
            Optional<Transaction> tx = this.processRawMessage(rm);
            if (tx.isEmpty()) {
                log.trace("No transaction found for message id {rm}", rm.message_id());
                continue;
            }
            rowEffect = persistRepo.saveTransaction(tx.get());
            if (rowEffect == 0) {
                log.trace("Transaction: Already Exists : {rm}", rm.message_id());
            } else {
                log.trace("Transaction Successfully for raw message id {rm}", rm.message_id());
            }
            count++;
        }

        log.info("MsgProcessor: Successfully processed {} raw messages ", count);
    }

    private Optional<Transaction> processRawMessage(RawMessage rawMessage) {
        for (SmsParsingStrategy strategy : strategies) {
            if (strategy.isApplicable(rawMessage.sender_id(), rawMessage.raw_body())) {
                Optional<Transaction> tx = strategy.parse(rawMessage.message_id(), rawMessage.raw_body(), rawMessage.getFormattedTransactionDate());
                if (tx.isPresent()) {
                    return tx; // Extracted successfully!
                }
            }
        }
        return Optional.empty(); // No matching pattern found (could log for manual review)
    }

}