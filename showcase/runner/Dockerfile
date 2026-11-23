# Keep the Playwright version the same as in package.json
FROM mcr.microsoft.com/playwright:v1.55.0-noble

# psql 17 from the PostgreSQL repository: Ubuntu's 16 can't read pg_dump 17 output
RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates curl ffmpeg redis-tools \
 && install -d /usr/share/postgresql-common/pgdg \
 && curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc -o /usr/share/postgresql-common/pgdg/apt.postgresql.org.asc \
 && echo "deb [signed-by=/usr/share/postgresql-common/pgdg/apt.postgresql.org.asc] https://apt.postgresql.org/pub/repos/apt noble-pgdg main" \
    > /etc/apt/sources.list.d/pgdg.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends postgresql-client-17 \
 && rm -rf /var/lib/apt/lists/*

# The scripts are mounted next to these packages, at /opt/runner/scripts
WORKDIR /opt/runner
COPY package.json package-lock.json ./
RUN npm ci --omit=dev --no-audit --no-fund

# Chromium needs a writable home
ENV HOME=/tmp
USER 65534:65534
