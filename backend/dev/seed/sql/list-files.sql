-- Lists stored files for files.tsv: s3_key, kind, label, variant (tab-separated).
-- Kind and label only set the placeholder's size and caption, so a rough guess is fine
CREATE TEMP VIEW item_titles AS
SELECT i.id,
       coalesce(pr.title,
                (SELECT min(pe.title) FROM item_metadata_personal pe WHERE pe.internal_metadata_id = i.id),
                i.original_id) AS title
FROM item_metadata_internal i
         LEFT JOIN item_metadata_projection pr ON pr.internal_id = i.id;

CREATE TEMP VIEW seed_files AS
SELECT f.s3_key,
       CASE
           WHEN l.owner_user_id IS NOT NULL THEN 'upload'
           WHEN l.purpose = 'cover' THEN 'cover'
           WHEN l.purpose = 'showcase' THEN 'shot'
           ELSE 'preview'
           END,
       CASE
           WHEN l.entity = 'GAME' THEN (SELECT g.name FROM game_metadata g WHERE g.id = l.entity_id)
           WHEN l.purpose = 'showcase' THEN 'Screenshot ' || l.sort_order || ': ' || t.title
           ELSE coalesce(t.title, f.file_name)
           END,
       f.media_item_id
FROM media_files f
         LEFT JOIN LATERAL (SELECT *
                            FROM media_links ml
                            WHERE ml.media_item_id = f.media_item_id
                            ORDER BY ml.id
                            LIMIT 1) l ON true
         LEFT JOIN item_titles t ON l.entity = 'ITEM' AND t.id = l.entity_id
UNION ALL
SELECT r.s3_key, 'archive', t.title || ' (revision ' || r.revision || ')', r.item_content_id * 7 + r.revision
FROM item_content_revisions r
         JOIN item_contents c ON c.id = r.item_content_id
         JOIN item_titles t ON t.id = c.internal_metadata_id;

\copy (SELECT * FROM seed_files ORDER BY 1) TO pstdout
