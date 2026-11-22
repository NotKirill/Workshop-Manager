-- Moves every app date by the interval in seed.shift
DO
$$
    DECLARE
        shift CONSTANT interval := current_setting('seed.shift')::interval;
        target         record;
    BEGIN
        FOR target IN
            SELECT table_name, string_agg(format('%1$I = %1$I + $1', column_name), ', ') AS assignments
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND data_type IN ('timestamp with time zone', 'timestamp without time zone')
              AND table_name NOT LIKE 'databasechangelog%'
              AND table_name NOT LIKE 'qrtz\_%'
            GROUP BY table_name
            LOOP
                EXECUTE format('UPDATE public.%I SET %s', target.table_name, target.assignments) USING shift;
            END LOOP;
    END
$$;
