package tracker.service.tracker;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.model.Message;
import tracker.repository.tracker.TrackerPersistentRepository;
import tracker.service.phone.TransactionParsingService;

import java.util.Collections;
import java.util.List;

/**
 * Service for synchronizing processed transaction messages from temporary database to persistent database.
 * Handles saving parsed transactions from phone.db to tracker.db.
 * Control Flow:
 * 1. Receive list of processed Message objects from RawExtractor
 * 2. Validate message data (non-null, valid amounts, timestamps)
 * 3. Check for duplicates in persistent database
 * 4. Insert new messages into persistent database
 * 5. Log results and handle errors gracefully
 */
@Slf4j
@Service
@Transactional
public class TransactionSyncService {


    private final  TransactionParsingService transactionParsingService;
    private final TrackerPersistentRepository trackerPersistentRepository;

    public TransactionSyncService(TransactionParsingService transactionParsingService, TrackerPersistentRepository trackerPersistentRepository) {
        this.transactionParsingService = transactionParsingService;
        this.trackerPersistentRepository = trackerPersistentRepository;
    }

    /**
     * Syncs processed messages to persistent database
     * @return Number of messages successfully synced
     * @throws RuntimeException if sync operation fails
     */
    public int syncMessagesToTracker() {

        List<Message> messages = transactionParsingService.parseTransactions();

        if (messages == null || messages.isEmpty()) {
            log.warn("No messages to sync [messageCount=0] [db=tracker.db]");
            return 0;
        }

        long startTime = System.currentTimeMillis();
        log.info("Starting sync to persistent database [db=tracker.db] [messageCount={}]", messages.size());

        int syncedCount = 0;
        int skippedCount = 0;

        List<String> errors = Collections.synchronizedList(new java.util.ArrayList<>());

        try {
            for (Message msg : messages) {
                try {
                    if (!isValidMessage(msg)) {
                        skippedCount++;
                        log.warn("Invalid message skipped during sync [msgId={}, db=tracker.db]", msg.message_id());
                        continue;
                    }

                    if (trackerPersistentRepository.isDuplicate(msg.message_id())) {
                        skippedCount++;
                        log.debug("Duplicate message skipped [msgId={}, db=tracker.db]", msg.message_id());
                        continue;
                    }

                    int inserted = trackerPersistentRepository.insertMessage(msg);
                    if (inserted > 0) {
                        syncedCount++;
                        log.debug("Message synced successfully [msgId={}, amount={}, type={}, db=tracker.db]",
                                  msg.message_id(), msg.amount(), msg.transaction());
                    }

                } catch (Exception e) {
                    skippedCount++;
                    String errorMsg = String.format("msgId=%d, error=%s", msg.message_id(), e.getMessage());
                    errors.add(errorMsg);
                    log.warn("Failed to sync message [msgId={}, error={}, db=tracker.db]", 
                             msg.message_id(), e.getMessage());
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("✓ Sync to persistent database completed [synced={}, skipped={}, total={}, duration={}ms, db=tracker.db]",
                     syncedCount, skippedCount, messages.size(), duration);

            if (!errors.isEmpty()) {
                log.debug("Sync errors: {}", errors);
            }

            return syncedCount;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✗ Sync to persistent database failed [duration={}ms] [error={}] [db=tracker.db]",
                      duration, e.getMessage(), e);
            throw new RuntimeException("Failed to sync messages to persistent database", e);
        }
    }

    /**
     * Validates if a message has all required fields
     */
    private boolean isValidMessage(Message msg) {
        boolean valid = msg != null &&
                msg.message_id() != null &&
                msg.from_address() != null && !msg.from_address().isEmpty() &&
                msg.to_address() != null && !msg.to_address().isEmpty() &&
                msg.amount() != null && msg.amount() > 0 &&
                msg.transaction() != null && !msg.transaction().isEmpty() &&
                msg.timestamp() != null && msg.timestamp() > 0;

        if (!valid) {
            log.debug("Message validation failed [msgId={}, valid={}]", 
                      msg != null ? msg.message_id() : "null", valid);
        }
        return valid;
    }

}
