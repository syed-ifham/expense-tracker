package tracker.repository.tracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tracker.entity.MessageEntity;

@Repository
public interface AppPersistentRepository extends JpaRepository<MessageEntity, Integer> {
    // JpaRepository provides save(), existsById(), and other CRUD operations
}
