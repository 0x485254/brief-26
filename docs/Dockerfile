# ------------------------------------------------------------------------------
# Dockerfile multi-stage pour project Node.js + pnpm + TypeScript
# ------------------------------------------------------------------------------

# ---- Build Stage ----
FROM node:18-alpine AS builder

WORKDIR /app

# pnpm installation (dernière version)
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copie des manifests et lockfile, pour le cache optimal
COPY package.json pnpm-lock.yaml ./

# Installation des dépendances production & dev
RUN pnpm install --frozen-lockfile

# Copie du code source
COPY . .

# Transpile le code TypeScript
RUN pnpm run build
RUN ls

# ---- Production Stage ----
FROM node:18-alpine AS production

WORKDIR /app

# pnpm installation minimale (runtime)
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copie package.json uniquement (pour lecture env/metadata)
COPY package.json ./

# Copie le build transpile et les node_modules du builder
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules

# Copy healthcheck script and make it executable
COPY healthcheck.sh ./
RUN chmod +x /app/healthcheck.sh

# Lancement
ENV HOST=0.0.0.0
ENV PORT=3000
EXPOSE 3000
CMD ["node", "./dist/server/entry.mjs"]
