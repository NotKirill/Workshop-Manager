-- Workshop Manager: generic data for testing the dev stack by hand
--
-- Empties every application table and fills it again, in one transaction, so running it twice gives the same
-- state. Liquibase and Quartz tables are left alone. Written for the schema after the changeset named in the
-- version check below; it refuses to run on any other version
--
-- Run: see README.md

BEGIN;

-- Options set by run.sh before this file runs. Run on its own, the defaults apply
CREATE TEMP TABLE IF NOT EXISTS seed_settings
(
    with_files     boolean NOT NULL DEFAULT false,
    admin_steam_id text
);
INSERT INTO seed_settings
SELECT
WHERE NOT EXISTS (SELECT FROM seed_settings);

-- 1. Version check -------------------------------------------------------------------------------------------

DO
$$
    DECLARE
        expected CONSTANT text := 'db/changelog/application/022-user-item-metadata-domain.yaml::022';
        latest            text;
    BEGIN
        IF to_regclass('public.databasechangelog') IS NULL THEN
            RAISE EXCEPTION 'No schema yet: start the backend once so Liquibase creates it, then run the seed';
        END IF;
        SELECT filename || '::' || id
        INTO latest
        FROM databasechangelog
        WHERE filename LIKE 'db/changelog/application/%'
        ORDER BY orderexecuted DESC
        LIMIT 1;
        IF latest = expected THEN
            RETURN;
        END IF;
        IF NOT EXISTS (SELECT FROM databasechangelog WHERE filename || '::' || id = expected) THEN
            RAISE EXCEPTION 'The database is older than the seed (latest changeset: %). Start the backend once so '
                'Liquibase applies the new migrations, then run the seed again', latest;
        END IF;
        RAISE EXCEPTION 'The database has migrations newer than the seed (latest changeset: %, the seed was written '
            'for %). Update seed.sql for the new schema and its version check', latest, expected;
    END
$$;

-- 2. Reset ---------------------------------------------------------------------------------------------------

DO
$$
    DECLARE
        tables text;
    BEGIN
        SELECT string_agg(format('%I', tablename), ', ')
        INTO tables
        FROM pg_tables
        WHERE schemaname = 'public'
          AND tablename NOT LIKE 'databasechangelog%'
          AND tablename NOT LIKE 'qrtz\_%';
        EXECUTE 'TRUNCATE TABLE ' || tables;
    END
$$;

-- The one row a migration inserts (017)
INSERT INTO game_metadata (id, name, slug)
VALUES (-1, 'Unknown game', 'unknown');

DO $$ BEGIN PERFORM setseed(0.42); END $$;

-- Files run.sh generates and uploads after the commit: one row per stored object
DROP TABLE IF EXISTS pg_temp.seed_uploads;
CREATE TEMP TABLE seed_uploads
(
    s3_key  text PRIMARY KEY,
    kind    text NOT NULL,
    label   text NOT NULL,
    variant int  NOT NULL
);

-- 3. Users ---------------------------------------------------------------------------------------------------
-- Passwords: admin -> Admin123!dev (same as the dev bootstrap admin), everyone else -> User123!dev

INSERT INTO users (id, password_hash, time_created, name, enabled, account_non_locked, permissions)
VALUES (1, '$2a$10$Q18eNgbPW7fHblXhD08tjOrgs5fI4oaw8L/f9f1ne.yYpEI6ApX7.', now() - interval '400 days',
        'Mewtwo', true, true,
        '{ITEMS_READ,ITEMS_WRITE,ITEMS_REGISTER,CONTENT_IMPORT,MEDIA_UPLOAD,USERS_MANAGE,GAMES_MANAGE,CONTENT_MANAGE,MAINTENANCE_MANAGE}'),
       (2, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '320 days',
        'Pikachu', true, true,
        '{ITEMS_READ,ITEMS_WRITE,ITEMS_REGISTER,CONTENT_IMPORT,MEDIA_UPLOAD,GAMES_MANAGE,CONTENT_MANAGE}'),
       (3, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '250 days',
        'Ditto', true, true,
        '{ITEMS_READ,ITEMS_WRITE,ITEMS_REGISTER,CONTENT_IMPORT,MEDIA_UPLOAD}'),
       (4, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '180 days',
        'Eevee', true, true,
        '{ITEMS_READ,ITEMS_WRITE,ITEMS_REGISTER}'),
       (5, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '120 days',
        'Psyduck', true, true,
        '{ITEMS_READ}'),
       (6, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '90 days',
        'Snorlax', false, true,
        '{ITEMS_READ,ITEMS_WRITE}'),
       (7, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '60 days',
        'Magikarp', true, false,
        '{ITEMS_READ,ITEMS_WRITE}'),
       (8, NULL, now() - interval '45 days',
        'Charizard', true, true,
        '{ITEMS_READ,ITEMS_WRITE,ITEMS_REGISTER}'),
       (9, '$2a$10$34ciPVt9Yc4GuU66r4xJMuK9Bx89YZ9wfy.ZX9DJjKyrj.N/3lp.q', now() - interval '30 days',
        'Rotom', true, true,
        '{ITEMS_READ,USERS_MANAGE,MAINTENANCE_MANAGE}');

INSERT INTO user_identities (user_id, provider, provider_key)
VALUES (1, 'LOCAL_USERNAME', 'admin'),
       (2, 'LOCAL_USERNAME', 'pikachu'),
       (2, 'LOCAL_EMAIL', 'pikachu@example.test'),
       (3, 'LOCAL_USERNAME', 'ditto'),
       (4, 'LOCAL_USERNAME', 'eevee'),
       (4, 'LOCAL_EMAIL', 'eevee@example.test'),
       (5, 'LOCAL_USERNAME', 'psyduck'),
       (6, 'LOCAL_USERNAME', 'snorlax'),
       (7, 'LOCAL_USERNAME', 'magikarp'),
       (8, 'STEAM', '76561190000000008'),
       (9, 'LOCAL_EMAIL', 'rotom@example.test');

