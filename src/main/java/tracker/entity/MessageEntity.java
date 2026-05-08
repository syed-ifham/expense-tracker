package tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "message")
public class MessageEntity {

    @Id
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "from_address", nullable = false)
    private String fromAddress;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected MessageEntity() {
        // JPA needs a no-arg constructor
    }

    public MessageEntity(Integer messageId, String fromAddress, String toAddress, Long amount, String transactionType, Long timestamp) {
        this.messageId = messageId;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
    }

    public static MessageEntity getEmptyMessageEntity() {
        return new MessageEntity(0, "EMPTY", "EMPTY", 0L, "EMPTY", 0L);
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "messageId=" + messageId +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                '}';
    }

    public boolean isEmpty() {
        return this.messageId == 0 &&
                "EMPTY".equals(this.fromAddress) &&
                "EMPTY".equals(this.toAddress) &&
                this.amount == 0L &&
                "EMPTY".equals(this.transactionType) &&
                this.timestamp == 0L;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof MessageEntity)) return false;
        MessageEntity entity = (MessageEntity) obj;
        return Objects.equals(this.messageId, entity.messageId) &&
                Objects.equals(this.fromAddress, entity.fromAddress) &&
                Objects.equals(this.toAddress, entity.toAddress) &&
                Objects.equals(this.amount, entity.amount) &&
                Objects.equals(this.transactionType, entity.transactionType) &&
                Objects.equals(this.timestamp, entity.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, fromAddress, toAddress, amount, transactionType, timestamp);
    }
}