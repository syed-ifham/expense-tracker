package tracker.repository.source;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import tracker.entity.db.Message;

import java.util.List;

@Repository
public class SourceRepo {
    private final JdbcClient jdbc;
    public SourceRepo(@Qualifier("sourceJdbcClient") JdbcClient jdbcClient) {
        this.jdbc = jdbcClient;
    }

    public List<Message> findAll(){
        String sql = """
                SELECT  message_id,  from_address,  body,  timestamp \s
               FROM message\s
                WHERE  from_address LIKE '%SBIUPI%'  OR from_address LIKE '%ATMSBI%'\s
                ORDER BY timestamp DESC;
               \s""";
        return jdbc.sql(sql)
                .query(Message.class).list();
    }

}