INSERT INTO user_identities (user_id, provider, provider_key)
SELECT 1, 'STEAM', admin_steam_id
FROM seed_settings
WHERE admin_steam_id IS NOT NULL
  AND admin_steam_id <> '';

INSERT INTO user_shared_permissions (user_provider_id, user_consumer_id, permission)
VALUES (3, 4, 'ITEMS_READ'),
       (2, 3, 'ITEMS_READ'),
       (2, 3, 'ITEMS_WRITE');

-- 4. Games ---------------------------------------------------------------------------------------------------
-- Made-up games and Steam app ids. name_requested_at stays empty, so no name lookup asks Steam.

CREATE TEMP TABLE seed_game
(
    id         bigint PRIMARY KEY,
    name       text   NOT NULL,
    slug       text   NOT NULL,
    app_id     text,
    item_ids   int[]  NOT NULL,
    core_title text,
    adjectives text[],
    nouns      text[],
    tags       text[]
) ON COMMIT DROP;

INSERT INTO seed_game
VALUES (1, 'Ironclad Frontier', 'ironclad-frontier', '9100010',
        ARRAY(SELECT generate_series(1, 40)), 'Frontier Core Library',
        '{Brass,Rusty,Steam-Powered,Copper,Iron,Frontier,Clockwork,Dusty,Riveted,Smoky}',
        '{Locomotives,Revolvers,Airships,Saloons,Railways,Outposts,Dirigibles,Boilers,Bounties,Mines}',
        '{Maps,Vehicles,Weapons,Gameplay,UI,Audio,Characters}'),
       (2, 'Starlane Haulers', 'starlane-haulers', '9100020',
        ARRAY(SELECT generate_series(41, 70)) || ARRAY [145, 146, 147], 'Starlane Core Framework',
        '{Deep-Space,Quantum,Long-Haul,Ion,Orbital,Stellar,Cargo,Nebula,Warp,Solar}',
        '{Freighters,Cockpits,"Trade Routes","Docking Bays",Engines,Containers,Stations,Beacons,Shields,Radios}',
        '{Ships,Economy,Stations,UI,Audio,Missions}'),
       (3, 'Hollow Grove', 'hollow-grove', '9100030',
        ARRAY(SELECT generate_series(71, 95)), 'Hollow Grove Mod Loader',
        '{Mossy,Enchanted,Cozy,Moonlit,Wild,Ancient,Blooming,Misty,Hidden,Golden}',
        '{Gardens,Cottages,Familiars,Potions,Orchards,Bridges,Lanterns,Mushrooms,Festivals,Recipes}',
        '{Decor,Crops,Characters,"Quality of Life",Seasons,Portraits}'),
       (4, 'Circuit Kingdom', 'circuit-kingdom', '9100040',
        ARRAY(SELECT generate_series(96, 115)) || ARRAY [148, 149, 150], 'Circuit Kingdom Script Extender',
        '{Modular,Overclocked,Compact,Smart,Neon,Industrial,Automated,Efficient,Vintage,Heavy}',
        '{Factories,Conveyors,"Power Grids",Districts,Robots,Assemblers,Highways,Reactors,Blueprints,Signals}',
        '{Buildings,Logistics,Balance,Scenarios,Graphics}'),
       (5, 'Tidewater Tactics', 'tidewater-tactics', '9100050',
        ARRAY(SELECT generate_series(116, 128)), 'Tidewater Shared Assets',
        '{Coastal,Stormy,Royal,Pirate,Northern,Armored,Swift,Old-World,Salt-Worn,Grand}',
        '{Fleets,Harbors,Campaigns,Frigates,Admirals,Islands,Cannons,Flags,Charts,Lighthouses}',
        '{Units,Maps,Campaigns,AI,Historical}'),
       (6, 'Ironclad Frontier Classic', 'ironclad-frontier-classic', '9100060',
        ARRAY(SELECT generate_series(129, 132)), 'Classic Compatibility Patch',
        '{Brass,Rusty,Steam-Powered,Copper,Iron,Frontier,Clockwork,Dusty,Riveted,Smoky}',
        '{Locomotives,Revolvers,Airships,Saloons,Railways,Outposts,Dirigibles,Boilers,Bounties,Mines}',
        '{Maps,Vehicles,Gameplay}'),
       (7, 'Pixel Parish', 'pixel-parish', NULL, '{}', NULL, NULL, NULL, NULL);

INSERT INTO game_metadata (id, name, slug)
SELECT id, name, slug
FROM seed_game;

INSERT INTO game_to_modspace_ids (game_id, source_modspace, external_id)
SELECT id, 'STEAM', app_id
FROM seed_game
WHERE app_id IS NOT NULL;

INSERT INTO game_compatibility (source_game_id, target_game_id)
VALUES (6, 1),
       (1, 6);

-- 5. Authors -------------------------------------------------------------------------------------------------

INSERT INTO author_metadata (id, source_modspace, original_id, name, time_synced)
SELECT n, 'STEAM', (76561190000000100 + n)::text, name, now() - (n || ' hours')::interval
FROM unnest('{Brassfang,"Mossy Marla",Quillwright,"Captain Nib",Ferrocat,"Juno Vex",Tinkerbolt,Saltreader,
             "Old Ironsides",Lanternfox,"Pixel Pilgrim",Gearhart,Wavecrest,"Rook & Rye",Cinderella42,Hexwright}'::text[])
         WITH ORDINALITY AS a(name, n);

