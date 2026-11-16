#!/bin/bash
# Deploy do DPB na EC2 (Ubuntu 24.04).
# Uso (dentro da EC2, via SSH):  bash deploy.sh
# Pode ser rodado de novo para atualizar o código (git pull + rebuild).
set -euo pipefail

REPO="https://github.com/jframnp/lab-engenharia-doacoes.git"
APP_DIR="$HOME/dpb"
WEB_DIR="/var/www/dpb"

echo "==> Instalando Node.js 20, nginx e pm2"
if ! command -v node >/dev/null 2>&1; then
  curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
  sudo apt-get install -y nodejs
fi
sudo apt-get install -y nginx git
command -v pm2 >/dev/null 2>&1 || sudo npm install -g pm2

echo "==> Baixando o código"
if [ -d "$APP_DIR/.git" ]; then
  git -C "$APP_DIR" pull
else
  git clone "$REPO" "$APP_DIR"
fi

echo "==> Back-end (API na porta 3333)"
cd "$APP_DIR/backend"
npm ci --no-audit --no-fund
if [ ! -f .env ]; then
  cp .env.example .env
  sed -i "s/^JWT_SECRET=.*/JWT_SECRET=$(openssl rand -hex 32)/" .env
fi
pm2 delete dpb-api >/dev/null 2>&1 || true
pm2 start src/server.js --name dpb-api
pm2 save

echo "==> Front-end (build)"
cd "$APP_DIR/frontend"
npm ci --no-audit --no-fund
npm run build
sudo mkdir -p "$WEB_DIR"
sudo rm -rf "$WEB_DIR"/*
sudo cp -r dist/* "$WEB_DIR"/

echo "==> nginx"
sudo tee /etc/nginx/sites-available/dpb >/dev/null <<'NGINX'
server {
    listen 80 default_server;
    server_name _;
    root /var/www/dpb;
    index index.html;
    client_max_body_size 10m;

    location /api/     { proxy_pass http://127.0.0.1:3333; proxy_set_header Host $host; }
    location /uploads/ { proxy_pass http://127.0.0.1:3333; }
    location /         { try_files $uri /index.html; }
}
NGINX
sudo ln -sf /etc/nginx/sites-available/dpb /etc/nginx/sites-enabled/dpb
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl enable --now nginx
sudo systemctl reload nginx

IP=$(curl -s http://checkip.amazonaws.com || echo "<ip-publico>")
echo ""
echo "Pronto! Abra: http://$IP"
echo "Logins de demonstracao: doador@dpb.com / admin@dpb.com (senha 123456)"
