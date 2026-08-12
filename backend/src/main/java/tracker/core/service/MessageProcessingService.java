package tracker.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.core.model.mapper.MessageMapper;
import tracker.entity.db.SourceMessage;
import tracker.entity.db.RawMessage;
import tracker.entity.db.Transaction;
import tracker.persistence.repository.RawMessageRepository;
import tracker.source.repository.SourceMessageRepository;
import tracker.core.service.strategy.SmsParsingStrategy;


import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MessageProcessingService {

    public void processRawMessages(RawMessageRepository rawMessageRepository, List<RawMessage> rawMessages, List<SmsParsingStrategy> strategies) {
        log.info("Processing raw messages");
        int count = 0;

        for (RawMessage rm : rawMessages) {
            int rowEffect = rawMessageRepository.saveRawMessage(rm);
            if (rowEffect == 0) {
                log.trace("RawMsg: Already Exists : {rm}", rm.message_id());
                continue;
            }

            Optional<Transaction> tx = this.processRawMessage(rm, strategies);

            if (tx.isEmpty()) {
                log.trace("No transaction found for message id {rm}", rm.message_id());
                continue;
            }

            rowEffect = rawMessageRepository.saveTransaction(tx.get());
            if (rowEffect == 0) {
                log.trace("Transaction: Already Exists : {rm}", rm.message_id());
            } else {
                log.trace("Transaction Successfully for raw message id {rm}", rm.message_id());
            }
            count++;
        }

        log.info("MsgProcessor: Successfully processed {} raw messages ", count);
    }

    private Optional<Transaction> processRawMessage(RawMessage rawMessage, List<SmsParsingStrategy> strategies) {
        for (SmsParsingStrategy strategy : strategies) {
            if (strategy.isApplicable(rawMessage.sender_id(), rawMessage.raw_body())) {
                Optional<Transaction> tx = strategy.parse(rawMessage.message_id(), rawMessage.raw_body(), rawMessage.getFormattedTransactionDate());
                if (tx.isPresent()) {
                    return tx; // Extracted successfully!
                }
            }
        }
        return Optional.empty(); // No matching pattern found
    }

}