-- 6. Items ---------------------------------------------------------------------------------------------------

CREATE TEMP TABLE seed_item
(
    id          bigint PRIMARY KEY,
    game_id     bigint      NOT NULL,
    n           int         NOT NULL,
    source      text        NOT NULL,
    original_id text        NOT NULL,
    lifecycle   text        NOT NULL,
    available   boolean     NOT NULL,
    title       text        NOT NULL,
    description text,
    author_id   bigint,
    file_size   bigint,
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    children    text[]      NOT NULL DEFAULT '{}'
) ON COMMIT DROP;

INSERT INTO seed_item (id, game_id, n, source, original_id, lifecycle, available, title, author_id, file_size,
                       created_at)
SELECT i.id,
       g.id,
       i.n - 1,
       'STEAM',
       (3900000000 + i.id)::text,
       CASE WHEN i.id BETWEEN 145 AND 147 THEN 'ORPHANED' WHEN i.id BETWEEN 148 AND 150 THEN 'FAILED' ELSE 'ACTIVE' END,
       i.id % 19 <> 0,
       CASE
           WHEN i.n = 1 THEN g.core_title
           ELSE g.adjectives[1 + (i.n - 1) % 10] || ' ' || g.nouns[1 + ((i.n - 1) / 10 + (i.n - 1) * 3) % 10]
               || (ARRAY ['', '', ' Pack', ' Overhaul', ' Redux', ' Expanded', ' (Lite)', ' Remastered'])[1 + ((i.n - 1) * 5) % 8]
           END,
       1 + (i.id * 5) % 16,
       (20000 + floor(random() * 400000000))::bigint,
       now() - ((60 + (i.id * 37) % 900) || ' days')::interval
FROM seed_game g
         CROSS JOIN LATERAL unnest(g.item_ids) WITH ORDINALITY AS i(id, n);

UPDATE seed_item
SET updated_at = created_at + ((1 + (id * 11) % greatest(extract(DAY FROM now() - created_at)::int - 2, 1)) || ' days')::interval
WHERE source = 'STEAM';

-- Dependencies
UPDATE seed_item s
SET children = ARRAY(SELECT c.original_id FROM seed_item c WHERE c.game_id = s.game_id AND c.n = 0)
                   || CASE WHEN s.n % 6 = 0 THEN ARRAY [(3900000000 + s.id - 1)::text] ELSE '{}' END
WHERE s.source = 'STEAM'
  AND s.n >= 2
  AND s.n % 3 = 0;
UPDATE seed_item
SET children = children || '{3999999999}'
WHERE id = 3;

-- Generated descriptions in Steam BBCode
UPDATE seed_item s
SET description = format(E'[h2]Overview[/h2]\n%s brings fresh %s to %s. Tested with the latest game update.\n\n',
                         s.title, lower(regexp_replace(s.title, '^\S+\s+', '')), g.name)
    || CASE s.n % 4
           WHEN 0 THEN E'[b]Features[/b]\n[list]\n[*]Hand-made textures and models\n[*]Balanced for multiplayer\n'
               || E'[*]Works with existing saves\n[/list]\n\n[i]Load it after the core item of the game.[/i]'
           WHEN 1 THEN E'[h3]Changelog[/h3]\n[quote=author]1.2: fixed the crash on load.\n1.1: new icons.[/quote]\n\n'
               || E'Questions? Join the [url=https://example.com/discord]community server[/url].'
           WHEN 2 THEN E'[b]Configuration[/b]\n[code]\nenabled = true\nspawn_rate = 0.75\n[/code]\n\n'
               || E'[spoiler]There is a secret variant in the last chapter.[/spoiler]'
           ELSE E'[olist]\n[*]Subscribe\n[*]Enable it in the mod list\n[*]Start a new game[/olist]\n\n'
               || E'[noparse][b]This stays literal[/b][/noparse] [strike]old notes[/strike]'
        END
FROM seed_game g
WHERE g.id = s.game_id;

UPDATE seed_item
SET description = E'[h1]Frontier Core Library[/h1]\nShared scripts and assets used by most Ironclad Frontier mods.\n\n'
    || E'[b]For players:[/b] just subscribe, other mods load it for you.\n'
    || E'[b]For modders:[/b] see the [url=https://example.com/wiki/frontier-core]wiki[/url].\n\n'
    || E'[table]\n[tr][th]Version[/th][th]Game version[/th][/tr]\n[tr][td]3.4[/td][td]1.9+[/td][/tr]\n'
    || E'[tr][td]3.3[/td][td]1.8[/td][/tr]\n[/table]'
WHERE id = 1;
UPDATE seed_item
SET title       = 'Brass Locomotives Expanded',
    description = E'[h1]Brass Locomotives Expanded[/h1]\n[img]https://example.com/banner.png[/img]\n'
        || E'Twelve new locomotives, from the tiny [i]Copper Kettle[/i] to the [b]Ironclad Behemoth[/b].\n\n'
        || E'[list]\n[*]Full interiors\n[*]Custom whistle sounds\n[*][u]Night lights[/u]\n[/list]\n\n'
        || E'[quote=Brassfang]Took two years. Worth it.[/quote]'
WHERE id = 2;
UPDATE seed_item
SET title       = 'Saloon Life Overhaul',
    description = E'Makes saloons busy again: card games, piano players and the odd brawl.\n\n'
        || E'[b]Requires[/b] Frontier Core Library and one item nobody registered yet (to show unknown dependencies).'
