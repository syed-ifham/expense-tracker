package tracker.service.reload;


//require to update two tables transactions and raw_messages

import org.springframework.stereotype.Service;
import tracker.entity.db.RawMessage;
import tracker.service.MessageProcessor;

import java.util.List;

@Service
public class ReloadPersistDBFile {

    private final MessageProcessor processor;
    public ReloadPersistDBFile(MessageProcessor processor) {
        this.processor = processor;
    }

    public void reload() {
        List<RawMessage> rawMessages = processor.mapMessageToRawMessage();
        processor.processRawMessages(rawMessages);
    }

}
