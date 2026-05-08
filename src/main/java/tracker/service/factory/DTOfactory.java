package tracker.service.factory;

import org.springframework.stereotype.Component;
import tracker.dto.MessageDto;
import tracker.entity.MessageEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class DTOfactory {
    public List<MessageDto> fromEntity(List<MessageEntity> list) {
        return list.stream()
                .map(msg -> new MessageDto(
                        msg.getMessageId(),
                        msg.getFromAddress(),
                        msg.getToAddress(),
                        msg.getAmount(),
                        msg.getTransactionType(),
                        toLocalDateTime(msg.getTimestamp())
                ))
                .toList();
    }

    private LocalDateTime toLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        // Compute unix seconds  nanos from FILETIME 100-ns ticks since 1601-01-01
        long secondsSinceUnixEpoch = Math.floorDiv(timestamp, 10_000_000L) - 11_644_473_600L; // (timestamp/10_000_000) - 11644473600
        long ticksRemainder = Math.floorMod(timestamp, 10_000_000L);
        long nanos = ticksRemainder * 100L; // each tick = 100 ns

      Instant instant = Instant.ofEpochSecond(secondsSinceUnixEpoch, nanos);

        // Use UTC to match SQLite 'unixepoch' (which yields UTC datetime)
        return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }
}