WHERE id = 3;
UPDATE seed_item
SET description = E'[h1]Starlane Core Framework[/h1]\nThe base every Starlane Haulers mod builds on.\n\n'
    || E'[code]\nload_order = first\n[/code]'
WHERE id = 41;
UPDATE seed_item
SET description = E'[h1]Mod Loader[/h1]\nLoads mods in Hollow Grove. [i]Install this first.[/i]'
WHERE id = 71;

-- Local items
INSERT INTO seed_item (id, game_id, n, source, original_id, lifecycle, available, title, description, created_at)
SELECT id,
       game_id,
       0,
       'LOCAL',
       md5('local-' || id)::uuid::text,
       'ACTIVE',
       true,
       title,
       description,
       now() - ((id - 120) || ' days')::interval
FROM (VALUES (133, 7, 'My Village Layout', 'Where every house goes, drawn on paper first.'),
             (134, 7, 'Church Bell Sounds', 'Recorded at the old chapel, cleaned up in an audio editor.'),
             (135, 7, 'Hand-drawn Map Tiles', NULL),
             (136, 7, 'Festival Banner Set', 'Banners for the summer and winter festivals.'),
             (137, 7, 'Townsfolk Names List', E'[list]\n[*]Agnes\n[*]Bartholomew\n[*]Cuthbert\n[/list]'),
             (138, 7, 'Night Palette Swap', NULL),
             (139, 1, 'Personal Balance Tweaks', 'Cheaper boilers, slower bandits.'),
             (140, 1, 'Saloon Music Playlist', NULL),
             (141, 3, 'Garden Layout Notes', 'Which crops grow next to which.'),
             (142, 3, 'Portrait Recolors', NULL),
             (143, 4, 'Factory Ratios Spreadsheet', 'Exported from a spreadsheet as CSV.'),
             (144, 4, 'Signal Layout Templates', NULL)) AS l(id, game_id, title, description);

INSERT INTO item_metadata_internal (id, source_modspace, game_id, original_id, time_registered, time_updated,
                                    lifecycle_state, time_synced, source_status)
SELECT id,
       source::modspace_type,
       game_id,
       original_id,
       created_at + interval '3 days',
       updated_at,
       lifecycle::item_lifecycle_state,
       CASE WHEN source = 'STEAM' THEN now() END,
       CASE WHEN available THEN 'AVAILABLE' ELSE 'UNAVAILABLE' END::item_source_status
FROM seed_item;

INSERT INTO item_metadata_projection (internal_id, title, description, author_id, file_size, source_time_created)
SELECT id, title, description, author_id, file_size, created_at
FROM seed_item
WHERE source = 'STEAM';

-- Orphaned items wait for the cleanup job
INSERT INTO item_cleanups (internal_metadata_id, orphaned_at, attempts, next_attempt_at, last_error)
SELECT id,
       CASE WHEN lifecycle = 'ORPHANED' THEN now() ELSE now() - interval '3 days' END,
       CASE WHEN lifecycle = 'ORPHANED' THEN 0 ELSE 5 END,
       NULL,
       CASE WHEN lifecycle = 'FAILED' THEN 'Cannot delete stored objects: connection refused (seed data)' END
FROM seed_item
WHERE lifecycle IN ('ORPHANED', 'FAILED');

-- 7. Steam workshop data -------------------------------------------------------------------------------------

INSERT INTO steam_workshop_items (local_id, publishedfileid, creator, creator_app_id, consumer_app_id, file_size,
                                  preview_url, title, description, time_created, time_updated, visibility,
                                  subscriptions, favorited, lifetime_subscriptions, lifetime_favorited)
SELECT s.id,
       s.original_id,
       a.original_id,
       g.app_id::bigint,
       g.app_id::bigint,
       s.file_size::text,
       NULL,
       s.title,
       s.description,
       s.created_at,
       s.updated_at,
       CASE WHEN s.id % 23 = 0 THEN 3 WHEN s.id % 29 = 0 THEN 1 ELSE 0 END,
       sub,
       sub / 4,
       sub * 3,
       sub / 3
FROM seed_item s
         JOIN seed_game g ON g.id = s.game_id
         JOIN author_metadata a ON a.id = s.author_id
         CROSS JOIN LATERAL (SELECT CASE WHEN s.n = 0 THEN 250000 + s.id * 1000 ELSE floor(random() * 60000)::int END) AS x(sub)
WHERE s.source = 'STEAM';

INSERT INTO steam_workshop_extended (local_id, preview_file_size, short_description, num_children, previews, children,
                                     kvtags, maybe_inappropriate_sex, maybe_inappropriate_violence,
                                     content_descriptorids, time_updated)
SELECT s.id,
       (40000 + s.id * 731)::text,
       left(s.title || ' for ' || g.name || '.', 200),
       cardinality(s.children),
       '[]'::jsonb,
       (SELECT coalesce(jsonb_agg(jsonb_build_object('publishedfileid', c.id, 'sortorder', c.n - 1, 'file_type', 0)),
                        '[]'::jsonb)
        FROM unnest(s.children) WITH ORDINALITY AS c(id, n)),
       CASE
           WHEN s.id % 3 = 0 THEN jsonb_build_array(
                   jsonb_build_object('key', 'Website', 'value', 'https://example.com/mods/' || s.id),
                   jsonb_build_object('key', 'Version', 'value', '1.' || s.id % 10))
           ELSE '[]'::jsonb
           END,
       s.id % 31 = 0,
       s.id % 37 = 0,
       CASE WHEN s.id % 31 = 0 THEN '{1,5}'::smallint[] ELSE '{}'::smallint[] END,
       s.updated_at
