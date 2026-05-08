package tracker.model;

import java.util.Objects;

public record Message(
        Integer message_id,
        String from_address,
        String to_address,
        Long amount,
        String transaction,
        Long timestamp
) {
    public static Message getEmptyMessage() {
        return new Message(0, "EMPTY", "EMPTY", 0L, "EMPTY", 0L);
    }

    public boolean isEmpty() {
        return this.message_id == 0 &&
                "EMPTY".equals(this.from_address) &&
                "EMPTY".equals(this.to_address) &&
                this.amount == 0L &&
                "EMPTY".equals(this.transaction) &&
                this.timestamp == 0L;
    }

}
