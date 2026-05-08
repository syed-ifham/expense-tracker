//package tracker.service.strategy;
//
//import tracker.model.Balance;
//import tracker.model.RawMessage;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Interface for balance extraction strategies
// */
//interface BalanceExtractor {
//    Balance extract(RawMessage rawMessage, String body);
//}
//
///**
// * SBI Balance Extractor
// * Extracts "Avl Bal INR 1,00,000.00" from credit messages
// */
//public class BalanceExtractor implements BalanceExtractor {
//    private static final String BALANCE_REGEX =
//            "(?i)Avl\\s+Bal\\s+(?:INR)?\\s+([\\d,]+\\.?\\d*)";
//    private final Pattern pattern = Pattern.compile(BALANCE_REGEX);
//
//    @Override
//    public Balance extract(RawMessage rawMessage, String body) {
//        Matcher matcher = pattern.matcher(body);
//
//        if (!matcher.find()) {
//            return null; // Balance not found, return null
//        }
//
//        String balanceStr = matcher.group(1).replace(",", "");
//        Long balanceAmount = parseAmount(balanceStr);
//
//        // Extract account number from message
//        String accountNumber = extractAccountNumber(body);
//
//        // Extract date from message
//        String date = extractDate(body);
//
//        return new Balance(
//                rawMessage.message_id(),
//                accountNumber,
//                balanceAmount,
//                date,
//                "INR",
//                rawMessage.timestamp()
//        );
//    }
//
//    private String extractAccountNumber(String body) {
//        Pattern acPattern = Pattern.compile("(?i)A/C\\s+(\\w+)");
//        Matcher matcher = acPattern.matcher(body);
//        return matcher.find() ? matcher.group(1) : "UNKNOWN";
//    }
//
//    private String extractDate(String body) {
//        // Extract date in format DD/MM/YY or DDMonYY
//        Pattern datePattern = Pattern.compile("on\\s+(\\d{2}/\\d{2}/\\d{2}|\\d{1,2}[A-Za-z]{3}\\d{2})");
//        Matcher matcher = datePattern.matcher(body);
//        return matcher.find() ? matcher.group(1) : "UNKNOWN";
//    }
//}