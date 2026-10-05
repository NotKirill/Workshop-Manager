package notkirill.workshopmanager.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GameItemCountDao {
    private final JdbcClient jdbcClient;

    @Autowired
    public GameItemCountDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Map<Long, Long> countByGame(long userId) {
        Map<Long, Long> counts = new HashMap<>();
        jdbcClient.sql("""
                               SELECT i.game_id, count(*)
                               FROM item_metadata_personal p
                                        JOIN item_metadata_internal i ON i.id = p.internal_metadata_id
                               WHERE p.user_id = ?
                               GROUP BY i.game_id
                               """)
                .param(userId)
                .query(rs -> {
                    counts.put(rs.getLong(1), rs.getLong(2));
                });
        return counts;
    }

    public long count(long userId, long gameId) {
        return jdbcClient.sql("""
                                      SELECT count(*)
                                      FROM item_metadata_personal p
                                               JOIN item_metadata_internal i ON i.id = p.internal_metadata_id
                                      WHERE p.user_id = ? AND i.game_id = ?
                                      """)
                .param(userId)
                .param(gameId)
                .query(Long.class)
                .single();
    }
}
