#!/usr/bin/env bash
# Prepares a fresh Debian 13 server: firewall, SSH keys only, Docker, the deploy user, .env and certificates.
# Safe to run again. Runs from provision.yml, or by hand from backend/:
#   scp deploy/prepare-server.sh root@SERVER:
#   ssh root@SERVER 'DOMAIN=workshop.example.com bash prepare-server.sh'
# Root and password sign-in are turned off at the end.
set -euo pipefail

DOMAIN="${DOMAIN:?Set DOMAIN, e.g. DOMAIN=workshop.example.com}"
LETSENCRYPT_EMAIL="${LETSENCRYPT_EMAIL:-}"
# One per line; empty: root's keys
DEPLOY_PUBLIC_KEYS="${DEPLOY_PUBLIC_KEYS:-}"
BOOTSTRAP_ADMIN_STEAM_ID="${BOOTSTRAP_ADMIN_STEAM_ID:-}"
STEAM_WEB_API_KEY="${STEAM_WEB_API_KEY:-}"
DEPLOY_USER="${DEPLOY_USER:-deploy}"
SWAP_SIZE="${SWAP_SIZE:-2G}"

APP_DIR=/opt/workshop-manager
ENV_FILE="$APP_DIR/.env"
ACME_DIR=/var/lib/workshop-manager/acme
CERT_DIR=/etc/workshop-manager/certs

step() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }
fail() { printf '\033[1;31m%s\033[0m\n' "$*" >&2; exit 1; }

# Writes stdin to a file; returns 1 if nothing changed
write_file() {
    local target=$1 temp
    temp=$(mktemp)
    cat > "$temp"
    if cmp -s "$temp" "$target"; then
        rm "$temp"
        return 1
    fi
    mv "$temp" "$target"
    chmod 644 "$target"
}

[[ $EUID -eq 0 ]] || fail "Run as root"
# shellcheck source=/dev/null
. /etc/os-release
[[ $VERSION_CODENAME == trixie ]] || fail "Expected Debian 13 (trixie), found $PRETTY_NAME"
[[ -n $DEPLOY_PUBLIC_KEYS || -s /root/.ssh/authorized_keys ]] \
    || fail "No SSH key: set DEPLOY_PUBLIC_KEYS, or run ssh-copy-id root@SERVER first"

step "System packages"
export DEBIAN_FRONTEND=noninteractive
# Some VPS images point GRUB to a disk that doesn't exist
if dpkg -s grub-pc &> /dev/null; then
    GRUB_DISKS=$(debconf-show grub-pc | sed -n 's/^[* ]*grub-pc\/install_devices: //p' | tr ',' ' ')
    GRUB_OK=$([[ -n $GRUB_DISKS ]] && echo true || echo false)
    for disk in $GRUB_DISKS; do
        [[ -e $disk ]] || GRUB_OK=false
    done
    if [[ $GRUB_OK == false ]]; then
        # The disk the root filesystem is on (-s walks from the partition up to its disk)
        DISKS=$(lsblk -snro NAME,TYPE "$(findmnt -nvo SOURCE /)" | awk '$2 == "disk" {print "/dev/" $1}')
        [[ -n $DISKS && $(wc -l <<< "$DISKS") -eq 1 ]] \
            || fail "GRUB's disk is not set and the system disk is unclear: run dpkg-reconfigure grub-pc"
        echo "GRUB disk: $DISKS"
        echo "grub-pc grub-pc/install_devices multiselect $DISKS" | debconf-set-selections
    fi
fi
# Finishes packages that a failed run left half-installed
dpkg --configure -a
apt-get update
apt-get -y -o Dpkg::Options::=--force-confdef -o Dpkg::Options::=--force-confold upgrade
apt-get -y install sudo curl ca-certificates openssl rsync ufw fail2ban python3-systemd unattended-upgrades certbot
timedatectl set-timezone UTC

step "Automatic security updates"
write_file /etc/apt/apt.conf.d/20auto-upgrades <<'EOF' || true
APT::Periodic::Update-Package-Lists "1";
APT::Periodic::Unattended-Upgrade "1";
EOF

