# Dev seed

Generic data for testing the dev stack by hand: users with different permissions, games, ~150 items with Steam
details, libraries with tags, groups, ratings and relations, images, YouTube embeds, downloadable backups and a
content job history.

## Run it

Stop the backend (and close database consoles connected to the dev database), then from `backend/`:

```bash
docker compose -f compose.dev.yaml --profile seed run --rm seed
```

Then start the backend. It takes about 15 seconds. Only Docker is needed: the `seed` container brings psql, curl,
zip, ImageMagick and redis-cli, and talks to the dev services by their compose names. The image is built on the first
run; the folder is mounted, so editing the files needs no rebuild.

What it does:

1. replaces the whole dev schema with `snapshot.sql` (schema, data and Liquibase history);
2. moves every date to the time it runs, so "added 3 days ago" stays 3 days ago;
3. generates the images and backup archives listed in `files.tsv` and uploads them to the dev bucket;
4. clears Redis (cached users and one-time tokens of the old rows).

When the backend starts, Liquibase applies every migration newer than the snapshot. A new migration needs no change
here: the real migrations bring the seed data up to date. If one fails on the seed data, that is a bug in the
migration.

## Changing the data

Edit the data in the app (or with SQL), then save it as the new snapshot. From `backend/`:

```bash
docker compose -f compose.dev.yaml --profile seed run --rm --user "$(id -u):$(id -g)" seed snapshot
```

`--user` lets the container write `snapshot.sql` and `files.tsv` into this folder. The snapshot:

- keeps dates relative (they count from 2000-01-01), and rows in key order, so the diff shows only what changed;
- leaves out sign-in sessions, nonces, unfinished uploads and the scheduler state;
- leaves out your own Steam ID (`BOOTSTRAP_ADMIN_STEAM_ID`), which every seed run adds again.

Stored files are not part of the snapshot: `files.tsv` lists them, and every seed run generates placeholders. An image
uploaded in the app comes back as a generated placeholder of the same name.

Take a snapshot soon after the seed and your changes: background jobs (Steam sync, cleanup) change the data the
longer the backend runs.

## Accounts

| Sign-in                            | Password       | What it shows                                                            |
|------------------------------------|----------------|--------------------------------------------------------------------------|
| `admin`                            | `Admin123!dev` | Mewtwo: every permission, the biggest library                            |
| `pikachu` / `pikachu@example.test` | `User123!dev`  | Pikachu: content manager; own preview replaces the Steam one on one item |
| `ditto`                            | `User123!dev`  | Ditto: uploads media and content; own fan art, embed, upload             |
| `eevee` / `eevee@example.test`     | `User123!dev`  | Eevee: can register items, no uploads                                    |
| `psyduck`                          | `User123!dev`  | Psyduck: read-only                                                       |
| `snorlax`                          | `User123!dev`  | Snorlax: disabled account                                                |
| `magikarp`                         | `User123!dev`  | Magikarp: locked account                                                 |
| `rotom@example.test`               | `User123!dev`  | Rotom: users and maintenance only, no items                              |
| Steam `76561190000000008`          | -              | Charizard: Steam-only (fake id, no real sign-in)                         |

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
- **Old files stay in the bucket:** a re-run uploads the objects again; objects of removed rows are no longer
  referenced.
