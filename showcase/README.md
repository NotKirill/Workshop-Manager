# Showcase

Real Steam Workshop data in a separate instance of the app, and the README GIF (`docs/images/add-items.gif`) recorded
from it. For local use in dev mode.

The pipeline reuses `backend/compose.dev.yaml` under its own project name (`workshop-manager-showcase`), so it gets its
own volumes and leaves the dev data alone. `compose.override.yaml` only moves the host ports, so both stacks can run
at the same time. Monitoring and SteamCMD are not started. The app runs from the executable jar with the dev profile
and other connection settings, on <http://localhost:8390>.

Everything else runs in one container, `runner` (`runner/Dockerfile`, added by `compose.runner.yaml`), so the
pipeline behaves the same on every machine. It is Playwright's image with Node, Chromium and fonts, plus ffmpeg, psql,
curl and redis-cli: it registers items, organizes the library, records the GIF, and loads snapshots and images. It
uses the host network and reaches the app and the services on their host ports.

The runner is a dev tool and has nothing to do with production. It is built on the first run and is large (about
2 GB, most of it Chromium).

## Requirements

Docker with Compose 2.24 or newer, and Java 25 for the app. A Steam Web API key in `backend/.env`
(`STEAM_WEB_API_KEY`), only for fetching: without it Steam returns no dependencies.

Ports, timeouts and the GIF's item, size and tempo are set at the top of `run.sh` and `scripts/capture.mjs`.

## Commands

```bash
showcase/run.sh
```

Does everything: fetches from Steam if there is no cache yet, organizes the library, records the GIF, then stops.

| Command    | What it does                                                                                      |
|------------|---------------------------------------------------------------------------------------------------|
| `fetch`    | Registers the items from `ids.json` in an empty database and saves the result to the cache        |
| `organize` | Loads the fetched data, adds tags, groups, ratings and notes, spreads the "added" dates, saves it |
| `capture`  | Loads the organized data and records the GIF (`scripts/capture.mjs`)                              |
| `browse`   | Starts the showcase instance with the organized data and leaves it running                        |
| `down`     | Stops the app and removes the showcase containers and volumes (the cache stays)                   |

## Cache

`showcase/.cache/` (ignored by git) keeps everything between runs: the snapshots `fetched.sql` and `organized.sql`,
every preview and screenshot, and the app log. Nothing is deleted automatically. Only `fetch` calls Steam: about 40 API
calls, then the app's own paced image downloads, around 5 minutes in total. A new `fetch` replaces the cache only once
it has finished.

A snapshot keeps the schema, the data and the Liquibase history, like the dev seed's. It is loaded while the app is
stopped, and the app's next start applies the migrations newer than the snapshot, so the cache outlives schema changes
and the showcase stays the same even when items change on Steam. Loading also moves the app's own dates to the time it
runs ("added 3 days ago" stays 3 days ago), but not the dates that come from Steam: `sql/shift-dates.sql` lists those.

## Changing the data

- `ids.json`: which items to register. `games` are registered first, in file order. `lead` items are registered last,
  one by one, and listed newest first. The library's default order ("Recently added") follows registration order, so
  the first lead item opens the library. Dependencies of lead items must already be in `games`.
- `organize.json`: tags, groups, ratings, notes and title overrides per item. Kept uneven on purpose, like a real
  library: some items have no tags, a few have several, libraries are not rated.
- `timeline.sql`: spreads the "added" dates over the past months, one day per game, in registration order.
