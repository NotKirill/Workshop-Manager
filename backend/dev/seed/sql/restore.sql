-- Replaces the dev schema with the snapshot. psql variables: anchor, admin_steam_id (may be empty)
SET client_min_messages = warning;
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\ir ../snapshot.sql
SET search_path = public;

-- Forget checksums, so a migration fixed after the snapshot still passes
UPDATE databasechangelog SET md5sum = NULL;

-- Move dates from the anchor to now: "3 days ago" stays 3 days ago, due jobs stay due
SELECT set_config('seed.shift', (now() - :'anchor'::timestamptz)::text, false);
\ir shift-dates.sql
-- The next snapshot moves them back by the same amount
SELECT format('ALTER DATABASE %I SET seed.restored_at = %L', current_database(), now()) \gexec

INSERT INTO user_identities (user_id, provider, provider_key)
SELECT user_id, 'STEAM', :'admin_steam_id'
FROM user_identities
WHERE provider = 'LOCAL_USERNAME'
  AND provider_key = 'admin'
  AND :'admin_steam_id' <> '';
