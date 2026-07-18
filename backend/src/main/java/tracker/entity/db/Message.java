package tracker.entity.db;

//FROM SOURCE.DB

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record Message(Long message_id, String from_address, String body, Long timestamp) {

    public String getFormattedTransactionDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return getTransactionDate().format(formatter);
    }

    private LocalDateTime getTransactionDate() {
        // 1. Convert 100-nanosecond blocks to standard milliseconds
        long millisSince1601 = this.timestamp / 10_000L;

        // 2. Milliseconds between Windows Epoch (1601) and Unix Epoch (1970)
        long windowsToUnixOffset = 11_644_473_600_000L;

        // 3. Calculate true Unix epoch milliseconds
        long epochMillis = millisSince1601 - windowsToUnixOffset;

        // 4. Map to your local system timezone (e.g., IST)
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }


}
