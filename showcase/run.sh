#!/bin/sh
# Showcase: real Workshop data in a separate app instance, and the README GIF. See showcase/README.md
set -eu

# Long TODO: make each showcase as separate file of actions, because right now this is hardcoded
# Settings
PROJECT=workshop-manager-showcase
APP_PORT=8390
# Host ports of the showcase services; compose.override.yaml and compose.runner.yaml read them
export SHOWCASE_POSTGRES_PORT=5435 SHOWCASE_REDIS_PORT=6382 SHOWCASE_S3_PORT=8336
# How often the app checks for previews to fetch (the app's default is a minute)
MEDIA_SYNC_INTERVAL=15s
APP_START_TIMEOUT=180   # seconds
MEDIA_TIMEOUT=1800      # seconds; fetch waits this long for previews
GIF_NAME=add-items.gif  # written to docs/images
# Java 25 is looked up on PATH and in the usual places; set JAVA to a binary to choose one

ROOT=$(cd "$(dirname "$0")/.." && pwd)
SHOWCASE=$ROOT/showcase
CACHE=$SHOWCASE/.cache
APP_URL=http://localhost:$APP_PORT

compose() {
    docker compose -p "$PROJECT" -f "$ROOT/backend/compose.dev.yaml" \
        -f "$SHOWCASE/compose.override.yaml" -f "$SHOWCASE/compose.runner.yaml" "$@"
}

# Runs as you, so the files it writes are yours. Inside: /opt/runner/scripts, /showcase, /output (docs/images)
in_runner() {
    mkdir -p "$ROOT/docs/images"
    compose run --rm --no-deps --user "$(id -u):$(id -g)" runner "$@"
}

node_script() {
    name=$1
    shift
    in_runner node "/opt/runner/scripts/$name" "$APP_URL" "$@"
}

sql() {
    compose exec -T postgres psql -U dev -d workshop_manager -X -At "$@"
}

# Saves <dir>/<name>.sql and the time it was taken (restore.sh moves the dates from there).
# Sessions, nonces, uploads in progress and scheduler state stay out
snapshot() {
    compose exec -T postgres pg_dump -U dev -d workshop_manager --no-owner --no-privileges \
        --exclude-table-data='qrtz_*' --exclude-table-data=refresh_tokens \
        --exclude-table-data=open_id_nonces --exclude-table-data=content_uploads > "$1/$2.sql"
    sql -c "SELECT now()" > "$1/$2.taken-at"
}

# The schema gets dropped, so the app must not run. Its next start migrates the data
restore() {
    stop_app
    in_runner sh /opt/runner/scripts/restore.sh /showcase/.cache "$1"
}

find_java() {
    for candidate in "${JAVA:-}" "${JAVA_HOME:+$JAVA_HOME/bin/java}" java /usr/lib/jvm/*25*/bin/java \
        "$HOME"/.jdks/*25*/bin/java; do
        [ -n "$candidate" ] && command -v "$candidate" > /dev/null 2>&1 || continue
        version=$("$candidate" -XshowSettings:properties -version 2>&1 | sed -n 's/.*java.specification.version = //p')
        [ "${version:-0}" -ge 25 ] 2> /dev/null && { echo "$candidate"; return; }
    done
    echo "Java 25 not found: set JAVA or JAVA_HOME" >&2
    exit 1
}

app_running() {
    [ -f "$CACHE/app.pid" ] && kill -0 "$(cat "$CACHE/app.pid")" 2> /dev/null
}

start_services() {
    mkdir -p "$CACHE"
    compose up -d --wait postgres redis seaweedfs
}

