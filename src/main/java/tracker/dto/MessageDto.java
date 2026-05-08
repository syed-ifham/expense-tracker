package tracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record MessageDto(Integer messageId, String from, String to, Long amount, String transaction,
                         @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date) {

    public static MessageDto getEmptyMessageDto() {
        return new MessageDto(0, "EMPTY", "EMPTY", 0L, "EMPTY", LocalDateTime.MIN);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.messageId == 0 &&
                "EMPTY".equals(this.from) &&
                "EMPTY".equals(this.to) &&
                this.amount == 0L &&
                "EMPTY".equals(this.transaction) &&
                LocalDateTime.MIN.equals(this.date);
    }
}
