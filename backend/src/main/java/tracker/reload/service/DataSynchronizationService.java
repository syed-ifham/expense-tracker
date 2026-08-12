package tracker.reload.service;


//require to update two tables transactions and raw_messages

import org.springframework.stereotype.Service;
import tracker.core.model.mapper.MessageMapper;
import tracker.core.service.strategy.SmsParsingStrategy;
import tracker.entity.db.RawMessage;
import tracker.core.service.MessageProcessingService;
import tracker.entity.db.SourceMessage;
import tracker.persistence.repository.RawMessageRepository;
import tracker.source.repository.SourceMessageRepository;

import java.nio.file.Path;
import java.util.List;

@Service
public class DataSynchronizationService {
    private final SourceMessageRepository sourceMessageRepository;
    private final MessageProcessingService processor;
    private final RawMessageRepository rawMessageRepository;
    private final List<SmsParsingStrategy> strategies;

    public DataSynchronizationService(MessageProcessingService processor, SourceMessageRepository sourceMessageRepository, RawMessageRepository rawMessageRepository, List<SmsParsingStrategy> strategies) {
        this.sourceMessageRepository = sourceMessageRepository;
        this.processor = processor;
        this.rawMessageRepository = rawMessageRepository;
        this.strategies = strategies;
    }

    public void syncNewMessages() {
        List<SourceMessage> sourceMessages = sourceMessageRepository.findAll();
        List<RawMessage> rawMessages = MessageMapper.toRawMessageList(sourceMessages);
        processor.processRawMessages(rawMessageRepository, rawMessages, strategies);
    }

    public void mergeTransactions(Path sourceDb, Path targetDb) {

    }

}
