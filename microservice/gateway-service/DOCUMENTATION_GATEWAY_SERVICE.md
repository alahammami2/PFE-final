# Gateway Service - Documentation Complète

## Vue d'ensemble

Le **Gateway Service** est l'API Gateway de la plateforme intelligente de gestion d'équipe de volley-ball. Il sert de point d'entrée unique pour tous les microservices, offrant des fonctionnalités de routage, sécurité, limitation de débit et monitoring.

## Architecture Technique

### Technologies Utilisées
- **Spring Boot**: 3.2.0
- **Spring Cloud Gateway**: 2023.0.0
- **Java**: 17
- **Redis**: 7 (pour le rate limiting)
- **JWT**: Pour l'authentification
- **Docker**: Pour le déploiement

### Configuration des Ports
- **Gateway Service**: 8080
- **Redis**: 6379

## Fonctionnalités Principales

### 1. Routage Intelligent
Le gateway route automatiquement les requêtes vers les microservices appropriés :

| Service | Port | Endpoint |
|---------|------|----------|
| Performance Service | 8083 | `/api/performance/**` |
| Messaging Service | 8084 | `/api/messaging/**` |
| Admin Request Service | 8085 | `/api/admin-requests/**` |
| Medical Service | 8086 | `/api/medical/**` |
| Finance Service | 8087 | `/api/finance/**` |
| Auth Service | 8088 | `/api/auth/**` |
| Planning Service | 8089 | `/api/planning/**` |

### 2. Sécurité JWT
- Validation automatique des tokens JWT
- Extraction des informations utilisateur
- Ajout d'headers pour les services downstream
- Endpoints publics configurables

### 3. Rate Limiting
- Limitation basée sur Redis
- Configuration par service
- Identification par IP ou utilisateur
- Fenêtres temporelles configurables

### 4. CORS Support
- Configuration globale CORS
- Support multi-origines
- Headers personnalisables

## Endpoints du Gateway

### Endpoints de Management

#### GET /api/gateway/health
Vérification de l'état du gateway
```json
{
  "status": "UP",
  "service": "gateway-service",
  "timestamp": 1704067200000,
  "version": "1.0.0"
}
```

#### GET /api/gateway/routes
Liste des routes configurées
```json
{
  "routes": {
    "performance-service": "http://localhost:8083/api/performance",
    "messaging-service": "http://localhost:8084/api/messaging",
    "admin-request-service": "http://localhost:8085/api/admin-requests",
    "medical-service": "http://localhost:8086/api/medical",
    "finance-service": "http://localhost:8087/api/finance",
    "auth-service": "http://localhost:8088/api/auth",
    "planning-service": "http://localhost:8089/api/planning"
  },
  "gateway": "http://localhost:8080"
}
```

