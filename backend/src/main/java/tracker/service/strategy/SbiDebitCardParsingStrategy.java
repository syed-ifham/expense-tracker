package tracker.service.strategy;

import org.springframework.stereotype.Service;
import tracker.entity.db.Transaction;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SbiDebitCardParsingStrategy implements SmsParsingStrategy {
    private static final Pattern PATTERN = Pattern.compile(
            "for Rs\\.(?<amount>[\\d.]+) by .*? (?<method>Debit Card|Credit Card) (?<account>\\S+) done at (?<remittance>.+?) on (?<date>\\d{2}[A-Za-z]{3}\\d{2}) at (?<time>\\d{2}:\\d{2}:\\d{2})"
    );

    @Override
    public boolean isApplicable(String senderId, String body) {
        return body.contains("Debit Card") || senderId.contains("ATMSBI");
    }

    @Override
    public Optional<Transaction> parse(Long messageId, String body,String transactionDate) {
        Matcher matcher = PATTERN.matcher(body);
        if (!matcher.find()) return Optional.empty();

        double amount = Double.parseDouble(matcher.group("amount"));
        String method = matcher.group("method").toUpperCase().contains("DEBIT") ? "CARD" : "CREDIT_CARD";


        return Optional.of(new Transaction(
                messageId,
                method,
                "DEBIT", // debit card only for spend
                amount,
                matcher.group("remittance").trim(),
                transactionDate,
                "Uncategorized"
        ));
    }
}