package tracker.service.strategy;

import tracker.model.Message;
import tracker.model.RawMessage;
import tracker.service.util.ServiceUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UPI Debit Pattern Matcher - SBI Format * : Dear UPI user A/C X123 debited by 120.00 on date 18Apr26 trf to Mr xyz KUMAR  Refno 610881503923
 */
public class UPIDebitPatternMatcher implements MessagePatternMatcher {

    private static final String REGEX =
            "(?i)Dear\\s+UPI\\s+user\\s+A/C\\s+(\\w+)\\s+debited\\s+by\\s+([\\d.]+)\\s+on\\s+date\\s+(\\w+)\\s+trf\\s+to\\s+(.*?)\\s+Refno\\s+(\\d+)";

    private final Pattern pattern = Pattern.compile(REGEX);

    @Override
    public boolean upiMatches(String body) {
        return pattern.matcher(body).find();
    }

    @Override
    public Message upiExtract(RawMessage rawMessage, String body) {
        Matcher matcher = pattern.matcher(body);

        if (!matcher.find()) {
            throw new RuntimeException("Failed to match UPI debit pattern");
        }

        //   String accountNumber = matcher.group(1);
        String amountStr = matcher.group(2);
        String recipient = matcher.group(4).trim();
        //  String dateStr = matcher.group(3);

        Long amount = ServiceUtility.parseAmount(amountStr);

        return new Message(
                rawMessage.message_id(),
                rawMessage.from_address(),
                recipient,
                amount,
                "DEBIT",
                rawMessage.timestamp()
        );
    }
}
