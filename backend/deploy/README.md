# Deploy: steps by hand

Everything else is automated by `.github/workflows/` (`provision.yml`, `deploy.yml`) and the scripts here.

## Once per GitHub repository

1. CI key pair: `ssh-keygen -t ed25519 -N "" -C wm-deploy-ci -f wm_deploy_ci`
2. Settings → Environments → `production` (deployment branches: no restriction):
    - variables: `DEPLOY_HOST`, `DOMAIN`, `DEPLOY_PUBLIC_KEYS` (your own public key + `wm_deploy_ci.pub`, one per
      line), optional `LETSENCRYPT_EMAIL`, `BOOTSTRAP_ADMIN_STEAM_ID`
    - secrets: `DEPLOY_SSH_KEY` (the whole content of `wm_deploy_ci` file), optional `STEAM_WEB_API_KEY`
3. Delete both `wm_deploy_ci` files. Your own private key stays on your computer; the CI's lives only in GitHub.

## Once per server

1. Order Debian 13. Point the domain's A record to it.
   Check: `dig +short NS DOMAIN` shows your DNS provider, `dig +short DOMAIN` shows the server's IP.
2. A new root key pair (each server gets its own; provisioning disables root sign-in):
   `ssh-keygen -t ed25519 -N "" -C wm-root -f wm_root`, then `ssh-copy-id -f -i wm_root.pub root@SERVER`, and put
   the whole `wm_root` file into the `ROOT_SSH_KEY` secret. Delete both `wm_root` files afterwards.
3. Set `DEPLOY_HOST` to the new address.
4. Actions → Provision server → Run. Save the host key line from its summary as the `SSH_KNOWN_HOSTS` variable.
5. Actions → Deploy → Run.
6. Sign in as `admin`. The password: `ssh deploy@SERVER sudo grep BOOTSTRAP_ /opt/workshop-manager/.env`.
   Afterwards remove the `BOOTSTRAP_` lines from that file.

Editing `.env` later (e.g. a new Steam key): `ssh deploy@SERVER`, then
`cd /opt/workshop-manager && nano .env && docker compose up -d`. Deploys only rewrite `APP_IMAGE` and
`STEAMCMD_IMAGE` there, so your edits stay. Change the GitHub variable or secret too, so a new server gets the
same value.

## Releases

- Deploy: push a tag (`git tag v1.2.0 && git push origin v1.2.0`), or Actions → Deploy → Run.
- Rollback: Actions → Deploy → Run with an image tag (`v1.1.0` or `sha-1a2b3c4`).
- Logs: `ssh deploy@SERVER`, then `cd /opt/workshop-manager && docker compose logs -f app`.

## Not automated

- Backups: the data lives only on the server (Docker volumes `workshop-manager_postgres_data`,
  `workshop-manager_seaweedfs_data`).
- Debian 14: change `trixie` in `prepare-server.sh` (the version check and the Docker repository).