FROM seed_item s
         JOIN seed_game g ON g.id = s.game_id
WHERE s.source = 'STEAM';

INSERT INTO steam_workshop_vote_data (local_id, score, votes_up, votes_down)
SELECT id, round((up::numeric / greatest(up + down, 1)), 3)::real, up, down
FROM (SELECT id, floor(random() * 4000)::int AS up, floor(random() * 600)::int AS down
      FROM seed_item
      WHERE source = 'STEAM'
        AND id % 13 <> 0) AS v;

INSERT INTO steam_workshop_tags (tag_id, tag_key, display_name, consumer_app_id)
SELECT row_number() OVER (ORDER BY g.id, t.n), t.tag, t.tag, g.app_id::bigint
FROM seed_game g
         CROSS JOIN LATERAL unnest(g.tags) WITH ORDINALITY AS t(tag, n);

INSERT INTO steam_workshop_item_tags (local_id, tag_id)
SELECT DISTINCT s.id, tg.tag_id
FROM seed_item s
         JOIN seed_game g ON g.id = s.game_id
         CROSS JOIN LATERAL (VALUES (1 + s.n % cardinality(g.tags)), (1 + (s.n * 3 + 1) % cardinality(g.tags))) AS p(pos)
         JOIN steam_workshop_tags tg ON tg.consumer_app_id = g.app_id::bigint AND tg.tag_key = g.tags[p.pos]
WHERE s.source = 'STEAM';

-- 8. Libraries -----------------------------------------------------------------------------------------------

CREATE TEMP TABLE seed_own
(
    user_id     bigint NOT NULL,
    internal_id bigint NOT NULL,
    PRIMARY KEY (user_id, internal_id)
) ON COMMIT DROP;

INSERT INTO seed_own
SELECT u.user_id, s.id
FROM seed_item s
         CROSS JOIN (VALUES (1), (2), (3), (4), (5), (6), (7), (8)) AS u(user_id)
WHERE s.source = 'STEAM'
  AND s.lifecycle = 'ACTIVE'
  AND CASE u.user_id
          WHEN 1 THEN s.id % 7 <> 0
          WHEN 2 THEN s.id % 7 = 0 OR s.id % 3 = 0
          WHEN 3 THEN s.id % 4 = 1
          WHEN 4 THEN s.id % 6 = 2
          WHEN 5 THEN s.id <= 40 AND s.id % 4 = 0
          WHEN 6 THEN s.id BETWEEN 1 AND 5
          WHEN 7 THEN s.id BETWEEN 6 AND 10
          WHEN 8 THEN s.id % 9 = 4
      END;

INSERT INTO seed_own
VALUES (1, 133), (1, 134), (1, 135), (1, 139), (1, 140),
       (2, 136), (2, 137), (2, 141),
       (3, 138), (3, 143),
       (4, 142), (4, 144);

INSERT INTO item_metadata_personal (id, user_id, internal_metadata_id, title, description, user_commentary,
                                    time_registered, dependencies_need_review, auto_backup)
SELECT row_number() OVER (ORDER BY o.user_id, o.internal_id),
       o.user_id,
       o.internal_id,
       CASE
           WHEN s.source = 'LOCAL' THEN s.title
           WHEN o.user_id IN (1, 2) AND s.id % 11 = 0 THEN s.title || ' (my patched copy)'
           END,
       CASE
           WHEN s.source = 'LOCAL' THEN s.description
           WHEN o.user_id IN (1, 2) AND s.id % 17 = 0 THEN E'My own notes replace the Steam text here.\n[b]Keep[/b] it disabled on the server.'
           END,
       CASE
           WHEN s.id % 6 = 0 THEN (ARRAY ['Works fine with the latest update.', 'Crashes with the lighting mod, load it last.',
               'Ask the server admin before enabling.', 'Great on small maps, too heavy for big ones.'])[1 + (s.id + o.user_id) % 4]
           END,
       greatest(s.created_at, now() - ((5 + (s.id * 3 + o.user_id * 7) % 200) || ' days')::interval),
       CASE WHEN cardinality(s.children) > 0 AND s.id % 13 IN (0, 3) THEN true END,
       s.source = 'STEAM' AND o.user_id IN (1, 2) AND s.id % 4 = 1 AND s.available
FROM seed_own o
         JOIN seed_item s ON s.id = o.internal_id;

-- Ratings 1-10
INSERT INTO user_ratings (user_id, item_personal_metadata_id, score)
SELECT p.user_id, p.id, 1 + (p.internal_metadata_id * p.user_id) % 10
FROM item_metadata_personal p
WHERE (p.internal_metadata_id + p.user_id) % 3 = 0;

-- Tags and groups per user and game
INSERT INTO user_tags (id, display_name, game_id, user_id)
SELECT row_number() OVER (ORDER BY u.user_id, g.id, t.n), t.name, g.id, u.user_id
FROM (VALUES (1), (2), (3), (4)) AS u(user_id)
         CROSS JOIN (SELECT id FROM seed_game WHERE id BETWEEN 1 AND 5) AS g
         CROSS JOIN unnest('{Favorite,Essential,Broken,"To test",Graphics,"Quality of life"}'::text[]) WITH ORDINALITY AS t(name, n)
WHERE u.user_id <> 4 OR t.n <= 3;

INSERT INTO user_item_tags (tag_id, item_personal_metadata_id)
SELECT t.id, p.id
FROM item_metadata_personal p
         JOIN item_metadata_internal i ON i.id = p.internal_metadata_id
         JOIN user_tags t ON t.user_id = p.user_id AND t.game_id = i.game_id