step "Swap"
if [[ -z $(swapon --show --noheadings) ]]; then
    fallocate -l "$SWAP_SIZE" /swapfile
    chmod 600 /swapfile
    mkswap /swapfile
    swapon /swapfile
fi
if [[ -e /swapfile ]] && ! grep -q '^/swapfile ' /etc/fstab; then
    echo '/swapfile none swap sw 0 0' >> /etc/fstab
fi
echo 'vm.swappiness=10' | write_file /etc/sysctl.d/99-swappiness.conf && sysctl -q --system
swapon --show

step "Firewall: SSH, HTTP and HTTPS only"
SSH_PORT=$(sshd -T | awk '$1 == "port" && !port {port = $2} END {print port}')
ufw default deny incoming
ufw default allow outgoing
ufw allow "$SSH_PORT/tcp"
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable
ufw status verbose

step "fail2ban for SSH"
# Debian 13 logs to journald, not /var/log/auth.log
write_file /etc/fail2ban/jail.d/sshd.local <<'EOF' || true
[sshd]
enabled = true
backend = systemd
maxretry = 5
findtime = 10m
bantime = 1h
EOF
systemctl enable fail2ban
systemctl restart fail2ban

step "Docker"
if ! command -v docker > /dev/null; then
    install -m 755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian trixie stable" \
        > /etc/apt/sources.list.d/docker.list
    apt-get update
    apt-get -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
fi
# Log size limit for all containers
if write_file /etc/docker/daemon.json <<'EOF'
{
  "log-driver": "json-file",
  "log-opts": {"max-size": "10m", "max-file": "3"}
}
EOF
then
    systemctl restart docker
fi
docker compose version

step "User $DEPLOY_USER"
if ! id "$DEPLOY_USER" &> /dev/null; then
    useradd --create-home --shell /bin/bash "$DEPLOY_USER"
fi
# The docker group is root-equivalent anyway, so sudo asks no password
usermod -aG sudo,docker "$DEPLOY_USER"
# No password; SSH keys still work
usermod -p '*' "$DEPLOY_USER"
write_file "/etc/sudoers.d/$DEPLOY_USER" <<EOF || true
$DEPLOY_USER ALL=(ALL) NOPASSWD: ALL
EOF
chmod 440 "/etc/sudoers.d/$DEPLOY_USER"
visudo -cf "/etc/sudoers.d/$DEPLOY_USER"
DEPLOY_HOME=$(getent passwd "$DEPLOY_USER" | cut -d: -f6)
KEYS_FILE="$DEPLOY_HOME/.ssh/authorized_keys"
install -d -m 700 -o "$DEPLOY_USER" -g "$DEPLOY_USER" "$DEPLOY_HOME/.ssh"
touch "$KEYS_FILE"
if [[ -n $DEPLOY_PUBLIC_KEYS ]]; then
    printf '%s\n' "$DEPLOY_PUBLIC_KEYS" >> "$KEYS_FILE"
else
    cat /root/.ssh/authorized_keys >> "$KEYS_FILE"
fi
sort -u "$KEYS_FILE" | sed '/^[[:space:]]*$/d' > "$KEYS_FILE.new"
mv "$KEYS_FILE.new" "$KEYS_FILE"
chown "$DEPLOY_USER:$DEPLOY_USER" "$KEYS_FILE"
chmod 600 "$KEYS_FILE"
[[ -s $KEYS_FILE ]] || fail "$KEYS_FILE is empty"

step "Folders"
install -d -o "$DEPLOY_USER" -g "$DEPLOY_USER" "$APP_DIR"
install -d "$ACME_DIR"
install -d -m 755 "$CERT_DIR"

