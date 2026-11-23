#!/usr/bin/env bash
# Runs on the server (from deploy.yml): switches both images to a tag and starts them;
# goes back to the previous ones if the app doesn't get healthy.
# By hand: docker login ghcr.io, then bash deploy.sh ghcr.io/notkirill/workshop-manager <tag>
set -euo pipefail

IMAGE="${1:?Usage: deploy.sh <image without tag> <tag>}"
TAG="${2:?Usage: deploy.sh <image without tag> <tag>}"
WAIT_SECONDS=300

cd "$(dirname "$0")"

env_value() { sed -n "s|^$1=||p" .env; }

# Replaces the variable in .env, or adds it
set_env() {
    if grep -q "^$1=" .env; then
        sed -i "s|^$1=.*|$1=$2|" .env
    else
        echo "$1=$2" >> .env
    fi
}

start() {
    docker compose up -d --remove-orphans
    docker compose up -d --wait --wait-timeout "$WAIT_SECONDS" app
}

PREVIOUS_APP=$(env_value APP_IMAGE)
PREVIOUS_STEAMCMD=$(env_value STEAMCMD_IMAGE)

echo "Deploying $IMAGE:$TAG (now: ${PREVIOUS_APP:-nothing})"
# Pull first, so a wrong tag leaves .env as is
docker pull --quiet "$IMAGE:$TAG"
docker pull --quiet "$IMAGE-steamcmd:$TAG"
set_env APP_IMAGE "$IMAGE:$TAG"
set_env STEAMCMD_IMAGE "$IMAGE-steamcmd:$TAG"

if ! start; then
    echo "The application did not get healthy. Its last log lines:" >&2
    docker compose logs --tail 100 app >&2
    if [[ -n $PREVIOUS_APP ]]; then
        echo "Going back to $PREVIOUS_APP" >&2
        set_env APP_IMAGE "$PREVIOUS_APP"
        set_env STEAMCMD_IMAGE "$PREVIOUS_STEAMCMD"
        start
    fi
    exit 1
fi

# Old releases
docker image prune -af --filter until=168h > /dev/null

# First deploy: replace the self-signed placeholder
if ! openssl x509 -in "$(env_value TLS_CERT_DIR)/fullchain.pem" -noout -issuer | grep -q "Let's Encrypt"; then
    sudo workshop-cert
fi

echo "Deployed $IMAGE:$TAG"
