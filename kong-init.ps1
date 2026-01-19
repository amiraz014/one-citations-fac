$KONG_ADMIN_URL = "http://localhost:8001"

Write-Host "Configuration de Kong..." -ForegroundColor Green

Write-Host "Création du service profiles-api..."
$profilesService = @{
    name = "profiles-api"
    url = "http://host.docker.internal:8082"
    protocol = "http"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services" `
    -Method POST `
    -ContentType "application/json" `
    -Body $profilesService | Out-Null

Write-Host "Création des routes pour profiles-api..."
$profilesRoute = @{
    paths = @("/api/v1/profiles")
    name = "profiles-routes"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services/profiles-api/routes" `
    -Method POST `
    -ContentType "application/json" `
    -Body $profilesRoute | Out-Null

Write-Host "Création du service citations-api..."
$citationsService = @{
    name = "citations-api"
    url = "http://host.docker.internal:8083"
    protocol = "http"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services" `
    -Method POST `
    -ContentType "application/json" `
    -Body $citationsService | Out-Null

Write-Host "Création des routes pour citations-api..."
$citationsRoute = @{
    paths = @("/api/v1/citations")
    name = "citations-routes"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services/citations-api/routes" `
    -Method POST `
    -ContentType "application/json" `
    -Body $citationsRoute | Out-Null

# 5. Créer le service images-api
$imagesService = @{
    name = "images-api"
    url = "http://host.docker.internal:8084"
    protocol = "http"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services" `
    -Method POST `
    -ContentType "application/json" `
    -Body $imagesService | Out-Null

# 6. Créer les routes pour images-api
$imagesRoute = @{
    paths = @("/api/v1/images")
    name = "images-routes"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services/images-api/routes" `
    -Method POST `
    -ContentType "application/json" `
    -Body $imagesRoute | Out-Null

# 7. Activer le plugin key-auth sur les services
$keyAuthPlugin = @{
    name = "key-auth"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services/profiles-api/plugins" `
    -Method POST `
    -ContentType "application/json" `
    -Body $keyAuthPlugin | Out-Null

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services/citations-api/plugins" `
    -Method POST `
    -ContentType "application/json" `
    -Body $keyAuthPlugin | Out-Null

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/services/images-api/plugins" `
    -Method POST `
    -ContentType "application/json" `
    -Body $keyAuthPlugin | Out-Null

# 8. Créer un consumer 'swagger-client'
$consumer = @{
    username = "swagger-client"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/consumers" `
    -Method POST `
    -ContentType "application/json" `
    -Body $consumer | Out-Null

Write-Host "Création d'une clé API pour swagger-client..."
    key = "swagger-api-key-12345"
} | ConvertTo-Json

Invoke-WebRequest -Uri "$KONG_ADMIN_URL/consumers/swagger-client/key-auth" `
    -Method POST `
    -ContentType "application/json" `
    -Body $apiKey | Out-Null

Write-Host ""
Write-Host "✅ Configuration Kong terminée !" -ForegroundColor Green
Write-Host ""
Write-Host "Services disponibles via Kong :" -ForegroundColor Cyan
Write-Host "  - http://localhost:8000/api/v1/profiles"
Write-Host "  - http://localhost:8000/api/v1/citations"
Write-Host "  - http://localhost:8000/api/v1/images"
Write-Host ""
Write-Host "Clé API : swagger-api-key-12345" -ForegroundColor Yellow
Write-Host ""
Write-Host "Exemple d'appel :" -ForegroundColor Cyan
Write-Host "  curl -X GET http://localhost:8000/api/v1/citations/random?apikey=swagger-api-key-12345"
