package tracker.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility methods for service layer
 */
@Slf4j
@Component
public class ServiceUtility {

    /**
     * Parse amount string handling various formats
     * Supports: "500", "500.50", "1,00,000.00"
     */
    public static Long parseAmount(String amountStr) {
        try {
            String cleaned = amountStr.replace(",", "");
            return (long) Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse amount [amountStr={}]", amountStr);
            throw new RuntimeException("Invalid amount format: " + amountStr, e);
        }
    }
}
