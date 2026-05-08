package tracker.service.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.model.Message;
import tracker.model.RawMessage;
import tracker.service.strategy.MessagePatternMatcher;
import tracker.service.strategy.UPIDebitPatternMatcher;
import tracker.service.strategy.UPICreditPatternMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extracts transaction details and balance information from raw SMS messages.
 * Supports UPI bank transaction formats (Debit & Credit)
 */
@Slf4j
@Service
public class TransactionParsingService {

    private final PhoneMessageRetrievalService phoneMessageRetrievalService;
    private final MessagePatternMatcher upiDebitMatcher;
    private final MessagePatternMatcher upiCreditMatcher;

    public TransactionParsingService(PhoneMessageRetrievalService phoneMessageRetrievalService) {
        this.phoneMessageRetrievalService = phoneMessageRetrievalService;
        this.upiDebitMatcher = new UPIDebitPatternMatcher();
        this.upiCreditMatcher = new UPICreditPatternMatcher();
    }

    /**
     * Main entry point for parsing transactions and extracting balances
     */
    public List<Message> parseTransactions() {
        long startTime = System.currentTimeMillis();
        log.info("Message extraction process started [limit=unlimited]");

        List<Message> successfullyParsedTransactions = new ArrayList<>();
        List<String> failedMessages = new ArrayList<>();

        try {
            List<RawMessage> rawMessages = phoneMessageRetrievalService.retrieveRawMessages();

            if (rawMessages == null || rawMessages.isEmpty()) {
                log.warn("No raw messages available for extraction [count=0]");
                return Collections.emptyList();
            }

            log.info("Processing {} raw messages from temporary database [db=phone.db]",
                    rawMessages.size());

            for (RawMessage rawMsg : rawMessages) {
                try {
                    log.debug("Processing raw message [msgId={}, timestamp={}]",
                            rawMsg.message_id(), rawMsg.timestamp());

                    // Try to parse as transaction
                    Message transaction = parseTransaction(rawMsg);

                    if (!transaction.isEmpty()) {
                        successfullyParsedTransactions.add(transaction);
                        log.debug("Transaction extracted [type={}, amount={}, to={}]",
                                transaction.transaction(), transaction.amount(), transaction.to_address());
                    } else {
                        String errorMsg = String.format("msgId=%d, error=%s",
                                rawMsg.message_id(), "No transaction pattern matched");
                        failedMessages.add(errorMsg);
                        log.warn("Failed to extract transaction data [msgId={}, error={}]",
                                rawMsg.message_id(), "No transaction pattern matched");
                    }

                } catch (Exception e) {
                    String errorMsg = String.format("msgId=%d, error=%s",
                            rawMsg.message_id(), e.getMessage());
                    failedMessages.add(errorMsg);
                    log.warn("Failed to process message [msgId={}, error={}]",
                            rawMsg.message_id(), e.getMessage());
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Message extraction completed [transactions={},  failed={}, duration={}ms]",
                    successfullyParsedTransactions.size(),
                    failedMessages.size(), duration);

            if (!failedMessages.isEmpty()) {
                log.debug("Failed message details: {}", failedMessages);
            }

            return successfullyParsedTransactions;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Message extraction process failed [duration={}ms] [error={}]",
                    duration, e.getMessage(), e);
            throw new RuntimeException("Failed to extract raw messages", e);
        }
    }

    /**
     * Attempts to parse a message as a transaction
     * Returns empty message if message doesn't match any pattern
     */
    private Message parseTransaction(RawMessage rawMessage) {
        String body = rawMessage.body();

        try {
            // Try UPI debit pattern first
            if (upiDebitMatcher.upiMatches(body)) {
                return upiDebitMatcher.upiExtract(rawMessage, body);
            }

            // Try UPI credit pattern
            if (upiCreditMatcher.upiMatches(body)) {
                return upiCreditMatcher.upiExtract(rawMessage, body);
            }

        } catch (Exception e) {
            log.warn("Pattern extraction failed for message [msgId={}, error={}]",
                    rawMessage.message_id(), e.getMessage());
        }

        return Message.getEmptyMessage();
    }
}

