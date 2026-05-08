package tracker.repository.tracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import tracker.entity.MessageEntity;
import tracker.model.Message;

import java.util.List;

@Slf4j
@Repository
public class TrackerPersistentRepository {
    private final AppPersistentRepository appPersistentRepository;

    public TrackerPersistentRepository(AppPersistentRepository appPersistentRepository) {
        this.appPersistentRepository = appPersistentRepository;
    }

    public List<MessageEntity> getAllTransactions() {
        List<MessageEntity> list = appPersistentRepository.findAll();
        return list;
    }

    public List<MessageEntity> getLimitedTransactions(int limit) {
        PageRequest page = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<MessageEntity> pages = appPersistentRepository.findAll(page);
        return pages.getContent();
    }

    /**
     * Checks if a message with the same ID already exists in persistent database
     */
    public boolean isDuplicate(Integer messageId) {
        try {
            return appPersistentRepository.existsById(messageId);
        } catch (Exception e) {
            log.warn("Failed to check duplicate [msgId={}] [error={}]", messageId, e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a message into persistent database
     */
    public int insertMessage(Message msg) {

        MessageEntity messageEntity = new MessageEntity(
                msg.message_id(),
                msg.from_address(),
                msg.to_address(),
                msg.amount(),
                msg.transaction(),
                msg.timestamp()
        );
        appPersistentRepository.save(messageEntity);
        return 1;
    }
}