# Dev profile, so backend/.env is read as usual
start_app() {
    app_running && return
    java=$(find_java)
    echo "Building the app jar"
    (cd "$ROOT/backend" && sh ./gradlew -q bootJar -x test)
    jar=$(ls "$ROOT"/backend/build/libs/*.jar | head -n 1)
    mkdir -p "$CACHE/tmp"
    echo "Starting the app on $APP_URL (log: showcase/.cache/app.log)"
    cd "$ROOT/backend"
    nohup "$java" --enable-native-access=ALL-UNNAMED -jar "$jar" \
        --spring.profiles.active=dev \
        --spring.application.name="$PROJECT" \
        --spring.datasource.url="jdbc:postgresql://localhost:$SHOWCASE_POSTGRES_PORT/workshop_manager" \
        --spring.data.redis.port="$SHOWCASE_REDIS_PORT" \
        --application.storage.s3.endpoint="http://localhost:$SHOWCASE_S3_PORT" \
        --server.port="$APP_PORT" \
        --management.server.port=-1 \
        --application.frontend.url="$APP_URL" \
        --application.content.temp-dir="$CACHE/tmp" \
        --application.media.sync-interval="$MEDIA_SYNC_INTERVAL" \
        --application.items.sync.enabled=false \
        --application.authors.refresh.enabled=false \
        > "$CACHE/app.log" 2>&1 &
    echo $! > "$CACHE/app.pid"
    cd - > /dev/null
    for _ in $(seq 1 $((APP_START_TIMEOUT / 2))); do
        curl -sf -o /dev/null "$APP_URL/" && return
        app_running || { echo "The app stopped, see showcase/.cache/app.log" >&2; exit 1; }
        sleep 2
    done
    echo "The app did not start in $APP_START_TIMEOUT s, see showcase/.cache/app.log" >&2
    exit 1
}

stop_app() {
    if app_running; then
        pid=$(cat "$CACHE/app.pid")
        kill "$pid"
        while kill -0 "$pid" 2> /dev/null; do sleep 1; done
    fi
    rm -f "$CACHE/app.pid"
}

wait_for_media() {
    echo "Waiting for the app to fetch previews and screenshots"
    for i in $(seq 1 $((MEDIA_TIMEOUT / 15))); do
        left=$(sql -c "SELECT count(*) FILTER (WHERE time_synced IS NULL) FROM media_source_syncs")
        [ "$left" = 0 ] && return
        [ $((i % 4)) -eq 0 ] && echo "  $left left"
        sleep 15
    done
    echo "Previews still missing after $MEDIA_TIMEOUT s, see showcase/.cache/app.log" >&2
    exit 1
}

cmd_fetch() {
    echo "== Fetch from Steam into an empty database"
    stop_app
    compose down -v --remove-orphans
    start_services
    start_app
    node_script import.mjs /showcase/ids.json
    wait_for_media
    new=$CACHE/new
    rm -rf "$new"
    mkdir -p "$new"
    snapshot "$new" fetched
    in_runner sh /opt/runner/scripts/export-media.sh /showcase/.cache/new
    # The old cache goes only once the new one is complete
    rm -rf "$CACHE/media" "$CACHE/media-keys.txt" "$CACHE"/fetched.* "$CACHE"/organized.*
    mv "$new/media" "$new/media-keys.txt" "$new/fetched.sql" "$new/fetched.taken-at" "$CACHE/"
    rmdir "$new"
}

cmd_organize() {
    echo "== Organize: tags, groups, ratings, notes, added dates"
    start_services
    restore fetched
    start_app
    node_script organize.mjs /showcase/organize.json
    sql -q -v ON_ERROR_STOP=1 < "$SHOWCASE/timeline.sql"
    snapshot "$CACHE" organized
}

cmd_capture() {
    echo "== Capture docs/images/$GIF_NAME"
    start_services
    restore organized
    start_app
    node_script capture.mjs "/output/$GIF_NAME"
}

cmd_browse() {
    start_services
    restore organized
    start_app
    echo "Showcase instance: $APP_URL (admin / Admin123!dev). Stop it with: showcase/run.sh down"
}

cmd_down() {
    stop_app
    compose down -v --remove-orphans
}

case "${1:-all}" in
    all)
        [ -f "$CACHE/fetched.sql" ] || cmd_fetch
        cmd_organize
        cmd_capture
        cmd_down
        echo "Done: docs/images/$GIF_NAME"
        ;;
    fetch) cmd_fetch ;;
    organize) cmd_organize ;;
    capture) cmd_capture ;;
    browse) cmd_browse ;;
    down) cmd_down ;;
    *)
        echo "Usage: showcase/run.sh [all | fetch | organize | capture | browse | down]" >&2
        exit 1
        ;;
esac