step "Environment file $ENV_FILE"
if [[ ! -e $ENV_FILE ]]; then
    if [[ -n $BOOTSTRAP_ADMIN_STEAM_ID ]]; then
        STEAM_ID_LINE="BOOTSTRAP_ADMIN_STEAM_ID=$BOOTSTRAP_ADMIN_STEAM_ID"
    else
        STEAM_ID_LINE="#BOOTSTRAP_ADMIN_STEAM_ID="
    fi
    if [[ -n $STEAM_WEB_API_KEY ]]; then
        STEAM_KEY_LINE="STEAM_WEB_API_KEY=$STEAM_WEB_API_KEY"
    else
        STEAM_KEY_LINE="#STEAM_WEB_API_KEY="
    fi
    # Aa1_ makes the password pass the app's rules
    (
        umask 077
        cat > "$ENV_FILE" <<EOF
# Generated by prepare-server.sh. Variables are described in backend/.env.example
POSTGRES_USER=workshop_manager
POSTGRES_PASSWORD=$(openssl rand -hex 24)
REDIS_PASSWORD=$(openssl rand -hex 24)
S3_ACCESS_KEY=$(openssl rand -hex 12)
S3_SECRET_KEY=$(openssl rand -hex 24)
JWT_SECRET_KEY=$(head -c 48 /dev/urandom | base64 -w0)
FRONTEND_URL=https://$DOMAIN
# APP_IMAGE and STEAMCMD_IMAGE: set by deploy.sh

# First administrator: remove these lines after the first sign-in
BOOTSTRAP_ADMIN_USERNAME=admin
BOOTSTRAP_ADMIN_PASSWORD=$(openssl rand -hex 10)Aa1_
$STEAM_ID_LINE

$STEAM_KEY_LINE

GRAFANA_ADMIN_PASSWORD=$(openssl rand -hex 16)
TLS_CERT_DIR=$CERT_DIR
EOF
    )
    chown "$DEPLOY_USER:$DEPLOY_USER" "$ENV_FILE"
else
    echo "Exists, left as is"
fi

step "Certificates"
# nginx needs a certificate to start; the first deploy replaces this one
if [[ ! -s $CERT_DIR/fullchain.pem ]]; then
    openssl req -x509 -newkey rsa:2048 -nodes -days 30 -subj "/CN=$DOMAIN" \
        -keyout "$CERT_DIR/privkey.pem" -out "$CERT_DIR/fullchain.pem"
    chmod 600 "$CERT_DIR/privkey.pem"
fi
# certbot's files are links nginx can't follow, so they are copied
write_file /usr/local/sbin/workshop-cert-deploy-hook <<EOF || true
#!/bin/sh
set -e
install -m 644 "\$RENEWED_LINEAGE/fullchain.pem" $CERT_DIR/fullchain.pem
install -m 600 "\$RENEWED_LINEAGE/privkey.pem" $CERT_DIR/privkey.pem
docker exec workshop-manager-nginx-1 nginx -s reload
EOF
chmod 755 /usr/local/sbin/workshop-cert-deploy-hook
if [[ -n $LETSENCRYPT_EMAIL ]]; then
    EMAIL_ARGS="--email $LETSENCRYPT_EMAIL"
else
    EMAIL_ARGS="--register-unsafely-without-email"
fi
# Run by deploy.sh; certbot.timer renews with the same hook
write_file /usr/local/sbin/workshop-cert <<EOF || true
#!/bin/sh
set -e
certbot certonly --webroot -w $ACME_DIR -d $DOMAIN $EMAIL_ARGS --agree-tos --non-interactive \\
    --deploy-hook /usr/local/sbin/workshop-cert-deploy-hook
EOF
chmod 755 /usr/local/sbin/workshop-cert

step "SSH: keys only, no root"
# sshd keeps the first value it reads, so this loads before e.g. 50-cloud-init.conf
if write_file /etc/ssh/sshd_config.d/00-hardening.conf <<'EOF'
PermitRootLogin no
PasswordAuthentication no
KbdInteractiveAuthentication no
PubkeyAuthentication yes
EOF
then
    sshd -t
    systemctl reload ssh
fi
sshd -T | grep -E '^(permitrootlogin|passwordauthentication|pubkeyauthentication) '

step "Done"
cat <<EOF
Sign in as $DEPLOY_USER from now on: ssh $DEPLOY_USER@$(hostname -I | awk '{print $1}')
First administrator's password (created on the first start): sudo grep BOOTSTRAP_ $ENV_FILE
EOF
