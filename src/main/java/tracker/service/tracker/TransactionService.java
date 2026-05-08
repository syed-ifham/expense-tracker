package tracker.service.tracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.dto.MessageDto;
import tracker.entity.MessageEntity;
import tracker.service.factory.DTOfactory;
import tracker.repository.tracker.TrackerPersistentRepository;

import java.util.List;

@Slf4j
@Service
public class TransactionService {
    private final TrackerPersistentRepository trackerPersistentRepository;
    private final DTOfactory dtoFactory;

    public TransactionService(TrackerPersistentRepository trackerPersistentRepository, DTOfactory dtoFactory) {
        this.trackerPersistentRepository = trackerPersistentRepository;
        this.dtoFactory = dtoFactory;
    }

    public List<MessageDto> getAllTransactions() {
        log.info("Fetching all transactions");
        List<MessageEntity> list = trackerPersistentRepository.getAllTransactions();
        log.info("Fetched all transactions [count={}]", list.size());
        return dtoFactory.fromEntity(list);
    }

    public List<MessageDto> getLimitedTransactions(int limit) {
        log.info("Fetching limited transactions [limit={}]", limit);
        List<MessageEntity> list = trackerPersistentRepository.getLimitedTransactions(limit);
        log.info("Fetched limited transactions [limit={}, count={}]", limit, list.size());
        return dtoFactory.fromEntity(list);
    }
}
