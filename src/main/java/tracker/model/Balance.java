package tracker.model;

public record Balance(
        Integer message_id,
        String account_number,
        Long balance_amount,
        String balance_date,
        String currency,
        Long timestamp
) {
}
