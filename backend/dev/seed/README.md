# Dev seed

Generic data for testing the dev stack by hand: users with different permissions, games, ~150 items with Steam
details, libraries with tags, groups, ratings and relations, images, YouTube embeds, downloadable backups and a
content job history.

## Run it

The dev stack must be up, and the backend must have started once on the current code (Liquibase creates or updates
the schema). From `backend/`:

```bash
docker compose -f compose.dev.yaml --profile seed run --rm seed
```

It takes about 15 seconds. Only Docker is needed: the `seed` container brings psql, curl, zip, ImageMagick and
redis-cli, and talks to the dev services by their compose names. The image is built on the first run; `seed.sql` and
`run.sh` are mounted, so editing them needs no rebuild.

What it does:

1. checks that the database is at the migration the seed was written for, and stops if not;
2. empties every application table (Liquibase and Quartz tables stay) and inserts the data, in one transaction;
3. generates the images and backup archives the data refers to and uploads them to the dev bucket;
4. writes the real file sizes and SHA-256 hashes;
5. clears Redis (cached users and one-time tokens of the old rows).

Running it again always gives the same state. The backend can keep running; signed-in browsers should sign in again.

`seed.sql` also runs on its own (IntelliJ database console, psql): same data, but without stored files (no images,
no backups).

## Accounts

| Sign-in                     | Password       | What it shows                                                            |
|-----------------------------|----------------|--------------------------------------------------------------------------|
| `admin`                     | `Admin123!dev` | Mewtwo: every permission, the biggest library                                  |
| `pikachu` / `pikachu@example.test` | `User123!dev`  | Pikachu: content manager; own preview replaces the Steam one on one item      |
| `ditto`                     | `User123!dev`  | Ditto: uploads media and content; own fan art, embed, upload             |
| `eevee` / `eevee@example.test` | `User123!dev`  | Eevee: can register items, no uploads                                         |
| `psyduck`                    | `User123!dev`  | Psyduck: read-only                                                              |
| `snorlax`                      | `User123!dev`  | Snorlax: disabled account                                                       |
| `magikarp`                       | `User123!dev`  | Magikarp: locked account                                                         |
| `rotom@example.test`          | `User123!dev`  | Rotom: users and maintenance only, no items                                |
| Steam `76561190000000008`   | -              | Charizard: Steam-only (fake id, no real sign-in)                      |

If `BOOTSTRAP_ADMIN_STEAM_ID` is set in `backend/.env`, `admin` also gets that Steam sign-in.

## What's inside

- **Games:** six made-up games with fake Steam app ids, one local-only game, a compatibility pair (Classic ↔
  current).
- **Items:** 138 Steam items with BBCode descriptions, tags, votes, links and dependencies (a core item per game,
  one dependency nobody registered), 12 local items, a few unavailable at the source, 3 orphaned (removed by the
  cleanup job about 10-20 minutes after seeding) and 3 failed cleanups on the maintenance page.
- **Libraries:** per-user titles and notes, ratings, tags, groups, auto-backup flags, dependency reviews, relations
  (dependency, parent, alternate).
- **Media:** game covers, item previews, screenshots, YouTube embeds (Blender open movies), own uploads, a hidden and
  a reordered source image (admin, items 1 and 3).
- **Content:** 1-3 shared backups per item with different "keep last" settings, uploads on local items, a job
  history with succeeded, failed, cancelled and waiting jobs.

## Good to know

- **Fake Steam ids:** once an item hasn't synced for 24 hours, the sync job asks Steam about it and marks it
  unavailable. Run the seed again to reset.
- **The waiting job** is due in a week; "retry" on the maintenance page runs it, and it fails (fake id).
- **Old files stay in the bucket:** a re-run uploads new objects; the previous run's are no longer referenced.
- **New migration?** The seed stops with "the database has migrations newer than the seed". Update `seed.sql` for
  the new schema and the changeset name in its version check.
