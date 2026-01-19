# Citations API

Microservice Spring Boot pour la gestion des citations de l'application One Citations.

## Configuration

- **Port par défaut**: 8083
- **Base de données**: MongoDB (one-citations)
- **Authentification**: OAuth2 avec Keycloak

## Lancer localement

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Docker

```bash
docker-compose up
```
