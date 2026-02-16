# ============================================================================
# Dockerfile Frontend Vue.js 3
# ============================================================================

FROM node:20-alpine

WORKDIR /app

# Copier les fichiers de configuration
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Copier tout le projet
COPY . .

# Exposer le port 5173 (Vite dev server)
EXPOSE 5173

# Démarrer le serveur de développement
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
