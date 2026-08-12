package tracker.source.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import tracker.entity.db.SourceMessage;

import java.util.List;

@Repository
public class SourceMessageRepository {
    private final JdbcClient jdbc;
    public SourceMessageRepository(@Qualifier("sourceJdbcClient") JdbcClient jdbcClient) {
        this.jdbc = jdbcClient;
    }

    public List<SourceMessage> findAll(){
        String sql = """
                SELECT  message_id,  from_address,  body,  timestamp \s
               FROM message\s
                WHERE  from_address LIKE '%SBI%'\s
                ORDER BY timestamp DESC;
               \s""";
        return jdbc.sql(sql)
                .query(SourceMessage.class).list();
    }

}
