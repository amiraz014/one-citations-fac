#!/bin/sh

# Script pour configurer Kong avec les services et routes
# Attendre que Kong soit disponible
echo "Attente de Kong..."
until curl -s http://kong:8001/health > /dev/null; do
  echo "Kong pas encore prêt, nouvelle tentative dans 5 secondes..."
  sleep 5
done

echo "Kong est prêt ! Configuration en cours..."

# Variables
KONG_ADMIN_URL="http://kong:8001"

# 1. Créer le service profiles-api
echo "Création du service profiles-api..."
curl -X POST "$KONG_ADMIN_URL/services" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "profiles-api",
    "url": "http://profiles-api:8080",
    "protocol": "http"
  }' 2>/dev/null

# 2. Créer les routes pour profiles-api
echo "Création des routes pour profiles-api..."
curl -X POST "$KONG_ADMIN_URL/services/profiles-api/routes" \
  -H "Content-Type: application/json" \
  -d '{
    "paths": ["/api/v1/profiles"],
    "name": "profiles-routes"
  }' 2>/dev/null

# 3. Créer le service citations-api
echo "Création du service citations-api..."
curl -X POST "$KONG_ADMIN_URL/services" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "citations-api",
    "url": "http://citations-api:8080",
    "protocol": "http"
  }' 2>/dev/null

# 4. Créer les routes pour citations-api
echo "Création des routes pour citations-api..."
curl -X POST "$KONG_ADMIN_URL/services/citations-api/routes" \
  -H "Content-Type: application/json" \
  -d '{
    "paths": ["/api/v1/citations"],
    "name": "citations-routes"
  }' 2>/dev/null

# 5. Créer le service images-api
echo "Création du service images-api..."
curl -X POST "$KONG_ADMIN_URL/services" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "images-api",
    "url": "http://images-api:8080",
    "protocol": "http"
  }' 2>/dev/null

# 6. Créer les routes pour images-api
echo "Création des routes pour images-api..."
curl -X POST "$KONG_ADMIN_URL/services/images-api/routes" \
  -H "Content-Type: application/json" \
  -d '{
    "paths": ["/api/v1/images"],
    "name": "images-routes"
  }' 2>/dev/null

# 7. Activer le plugin key-auth sur les services
echo "Activation du plugin key-auth sur profiles-api..."
curl -X POST "$KONG_ADMIN_URL/services/profiles-api/plugins" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "key-auth"
  }' 2>/dev/null

echo "Activation du plugin key-auth sur citations-api..."
curl -X POST "$KONG_ADMIN_URL/services/citations-api/plugins" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "key-auth"
  }' 2>/dev/null

echo "Activation du plugin key-auth sur images-api..."
curl -X POST "$KONG_ADMIN_URL/services/images-api/plugins" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "key-auth"
  }' 2>/dev/null

# 8. Créer un consumer 'swagger-client'
echo "Création du consumer swagger-client..."
curl -X POST "$KONG_ADMIN_URL/consumers" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "swagger-client"
  }' 2>/dev/null

# 9. Créer une clé API pour le consumer
echo "Création d'une clé API pour swagger-client..."
curl -X POST "$KONG_ADMIN_URL/consumers/swagger-client/key-auth" \
  -H "Content-Type: application/json" \
  -d '{
    "key": "swagger-api-key-12345"
  }' 2>/dev/null

echo ""
echo "✅ Configuration Kong terminée !"
echo ""
echo "Services disponibles via Kong :"
echo "  - http://localhost:8000/api/v1/profiles"
echo "  - http://localhost:8000/api/v1/citations"
echo "  - http://localhost:8000/api/v1/images"
echo ""
echo "Clé API : swagger-api-key-12345"
echo ""
echo "Exemple d'appel :"
echo "  curl -X GET http://localhost:8000/api/v1/citations/random?apikey=swagger-api-key-12345"
