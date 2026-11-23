#!/bin/sh
# Downloads every stored image into <target dir>/media, at its storage key. Run by run.sh in the runner
# Usage: export-media.sh <target dir>
set -eu

TARGET=$1
rm -rf "$TARGET/media"
mkdir -p "$TARGET/media"
psql -X -At -c "SELECT s3_key FROM media_files ORDER BY s3_key" > "$TARGET/media-keys.txt"
echo "Downloading $(wc -l < "$TARGET/media-keys.txt") media files"
while read -r key; do
    mkdir -p "$TARGET/media/$(dirname "$key")"
    curl -sS --fail --aws-sigv4 "aws:amz:${S3_REGION}:s3" --user "${S3_ACCESS_KEY}:${S3_SECRET_KEY}" \
        -o "$TARGET/media/$key" "${S3_ENDPOINT}/${S3_BUCKET}/$key"
done < "$TARGET/media-keys.txt"
