#!/bin/bash

# ============================================================================
# Script de Démarrage Rapide - Flux Financiers
# ============================================================================

echo "🚀 Démarrage de l'application Flux Financiers..."
echo ""

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé !"
    echo "📥 Installez Docker Desktop : https://www.docker.com/products/docker-desktop/"
    exit 1
fi

# Vérifier si Docker Compose est installé
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose n'est pas installé !"
    exit 1
fi

echo "✅ Docker et Docker Compose sont installés"
echo ""

# Arrêter les conteneurs existants (si présents)
echo "🛑 Arrêt des conteneurs existants..."
docker-compose down

echo ""
echo "🔨 Construction et démarrage des services..."
docker-compose up -d --build

echo ""
echo "⏳ Attente du démarrage des services..."
sleep 10

echo ""
echo "📊 État des conteneurs :"
docker-compose ps

echo ""
echo "============================================================================"
echo "✅ APPLICATION DÉMARRÉE AVEC SUCCÈS !"
echo "============================================================================"
echo ""
echo "🌐 Accès aux services :"
echo ""
echo "   Frontend (Vue.js)  : http://localhost:5173"
echo "   Backend API        : http://localhost:8080/api"
echo "   MySQL              : localhost:3307"
echo ""
echo "🔐 Credentials par défaut :"
echo ""
echo "   Username : admin"
echo "   Password : admin123"
echo ""
echo "============================================================================"
echo ""
echo "📋 Commandes utiles :"
echo ""
echo "   Voir les logs       : docker-compose logs -f"
echo "   Arrêter             : docker-compose down"
echo "   Redémarrer          : docker-compose restart"
echo "   Reconstruire        : docker-compose up -d --build"
echo ""
echo "============================================================================"
echo ""
echo "🎉 Bon développement !"
echo ""
