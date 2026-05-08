package tracker.service.phone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.model.RawMessage;
import tracker.repository.phone.PhoneMessageRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PhoneMessageRetrievalService {

    private final PhoneMessageRepository phoneMessageRepository;

    public PhoneMessageRetrievalService(PhoneMessageRepository phoneMessageRepository) {
        this.phoneMessageRepository = phoneMessageRepository;
    }

    /**
     * Extracts and retrieves raw transaction messages from temporary database
     * @return List of RawMessage objects containing transaction SMS data
     * @throws RuntimeException if database query fails
     */
    public List<RawMessage> retrieveRawMessages() {
        long startTime = System.currentTimeMillis();
        log.info("Starting raw message extraction from temporary database [db=phone.db]");

        try {
            List<RawMessage> rawMessages = phoneMessageRepository.fetchMessages();

            if (rawMessages == null) {
                log.warn("Repository returned null instead of empty list [db=phone.db]");
                return Collections.emptyList();
            }

            if (rawMessages.isEmpty()) {
                log.warn("No raw messages found in temporary database [db=phone.db] [count=0]");
                return Collections.emptyList();
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("✓ Successfully fetched raw messages from temporary database [count={}] [duration={}ms] [db=phone.db]",
                     rawMessages.size(), duration);
            
            log.debug("Raw messages fetched [filters=A/C OR INR] [messageIds={}]", 
                      rawMessages.stream()
                          .map(RawMessage::message_id)
                          .limit(5)
                          .toList());

            return rawMessages;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("✗ Failed to extract raw messages from temporary database [duration={}ms] [error={}]",
                      duration, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch raw messages from phone.db", e);
        }
    }
}
