package tracker.service.strategy;

import tracker.model.Message;
import tracker.model.RawMessage;

/**
 * Interface for different message pattern matching strategies
 */
public interface MessagePatternMatcher {
    
    // SBI-UPI-specific methods
    boolean upiMatches(String body);

    Message upiExtract(RawMessage rawMessage, String body);

}