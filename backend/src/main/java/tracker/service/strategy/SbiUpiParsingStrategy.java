package tracker.service.strategy;

import org.springframework.stereotype.Service;
import tracker.entity.db.Transaction;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SbiUpiParsingStrategy implements SmsParsingStrategy {
    private static final Pattern PATTERN = Pattern.compile(
            "A/C (?<account>\\S+) (?<direction>debited|credited) by (?<amount>[\\d.]+) on date (?<date>\\d{2}[A-Za-z]{3}\\d{2}) trf to (?<remittance>.+?)(?= Refno| If not|$)"
    );

    @Override
    public boolean isApplicable(String senderId, String body) {
        return senderId.contains("SBIUPI") || body.contains("UPI user");
    }

    @Override
    public Optional<Transaction> parse(Long messageId, String body,String transactionDate) {
        Matcher matcher = PATTERN.matcher(body);
        if (!matcher.find()) return Optional.empty();

        double amount = Double.parseDouble(matcher.group("amount"));
        String direction = matcher.group("direction").toUpperCase();
        if (direction.equals("DEBITED")) direction = "DEBIT";
        if (direction.equals("CREDITED")) direction = "CREDIT";

        return Optional.of(new Transaction(
                messageId,
                "UPI",
                direction,
                amount,
                matcher.group("remittance").trim(),
                transactionDate,
                "Uncategorized"
        ));
    }
}