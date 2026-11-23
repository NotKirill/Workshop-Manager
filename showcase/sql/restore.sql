-- Replaces the schema with a snapshot. psql variables: snapshot (file), taken_at
SET client_min_messages = warning;
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
-- The default comment, so later snapshots leave the schema out, like from a new database
COMMENT ON SCHEMA public IS 'standard public schema';
\i :snapshot
SET search_path = public;

-- Forget checksums, so a migration fixed after the snapshot still passes
UPDATE databasechangelog SET md5sum = NULL;

SELECT set_config('showcase.shift', (now() - :'taken_at'::timestamptz)::text, false);
\ir shift-dates.sql
