package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages on page, sorted by publishing date - descending.
     *
     * @param page number
     * @return list of sorted messages
     */
    @Query(
        value = "SELECT * FROM CONTENT_ITEMS c WHERE c.type = 'message' "
            + "AND c.id NOT IN (SELECT i.id from CONTENT_ITEMS i WHERE i.type = 'message' ORDER BY i.published_at DESC LIMIT 10*?1)"
            + " ORDER BY c.published_at DESC LIMIT 11",
        nativeQuery = true
    )
    List<Message> findAllByOrderByPublishedAtDesc(int page);

    /**
     * Find all messages that user has read.
     *
     * @param userId user identifier
     * @param page number
     * @return list of read messages
     */
    @Query(
        value = "SELECT * FROM CONTENT_ITEMS c WHERE c.type = 'message' AND c.id IN"
            + " (SELECT h.message_id FROM HAS_READ h WHERE h.user_id = ?1) AND c.id NOT IN "
            + "(SELECT i.id FROM CONTENT_ITEMS i WHERE i.type = 'message' AND i.id IN "
            + "(SELECT h.message_id FROM HAS_READ h WHERE h.user_id = ?1) ORDER BY i.published_at DESC LIMIT 10*?2) "
            + "ORDER BY c.published_at DESC LIMIT 11",
        nativeQuery = true)
    List<Message> findAllReadMessages(long userId, int page);

    /**
     * Find all messages that user has not read.
     *
     * @param userId user identifier
     * @param page number
     * @return list of unread messages
     */
    @Query(
        value = "SELECT * FROM CONTENT_ITEMS c WHERE c.type = 'message' AND c.id NOT IN"
            + " (SELECT h.message_id FROM HAS_READ h WHERE h.user_id = ?1) AND c.id NOT IN "
            + "(SELECT i.id FROM CONTENT_ITEMS i WHERE i.type = 'message' AND i.id NOT IN "
            + "(SELECT h.message_id FROM HAS_READ h WHERE h.user_id = ?1) ORDER BY i.published_at DESC LIMIT 10*?2) "
            + "ORDER BY c.published_at DESC LIMIT 11",
        nativeQuery = true)
    List<Message> findAllUnreadMessages(long userId, int page);
}