WHERE (p.internal_metadata_id + t.id) % 5 = 0;

INSERT INTO user_groups (id, user_id, game_id, name)
SELECT row_number() OVER (ORDER BY u.user_id, g.id, n.n), u.user_id, g.id, n.name
FROM (VALUES (1), (2), (3)) AS u(user_id)
         CROSS JOIN (SELECT id FROM seed_game WHERE id BETWEEN 1 AND 4) AS g
         CROSS JOIN unnest('{"Main modlist",Testing,"Multiplayer server",Archive}'::text[]) WITH ORDINALITY AS n(name, n);

INSERT INTO user_group_items (user_group_id, item_personal_metadata_id)
SELECT gr.id, p.id
FROM item_metadata_personal p
         JOIN item_metadata_internal i ON i.id = p.internal_metadata_id
         JOIN user_groups gr ON gr.user_id = p.user_id AND gr.game_id = i.game_id
WHERE CASE gr.name
          WHEN 'Main modlist' THEN p.internal_metadata_id % 4 IN (0, 1)
          WHEN 'Testing' THEN p.internal_metadata_id % 4 = 2
          WHEN 'Multiplayer server' THEN p.internal_metadata_id % 5 = 0
          ELSE i.source_status = 'UNAVAILABLE'
      END;

-- Relations inside one library
INSERT INTO user_item_relations (source_item_personal_metadata_id, target_item_personal_metadata_id, relation)
SELECT p.id, dp.id, 'DEPENDENCY'::relation_type
FROM item_metadata_personal p
         JOIN seed_item s ON s.id = p.internal_metadata_id
         JOIN seed_item d ON d.original_id = ANY (s.children)
         JOIN item_metadata_personal dp ON dp.user_id = p.user_id AND dp.internal_metadata_id = d.id
UNION ALL
SELECT p.id, c.id, 'PARENT'
FROM item_metadata_personal p
         JOIN item_metadata_personal c ON c.user_id = p.user_id
WHERE p.user_id IN (1, 2)
  AND (p.internal_metadata_id, c.internal_metadata_id) IN ((2, 12), (2, 22), (42, 52), (72, 82))
UNION ALL
SELECT p.id, a.id, 'ALTERNATE'
FROM item_metadata_personal p
         JOIN item_metadata_personal a ON a.user_id = p.user_id
WHERE (p.internal_metadata_id, a.internal_metadata_id) IN ((5, 15), (44, 54), (97, 101), (118, 122));

-- 9. Media ---------------------------------------------------------------------------------------------------
-- Image files exist only when run.sh generates and uploads them. YouTube embeds (Blender open movies) are
-- always added

CREATE TEMP TABLE seed_media
(
    id          bigint PRIMARY KEY,
    type        text   NOT NULL,
    owner       bigint,
    entity      text   NOT NULL,
    entity_id   bigint NOT NULL,
    purpose     text   NOT NULL,
    sort_order  int    NOT NULL,
    source_key  text,
    file_name   text,
    kind        text,
    label       text,
    embed_id    text
) ON COMMIT DROP;

INSERT INTO seed_media (id, type, owner, entity, entity_id, purpose, sort_order, source_key, file_name, kind, label,
                        embed_id)
SELECT row_number() OVER (ORDER BY m.ord, m.entity_id, m.sort_order),
       m.type, m.owner, m.entity, m.entity_id, m.purpose, m.sort_order, m.source_key, m.file_name, m.kind, m.label,
       m.embed_id
FROM (
         -- Game covers
         SELECT 1 AS ord, 'FILE' AS type, NULL::bigint AS owner, 'GAME' AS entity, g.id AS entity_id,
                'cover' AS purpose, 0 AS sort_order, 'cover' AS source_key, 'cover.jpg' AS file_name,
                'cover' AS kind, g.name AS label, NULL AS embed_id
         FROM seed_game g
         WHERE g.app_id IS NOT NULL
         UNION ALL
         -- Previews of 9 in 10 Steam items
         SELECT 2, 'FILE', NULL, 'ITEM', s.id, 'preview', 0, 'preview',
                CASE WHEN s.id % 2 = 0 THEN 'preview.png' ELSE 'preview.jpg' END, 'preview', s.title, NULL
         FROM seed_item s
         WHERE s.source = 'STEAM'
           AND s.id % 10 <> 9
         UNION ALL
         -- Screenshots of the showcase items
         SELECT 3, 'FILE', NULL, 'ITEM', s.id, 'showcase', k, 'previews/' || (s.id * 10 + k),
                'screenshot-' || k || '.jpg', 'shot', 'Screenshot ' || k || ': ' || s.title, NULL
         FROM seed_item s
                  CROSS JOIN generate_series(1, 3) AS k
         WHERE s.id IN (1, 2, 3, 41, 71, 96, 116)
         UNION ALL
         -- Own uploads
         SELECT 4, 'FILE', 2, 'ITEM', 2, 'preview', 0, NULL, 'my-preview.png', 'upload', 'Pikachu''s preview', NULL
         UNION ALL
         SELECT 4, 'FILE', 3, 'ITEM', 1, 'showcase', 10, NULL, 'fan-art.png', 'upload', 'Fan art by Ditto', NULL
         UNION ALL
         SELECT 4, 'FILE', o.user_id, 'ITEM', s.id, 'preview', 0, NULL, 'preview.png', 'upload', s.title, NULL
         FROM seed_item s
                  JOIN seed_own o ON o.internal_id = s.id
         WHERE s.source = 'LOCAL'
           AND s.id % 3 <> 2
     ) AS m
         CROSS JOIN seed_settings st
WHERE st.with_files;

