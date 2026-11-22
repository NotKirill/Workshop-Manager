-- Writes sizes and hashes of the generated files. Input: "s3_key, size, sha256" lines, tab-separated.
-- Runs before the backend migrates, so it uses the snapshot's schema
CREATE TEMP TABLE seed_sizes
(
    s3_key text PRIMARY KEY,
    size   bigint NOT NULL,
    sha256 text   NOT NULL
);
\copy seed_sizes FROM pstdin

UPDATE media_files f
SET file_size = s.size
FROM seed_sizes s
WHERE f.s3_key = s.s3_key;

UPDATE item_content_revisions r
SET file_size = s.size,
    sha256    = s.sha256
FROM seed_sizes s
WHERE r.s3_key = s.s3_key;
