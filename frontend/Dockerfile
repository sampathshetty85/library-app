# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM node:24-alpine AS builder

WORKDIR /app

# Install dependencies first (cached unless package*.json changes)
COPY package*.json ./
RUN npm ci --silent

# Copy source and build
COPY index.html .
COPY public ./public
COPY src ./src
COPY vite.config.js .
RUN npm run build

# ── Stage 2: Serve ───────────────────────────────────────────────────────────
FROM nginx:alpine AS runner

# Copy the built static files into Nginx's default serving directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Use our custom Nginx config (adds /api/ reverse proxy to backend)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
