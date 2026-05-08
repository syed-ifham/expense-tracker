package tracker.service.strategy;

import tracker.model.Message;
import tracker.model.RawMessage;
import tracker.service.util.ServiceUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UPI Credit Pattern Matcher - SBI Format * : Dear UPI user A/C XXXXXX123123 has credit for 0000000177 DT280825 RR52 of 850.00 on 18Apr26
 */
public class UPICreditPatternMatcher implements MessagePatternMatcher {
    
    private static final String REGEX =
            "(?i)Dear\\s+UPI\\s+user\\s+A/C\\s+(\\w+)\\s+has\\s+credit\\s+for\\s+(\\w+)\\s+DT(\\d+)\\s+RR(\\w+)\\s+of\\s+([\\d.]+)\\s+on\\s+(\\w+)";
    
    private final Pattern pattern = Pattern.compile(REGEX);

    @Override
    public boolean upiMatches(String body) {
        return pattern.matcher(body).find();
    }

    @Override
    public Message upiExtract(RawMessage rawMessage, String body) {
        Matcher matcher = pattern.matcher(body);

        if (!matcher.find()) {
            throw new RuntimeException("Failed to match UPI credit pattern");
        }

//        String accountNumber = matcher.group(1);
//        String dateStr = matcher.group(3);
//        String rrCode = matcher.group(4);
        String amountStr = matcher.group(5);
//        String dateOn = matcher.group(6);

        Long amount = ServiceUtility.parseAmount(amountStr);

        return new Message(
                rawMessage.message_id(),
                rawMessage.from_address(),
                "UPI Transfer/Credit",
                amount,
                "CREDIT",
                rawMessage.timestamp()
        );
    }
}