#### POST /api/gateway/validate-token
Validation d'un token JWT
```bash
curl -X POST http://localhost:8080/api/gateway/validate-token \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### GET /api/gateway/info
Informations sur le gateway
```json
{
  "name": "Volleyball Platform API Gateway",
  "version": "1.0.0",
  "description": "API Gateway for Volleyball Team Management Platform",
  "port": 8080,
  "features": {
    "routing": "Routes requests to appropriate microservices",
    "security": "JWT-based authentication and authorization",
    "rate-limiting": "Redis-based request throttling",
    "cors": "Cross-Origin Resource Sharing support",
    "monitoring": "Health checks and metrics"
  }
}
```

### Endpoints Actuator

#### GET /actuator/health
```json
{
  "status": "UP",
  "components": {
    "redis": {
      "status": "UP"
    }
  }
}
```

#### GET /actuator/gateway/routes
Routes Spring Cloud Gateway détaillées

## Configuration

### Rate Limiting par Service
```yaml
# Performance Service: 10 req/sec, burst 20
# Messaging Service: 10 req/sec, burst 20
# Admin Request Service: 5 req/sec, burst 10
# Medical Service: 10 req/sec, burst 20
# Finance Service: 5 req/sec, burst 10
# Auth Service: 20 req/sec, burst 50
# Planning Service: 10 req/sec, burst 20
```

### JWT Configuration
- **Secret**: volleyball-platform-secret-key-2024-very-secure-and-long-enough
- **Expiration**: 24 heures
- **Algorithm**: HS256

### Endpoints Publics
- `/api/auth/**` - Authentification
- `/actuator/**` - Monitoring
- `/health` - Health check
- `/favicon.ico` - Favicon

## Déploiement Docker

### Démarrage Rapide
```bash
# Cloner et naviguer vers le répertoire
cd gateway-service

# Démarrer avec Docker Compose
./start-docker.bat
```

### Services Docker
- **gateway-redis**: Redis pour rate limiting
- **gateway-service**: Application Spring Boot

### Vérification du Déploiement
```bash
# Vérifier les containers
docker-compose ps

# Vérifier les logs
docker-compose logs gateway-service

# Test de santé
curl http://localhost:8080/actuator/health
```

## Tests et Validation

### Tests de Routage
```bash
# Test route vers performance service
curl http://localhost:8080/api/performance/players

# Test route vers messaging service
curl http://localhost:8080/api/messaging/messages

# Test route vers medical service
curl http://localhost:8080/api/medical/health-records
```

### Tests de Rate Limiting
```bash
# Test de limitation (dépasse la limite)
for i in {1..25}; do curl http://localhost:8080/api/finance/budgets; done
```

### Tests JWT
```bash
# Test sans token (doit être rejeté en production)
curl http://localhost:8080/api/performance/players

# Test avec token valide
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/performance/players
```

## Monitoring et Logs

### Logs Disponibles
- Requêtes entrantes et sortantes
- Validation JWT
- Rate limiting
- Erreurs de routage

### Métriques
- Nombre de requêtes par service
- Temps de réponse
- Erreurs HTTP
- Utilisation Redis

## Sécurité

### Headers Ajoutés
Le gateway ajoute automatiquement ces headers aux requêtes downstream :
- `X-User-Id`: ID de l'utilisateur
- `X-Username`: Nom d'utilisateur
- `X-User-Role`: Rôle de l'utilisateur

### Mode Développement vs Production
- **Développement**: Authentification désactivée pour faciliter les tests
- **Production**: Authentification JWT obligatoire (à activer dans SecurityConfig)

## Dépannage

### Problèmes Courants

#### Gateway ne démarre pas
```bash
# Vérifier les ports
netstat -an | findstr :8080
netstat -an | findstr :6379

# Vérifier Redis
docker-compose logs gateway-redis
```

#### Erreurs de routage
```bash
# Vérifier que les services cibles sont démarrés
curl http://localhost:8083/actuator/health  # performance
curl http://localhost:8084/actuator/health  # messaging
curl http://localhost:8085/actuator/health  # admin-request
curl http://localhost:8086/actuator/health  # medical
curl http://localhost:8087/actuator/health  # finance
```

#### Rate Limiting ne fonctionne pas
```bash
# Vérifier Redis
docker exec -it gateway-redis redis-cli ping

# Vérifier les clés Redis
docker exec -it gateway-redis redis-cli keys "rate_limit:*"
```

## URLs Importantes

- **Gateway**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Routes Info**: http://localhost:8080/api/gateway/routes
- **Service Info**: http://localhost:8080/api/gateway/info
- **Redis**: localhost:6379

## Commandes Utiles

```bash
# Démarrer le gateway
./start-docker.bat

# Arrêter le gateway
docker-compose down

# Voir les logs en temps réel
docker-compose logs -f gateway-service

# Redémarrer uniquement le gateway
docker-compose restart gateway-service

# Nettoyer et redémarrer
docker-compose down && docker system prune -f && docker-compose up --build -d
```

## Intégration avec les Microservices

Le Gateway Service est conçu pour s'intégrer parfaitement avec tous les microservices de la plateforme :
- Performance Service (port 8083)
- Messaging Service (port 8084)
- Admin Request Service (port 8085)
- Medical Service (port 8086)
- Finance Service (port 8087)
- Auth Service (port 8088)
- Planning Service (port 8089)

Tous les microservices sont accessibles via le gateway à l'adresse `http://localhost:8080` avec leurs endpoints respectifs.
