-- Prepares the copy of the dev database for pg_dump. psql variables: anchor, restored_at, admin_steam_id
SELECT set_config('seed.shift', (:'anchor'::timestamptz - :'restored_at'::timestamptz)::text, false) \g /dev/null
\ir shift-dates.sql

-- Added again on every seed run; keeps your Steam ID out of the repository
DELETE
FROM user_identities
WHERE provider = 'STEAM'
  AND provider_key = :'admin_steam_id';

-- Without the default comment, pg_dump writes the schema too
COMMENT ON SCHEMA public IS 'standard public schema';

-- Rows rewritten in key order: unchanged data gives an unchanged snapshot
DO
$$
    DECLARE
        target record;
    BEGIN
        FOR target IN
            SELECT c.oid::regclass AS name,
                   coalesce((SELECT string_agg(quote_ident(a.attname), ', ' ORDER BY k.n)
                             FROM pg_index i
                                      CROSS JOIN unnest(i.indkey) WITH ORDINALITY AS k(attnum, n)
                                      JOIN pg_attribute a ON a.attrelid = c.oid AND a.attnum = k.attnum
                             WHERE i.indrelid = c.oid
                               AND i.indisprimary) || ', ',
                            CASE WHEN c.relname = 'databasechangelog' THEN 'orderexecuted, ' END,
                            '') AS sort_key
            FROM pg_class c
                     JOIN pg_namespace ns ON ns.oid = c.relnamespace
            WHERE ns.nspname = 'public'
              AND c.relkind = 'r'
              -- Empty anyway, and their foreign keys stop TRUNCATE
              AND c.relname NOT LIKE 'qrtz\_%'
            LOOP
                EXECUTE format('CREATE TEMP TABLE sorted AS SELECT * FROM %s', target.name);
                EXECUTE format('TRUNCATE %s', target.name);
                EXECUTE format('INSERT INTO %s OVERRIDING SYSTEM VALUE SELECT * FROM sorted ORDER BY %s sorted::text',
                               target.name, target.sort_key);
                DROP TABLE sorted;
            END LOOP;
    END
$$;