INSERT INTO seed_media (id, type, owner, entity, entity_id, purpose, sort_order, source_key, embed_id)
SELECT coalesce((SELECT max(id) FROM seed_media), 0) + row_number() OVER (ORDER BY e.entity_id, e.sort_order), e.*
FROM (VALUES ('EMBED', NULL::bigint, 'ITEM', 1, 'showcase', 4, 'previews/youtube-1', 'aqz-KE-bpKQ'),
             ('EMBED', NULL, 'ITEM', 2, 'showcase', 4, 'previews/youtube-2', 'eRsGyueVLvQ'),
             ('EMBED', NULL, 'ITEM', 41, 'showcase', 1, 'previews/youtube-41', 'R6MlUcmOul8'),
             ('EMBED', NULL, 'ITEM', 71, 'showcase', 1, 'previews/youtube-71', 'WhWc3b3KhnY'),
             ('EMBED', 3, 'ITEM', 5, 'showcase', 1, NULL, 'mN0zPOpADL4')) AS e(type, owner, entity, entity_id,
                                                                             purpose, sort_order, source_key,
                                                                             embed_id);

INSERT INTO media_items (id, type, time_created, owner_user_id)
SELECT id, type::media_type, now() - ((id % 90) || ' days')::interval, owner
FROM seed_media;

INSERT INTO media_files (media_item_id, source_version, file_size, file_name, s3_key)
SELECT id,
       CASE WHEN owner IS NULL THEN '1700000000' END,
       0, -- run.sh writes the real size after generating the file
       file_name,
       'media/' || id || '/' || file_name
FROM seed_media
WHERE type = 'FILE';

INSERT INTO media_embeds (media_item_id, external_id, provider)
SELECT id, embed_id, 'YOUTUBE'
FROM seed_media
WHERE type = 'EMBED';

INSERT INTO media_links (id, media_item_id, entity, entity_id, purpose, sort_order, owner_user_id, source_key)
SELECT id, id, entity::entity_type, entity_id, purpose, sort_order, owner, source_key
FROM seed_media;

-- The admin hides the second screenshot of item 1 and moves the third screenshot of item 3 to the front
INSERT INTO media_link_overrides (link_id, user_id, sort_order, hidden)
SELECT id, 1, CASE WHEN entity_id = 3 THEN -1 END, entity_id = 1
FROM seed_media
WHERE entity = 'ITEM'
  AND ((entity_id = 1 AND source_key = 'previews/12') OR (entity_id = 3 AND source_key = 'previews/33'));

INSERT INTO seed_uploads (s3_key, kind, label, variant)
SELECT 'media/' || id || '/' || file_name, kind, label, id::int
FROM seed_media
WHERE type = 'FILE';

-- 10. Content ------------------------------------------------------------------------------------------------
-- Backups (revisions with stored archives) only when run.sh uploads them; the job history without them

CREATE TEMP TABLE seed_revision
(
    content_id  bigint      NOT NULL,
    internal_id bigint      NOT NULL,
    revision    bigint      NOT NULL,
    uploaded_by bigint,
    source_ver  text,
    file_name   text        NOT NULL,
    created_at  timestamptz NOT NULL,
    s3_key      text        NOT NULL,
    label       text        NOT NULL
) ON COMMIT DROP;

INSERT INTO item_contents (id, internal_metadata_id, edition, attributes, max_revisions)
SELECT row_number() OVER (ORDER BY s.id),
       s.id,
       'latest',
       NULL,
       CASE WHEN s.id % 5 = 0 THEN 1 WHEN s.id % 7 = 0 THEN 5 ELSE 3 END
FROM seed_item s
         CROSS JOIN seed_settings st
WHERE st.with_files
  AND s.lifecycle = 'ACTIVE'
  AND ((s.source = 'STEAM' AND s.id % 4 = 1) OR s.id IN (133, 134, 139, 141));

-- Shared backups of Steam items
INSERT INTO seed_revision (content_id, internal_id, revision, uploaded_by, source_ver, file_name, created_at, s3_key,
                           label)
SELECT c.id,
       s.id,
       r.k,
       NULL,
       extract(EPOCH FROM s.updated_at - ((total - r.k) * 17 || ' days')::interval)::bigint::text,
       f.name,
       least(now(), s.updated_at - ((total - r.k) * 17 || ' days')::interval + interval '2 hours'),
       'content/' || c.id || '/' || gen_random_uuid() || '/' || f.name,
       s.title || ' (revision ' || r.k || ')'
FROM item_contents c
         JOIN seed_item s ON s.id = c.internal_metadata_id
         CROSS JOIN LATERAL (SELECT least(1 + s.id % 3, c.max_revisions)) AS t(total)
         CROSS JOIN LATERAL generate_series(1, t.total) AS r(k)
         CROSS JOIN LATERAL (SELECT trim(BOTH '-' FROM regexp_replace(lower(s.title), '[^a-z0-9]+', '-', 'g')) || '.zip') AS f(name)
WHERE s.source = 'STEAM';

-- Uploads
INSERT INTO seed_revision (content_id, internal_id, revision, uploaded_by, source_ver, file_name, created_at, s3_key,
                           label)
SELECT c.id,
       s.id,
       coalesce((SELECT max(r.revision) FROM seed_revision r WHERE r.content_id = c.id), 0) + u.k,
       o.user_id,
       NULL,
       u.name,
       now() - ((30 - u.k * 10) || ' days')::interval,
       'content/' || c.id || '/' || gen_random_uuid() || '/' || u.name,
       s.title || ' (upload ' || u.k || ')'
