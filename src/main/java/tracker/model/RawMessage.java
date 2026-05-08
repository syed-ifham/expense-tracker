package tracker.model;

public record RawMessage(Integer message_id, String from_address, String body, Long timestamp) {
}
