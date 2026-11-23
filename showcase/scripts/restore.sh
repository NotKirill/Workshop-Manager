#!/bin/sh
# Loads a cached snapshot and its images. Run by run.sh in the runner, while the app is stopped
# Usage: restore.sh <cache dir> <fetched | organized>
set -eu

CACHE=$1
SNAPSHOT=$CACHE/$2.sql
[ -f "$SNAPSHOT" ] || { echo "No $SNAPSHOT: run the fetch first" >&2; exit 1; }

s3() {
    curl -sS --aws-sigv4 "aws:amz:${S3_REGION}:s3" --user "${S3_ACCESS_KEY}:${S3_SECRET_KEY}" "$@"
}

# The schema gets dropped, so nothing else may be connected
others=$(psql -X -At -c "SELECT coalesce(nullif(application_name, ''), 'unnamed client') FROM pg_stat_activity
    WHERE datname = current_database() AND backend_type = 'client backend' AND pid <> pg_backend_pid()" | sort | uniq -c)
if [ -n "$others" ]; then
    echo "Other connections to ${PGDATABASE}: stop the app and close database consoles, then try again" >&2
    echo "$others" >&2
    exit 1
fi

echo "Loading $2.sql"
psql -X -q -o /dev/null -v ON_ERROR_STOP=1 --single-transaction \
    -v snapshot="$SNAPSHOT" -v taken_at="$(cat "$CACHE/$2.taken-at")" -f /showcase/sql/restore.sql

code=$(s3 -o /dev/null -w '%{http_code}' -X PUT "${S3_ENDPOINT}/${S3_BUCKET}")
case $code in
    200 | 409) ;;
    *) echo "Cannot create bucket ${S3_BUCKET} (HTTP $code)" >&2; exit 1 ;;
esac

echo "Uploading $(wc -l < "$CACHE/media-keys.txt") media files"
while read -r key; do
    case $key in
        *.png) type=image/png ;;
        *.gif) type=image/gif ;;
        *.webp) type=image/webp ;;
        *) type=image/jpeg ;;
    esac
    s3 --fail-with-body -o /dev/null -H "Content-Type: $type" -T "$CACHE/media/$key" "${S3_ENDPOINT}/${S3_BUCKET}/$key"
done < "$CACHE/media-keys.txt"

# Cached users and tokens belong to the old rows
redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" -a "$REDIS_PASSWORD" --no-auth-warning FLUSHDB > /dev/null
