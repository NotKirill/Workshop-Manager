# The backend jar with the frontend inside. Built from the repository root, because the frontend
# lives next to the backend:
#   docker build -f backend/Dockerfile -t workshop-manager .

FROM eclipse-temurin:25.0.5.1_2-jdk-noble AS build
WORKDIR /src
COPY backend/ backend/
COPY frontend/ frontend/
WORKDIR /src/backend
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/src/backend/.gradle \
    --mount=type=cache,target=/root/.npm \
    sh ./gradlew --no-daemon bootJar \
 && cp build/libs/workshop-manager-backend-*.jar /app.jar

FROM eclipse-temurin:25.0.5.1_2-jre-noble
# uid and gid 10001: compose.yaml builds the steamcmd image with the same ids
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --gid 10001 workshop \
 && useradd --uid 10001 --gid workshop --no-create-home --shell /usr/sbin/nologin workshop \
 && install -d -o workshop -g workshop /var/lib/workshop-manager \
    /var/lib/workshop-manager/content-tmp /var/lib/workshop-manager/steam-downloads
COPY --from=build /app.jar /opt/workshop-manager/app.jar
USER workshop
WORKDIR /var/lib/workshop-manager
# Application, and metrics with health (not proxied by nginx)
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "/opt/workshop-manager/app.jar"]