FROM item_contents c
         JOIN seed_item s ON s.id = c.internal_metadata_id
         JOIN seed_own o ON o.internal_id = s.id AND (s.source = 'LOCAL' OR (s.id = 5 AND o.user_id = 3))
         CROSS JOIN LATERAL (VALUES (1, CASE WHEN s.id = 5 THEN 'patched-build.zip' ELSE 'files-v1.zip' END),
                                    (2, 'files-v2.zip')) AS u(k, name)
WHERE s.source = 'LOCAL'
   OR u.k = 1;

INSERT INTO item_content_revisions (item_content_id, revision, s3_key, file_name, file_size, time_created, sha256,
                                    source_version, uploaded_by_user_id)
SELECT content_id, revision, s3_key, file_name, 0, created_at, NULL, source_ver, uploaded_by
FROM seed_revision;

INSERT INTO seed_uploads (s3_key, kind, label, variant)
SELECT s3_key, 'archive', label, (content_id * 7 + revision)::int
FROM seed_revision;

-- Job history
INSERT INTO content_jobs (id, internal_metadata_id, type, status, revision, requested_by_user_id, attempts,
                          next_attempt_at, last_error, time_created, time_started, time_finished, error_code, note)
SELECT row_number() OVER (ORDER BY j.time_created, j.internal_id), j.*
FROM (SELECT r.internal_id,
             'BACKUP'::content_job_type,
             'SUCCEEDED'::content_job_status,
             r.revision,
             CASE WHEN r.revision = 1 AND r.internal_id % 2 = 0 THEN 2 END::bigint,
             1::smallint,
             r.created_at - interval '5 minutes',
             NULL::text,
             r.created_at - interval '5 minutes' AS time_created,
             r.created_at - interval '4 minutes',
             r.created_at,
             NULL::text,
             'Stored new revision'
      FROM seed_revision r
      WHERE r.uploaded_by IS NULL
      UNION ALL
      SELECT s.id, 'BACKUP', 'FAILED', NULL, 1, 5, now() - interval '1 day',
             'steamcmd exited with code 5 (seed data)', now() - interval '2 days', now() - interval '1 day',
             now() - interval '1 day', 'DOWNLOAD_FAILED', NULL
      FROM seed_item s
      WHERE s.source = 'STEAM'
        AND s.lifecycle = 'ACTIVE'
        AND s.id % 12 = 3
      UNION ALL
      SELECT 10, 'BACKUP', 'FAILED', NULL, 2, 5, now() - interval '6 hours', 'Download took longer than 30 minutes',
             now() - interval '7 hours', now() - interval '6 hours', now() - interval '6 hours', 'DOWNLOAD_TIMEOUT', NULL
      UNION ALL
      SELECT 20, 'BACKUP', 'CANCELLED', NULL, 1, 0, now() - interval '3 days', NULL, now() - interval '3 days',
             NULL, now() - interval '3 days', NULL, 'Cancelled by the user'
      UNION ALL
      -- Waits a week, so the worker doesn't pick it up; "retry" on the maintenance page runs it (and fails: fake id).
      SELECT 30, 'BACKUP', 'PENDING', NULL, 2, 0, now() + interval '7 days', NULL, now() - interval '1 hour', NULL,
             NULL, NULL, NULL
      UNION ALL
      SELECT 13, 'DELETE_REVISION', 'SUCCEEDED', 1, 1, 1, now() - interval '10 days', NULL,
             now() - interval '10 days', now() - interval '10 days', now() - interval '10 days', NULL,
             'Deleted revision 1') AS j;

-- 11. Sequences ----------------------------------------------------------------------------------------------

DO $$ BEGIN PERFORM setval('users_id_seq', (SELECT max(id) FROM users)),
       setval('game_metadata_id_seq', (SELECT max(id) FROM game_metadata)),
       setval('author_metadata_id_seq', (SELECT max(id) FROM author_metadata)),
       setval('item_metadata_internal_id_seq', (SELECT max(id) FROM item_metadata_internal)),
       setval('item_metadata_personal_id_seq', (SELECT max(id) FROM item_metadata_personal)),
       setval('user_tags_id_seq', (SELECT max(id) FROM user_tags)),
       setval('user_groups_id_seq', (SELECT max(id) FROM user_groups)),
       setval('steam_workshop_items_local_id_seq', (SELECT max(local_id) FROM steam_workshop_items)),
       setval('steam_workshop_tags_tag_id_seq', (SELECT max(tag_id) FROM steam_workshop_tags)),
       setval('media_items_id_seq', coalesce((SELECT max(id) FROM media_items), 1), EXISTS (SELECT FROM media_items)),
       setval('media_links_id_seq', coalesce((SELECT max(id) FROM media_links), 1), EXISTS (SELECT FROM media_links)),
       setval('item_contents_id_seq', coalesce((SELECT max(id) FROM item_contents), 1), EXISTS (SELECT FROM item_contents)),
       setval('content_jobs_id_seq', coalesce((SELECT max(id) FROM content_jobs), 1), EXISTS (SELECT FROM content_jobs)),
       setval('content_uploads_id_seq', 1, false); END $$;

COMMIT;

-- Summary.
SELECT 'users' AS what, count(*) AS rows FROM users
UNION ALL SELECT 'games', count(*) FROM game_metadata
UNION ALL SELECT 'items', count(*) FROM item_metadata_internal
UNION ALL SELECT 'library entries', count(*) FROM item_metadata_personal
UNION ALL SELECT 'media', count(*) FROM media_items
UNION ALL SELECT 'backup revisions', count(*) FROM item_content_revisions
UNION ALL SELECT 'content jobs', count(*) FROM content_jobs;
