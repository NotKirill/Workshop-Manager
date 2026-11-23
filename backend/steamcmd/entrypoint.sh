#!/bin/sh
# Downloads SteamCMD on the first start (the image doesn't include it), then runs the wrapper as the steam user
set -e

# Volumes may belong to root
chown steam:steam /downloads /opt/steamcmd

# Not -x: root here can't run files of other users
if [ ! -f /opt/steamcmd/steamcmd.sh ]; then
    echo "Downloading SteamCMD from Valve"
    setpriv --reuid=steam --regid=steam --init-groups sh -c \
        'curl -fsSL https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz | tar -xz -C /opt/steamcmd'
fi

exec setpriv --reuid=steam --regid=steam --init-groups \
    /opt/jre/bin/java -cp /opt/wrapper notkirill.workshopmanager.steamcmd.SteamCmdWrapper
