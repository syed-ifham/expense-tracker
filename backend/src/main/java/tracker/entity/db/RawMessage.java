package tracker.entity.db;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record RawMessage(
        Long message_id,
        String sender_id,
        String raw_body,
        Long sms_timestamp
) {

    public String getFormattedTransactionDate() {
        LocalDateTime dateTime = getTransactionDate();
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private LocalDateTime getTransactionDate() {
        if (sms_timestamp == null) return null;

        long millisSince1601 = this.sms_timestamp / 10_000L;
        long windowsToUnixOffset = 11_644_473_600_000L;
        long epochMillis = millisSince1601 - windowsToUnixOffset;

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }


}
