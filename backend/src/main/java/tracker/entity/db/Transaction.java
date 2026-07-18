package tracker.entity.db;

public record Transaction(
        Long message_id,
        String payment_method,
        String direction,
        Double amount,
        String remittance,
        String transaction_date,
        String category
) {

    public boolean isExpense() {
        return "DEBIT".equalsIgnoreCase(direction);
    }
}