package tracker.core.model.mapper;

import tracker.entity.db.SourceMessage;
import tracker.entity.db.RawMessage;

import java.util.List;

public class MessageMapper {

    /**
     * Converts SourceMessage to RawMessage
     */
    public static RawMessage toRawMessage(SourceMessage sourceMessage) {
        return new RawMessage(
                sourceMessage.messageId(),
                sourceMessage.fromAddress(),
                sourceMessage.body(),
                sourceMessage.timestamp()
        );
    }

    /**
     * Converts list of SourceMessages to list of RawMessages
     */
    public static List<RawMessage> toRawMessageList(List<SourceMessage> sourceMessages) {
        return sourceMessages.stream()
                .map(MessageMapper::toRawMessage)
                .toList();
    }

}