-- Moves the app's own dates by the interval in showcase.shift. Dates from Steam stay real: a new column with a
-- source date goes in the list below
DO
$$
    DECLARE
        shift CONSTANT interval := current_setting('showcase.shift')::interval;
        target         record;
    BEGIN
        FOR target IN
            SELECT table_name, string_agg(format('%1$I = %1$I + $1', column_name), ', ') AS assignments
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND data_type IN ('timestamp with time zone', 'timestamp without time zone')
              AND table_name NOT LIKE 'databasechangelog%'
              AND table_name NOT LIKE 'qrtz\_%'
              AND table_name || '.' || column_name NOT IN (
                                                           -- source dates
                                                           'item_metadata_internal.time_updated',
                                                           'item_metadata_projection.source_time_created',
                                                           'steam_workshop_extended.time_updated',
                                                           'steam_workshop_items.time_created',
                                                           'steam_workshop_items.time_updated')
            GROUP BY table_name
            LOOP
                EXECUTE format('UPDATE public.%I SET %s', target.table_name, target.assignments) USING shift;
            END LOOP;
    END
$$;
