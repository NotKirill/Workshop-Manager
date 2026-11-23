-- Spreads "added" dates over months, game by game. "Recently added" sorts by registration order (personal id),
-- so the dates follow it: the 10 newest get the last two days
WITH game_cluster(game, days_ago) AS (VALUES ('RimWorld', 150), ('Project Zomboid', 128), ('Slay the Spire', 104),
                                             ('Don''t Starve Together', 97), ('Terraria', 71), ('tModLoader', 69),
                                             ('Cities: Skylines', 38), ('Arma 3', 16), ('Garry''s Mod', 12)),
     ranked AS (SELECT pe.id,
                       g.name,
                       row_number() OVER (ORDER BY pe.id DESC)                    AS newest_rank,
                       row_number() OVER (PARTITION BY g.id ORDER BY pe.id)       AS in_game
                FROM item_metadata_personal pe
                         JOIN item_metadata_internal i ON i.id = pe.internal_metadata_id
                         JOIN game_metadata g ON g.id = i.game_id),
     planned AS (SELECT r.id,
                        CASE
                            WHEN r.newest_rank <= 10 THEN now() - make_interval(hours => r.newest_rank::int * 4 + 1)
                            ELSE date_trunc('day', now()) - make_interval(days => c.days_ago)
                                + make_interval(hours => 19, secs => r.in_game::int * 37)
                            END AS at
                 FROM ranked r
                          JOIN game_cluster c ON c.game = r.name)
UPDATE item_metadata_personal pe
SET time_registered = p.at
FROM planned p
WHERE pe.id = p.id;
