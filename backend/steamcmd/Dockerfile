# SteamCMD for content backups of Steam items, driven by the backend over HTTP

FROM eclipse-temurin:25.0.5.1_2-jdk-noble AS build
COPY src/main/java/ /src/
RUN javac -d /out /src/notkirill/workshopmanager/steamcmd/*.java \
 && jlink --add-modules java.base,jdk.httpserver --strip-debug --no-header-files --no-man-pages --output /jre

FROM debian:trixie-slim
ARG UID=1000
ARG GID=1000
RUN apt-get update \
 && apt-get install -y --no-install-recommends lib32gcc-s1 ca-certificates curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --gid "$GID" steam \
 && useradd --uid "$UID" --gid "$GID" --create-home steam \
 && install -d -o steam -g steam /opt/steamcmd /downloads
COPY --from=build /jre /opt/jre
COPY --from=build /out /opt/wrapper
ENV HOME=/home/steam
EXPOSE 8095
COPY --chmod=755 entrypoint.sh /opt/entrypoint.sh
ENTRYPOINT ["/opt/entrypoint.sh"]
