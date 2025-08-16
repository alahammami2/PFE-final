# Discovery Service - Documentation Complète

## Vue d'ensemble

Le **Discovery Service** est le service de découverte Eureka de la plateforme intelligente de gestion d'équipe de volley-ball. Il permet aux microservices de s'enregistrer automatiquement et de se découvrir mutuellement, facilitant la communication inter-services et la scalabilité.

## Architecture Technique

### Technologies Utilisées
- **Spring Boot**: 3.2.0
- **Spring Cloud Netflix Eureka Server**: 2023.0.0
- **Java**: 17
- **Spring Security**: Pour l'authentification
- **Docker**: Pour le déploiement

### Configuration des Ports
- **Eureka Server**: 8761
- **Management/Actuator**: 8762

## Fonctionnalités Principales

### 1. Service Discovery
- Enregistrement automatique des microservices
- Découverte dynamique des services
- Health checks automatiques
- Load balancing côté client

### 2. Dashboard Web Eureka
- Interface graphique pour visualiser les services
- Monitoring en temps réel
- Informations détaillées sur chaque service

### 3. Sécurité
- Authentification Basic Auth
- Credentials: eureka/eureka123
- Protection des endpoints sensibles

### 4. Monitoring
- Health checks via Actuator
- Métriques de performance
- Logs détaillés

## Services Enregistrés

Le Discovery Service est conçu pour enregistrer tous les microservices de la plateforme :

| Service | Port | Application Name |
|---------|------|------------------|
| Performance Service | 8083 | performance-service |
|  |  |  |
| Admin Request Service | 8085 | admin-request-service |
| Medical Service | 8086 | medical-service |
| Finance Service | 8087 | finance-service |
| Auth Service | 8088 | auth-service |
| Planning Service | 8089 | planning-service |
| Gateway Service | 8080 | gateway-service |

## Endpoints du Discovery Service

### Dashboard Eureka
#### GET http://localhost:8761
Interface web principale d'Eureka
- **Authentification**: eureka/eureka123
- **Fonctionnalités**: Visualisation des services, statuts, instances

### Endpoints API

#### GET /api/discovery/health
Vérification de l'état du service
```json
{
  "status": "UP",
  "service": "discovery-service",
  "timestamp": 1704067200000,
  "version": "1.0.0",
  "eureka-server": "RUNNING"
}
```

#### GET /api/discovery/info
Informations détaillées sur le service
```json
{
  "name": "Volleyball Platform Discovery Service",
  "version": "1.0.0",
  "description": "Eureka Discovery Service for Volleyball Team Management Platform",
  "port": 8761,
  "management-port": 8762,
  "features": {
    "service-discovery": "Automatic service registration and discovery",
    "health-monitoring": "Service health checks and monitoring",
    "load-balancing": "Client-side load balancing support",
    "failover": "Automatic failover and recovery",
    "dashboard": "Web-based Eureka dashboard"
  },
  "urls": {
    "eureka-dashboard": "http://localhost:8761",
    "health-check": "http://localhost:8762/actuator/health",
    "service-registry": "http://localhost:8761/eureka/apps"
  }
}
```

#### GET /api/discovery/services
Liste des services enregistrés
```json
{
  "registered-services-count": 8,
  "applications": [
    {
      "name": "PERFORMANCE-SERVICE",
      "instances": 1
    },
    {
      
      "instances": 1
    }
  ]
}
```

#### GET /api/discovery/status
Statut du serveur Eureka
```json
{
  "server-status": "RUNNING",
  "self-preservation-mode": false,
  "registered-replicas": 8,
  "available-replicas": 8,
  "unavailable-replicas": 0
}
```

### Endpoints Actuator

#### GET /actuator/health
```json
{
  "status": "UP",
  "components": {
    "eureka": {
      "status": "UP"
    }
  }
}
```

#### GET /actuator/eureka
Informations détaillées sur Eureka

## Configuration Eureka

### Paramètres Serveur
```yaml
eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 10000
    renewal-percent-threshold: 0.85
    response-cache-update-interval-ms: 5000
    response-cache-auto-expiration-in-seconds: 180
```

### Paramètres Instance
```yaml
eureka:
  instance:
    hostname: localhost
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

### Sécurité
- **Username**: eureka
- **Password**: eureka123
- **Rôle**: ADMIN

## Intégration avec les Microservices

### Configuration Client Eureka
Pour enregistrer un microservice avec Eureka, ajoutez dans `application.yml` :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka:eureka123@localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30

spring:
  application:
    name: your-service-name
```

### Dépendances Maven
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Annotation Application
```java
@SpringBootApplication
@EnableEurekaClient
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

## Déploiement Docker

### Démarrage Rapide
```bash
# Cloner et naviguer vers le répertoire
cd discovery-service

# Démarrer avec Docker Compose
./start-docker.bat
```

### Vérification du Déploiement
```bash
# Vérifier les containers
docker-compose ps

# Vérifier les logs
docker-compose logs discovery-service

# Test de santé
curl http://localhost:8762/actuator/health

# Accès au dashboard (avec authentification)
curl -u eureka:eureka123 http://localhost:8761
```

## Tests et Validation

### Tests de Connectivité
```bash
# Test health check
curl http://localhost:8762/actuator/health

# Test API endpoints
curl http://localhost:8762/api/discovery/health
curl http://localhost:8762/api/discovery/info
curl http://localhost:8762/api/discovery/services
curl http://localhost:8762/api/discovery/status
```

### Tests Dashboard
```bash
# Accès au dashboard Eureka
http://localhost:8761
# Credentials: eureka/eureka123
```

### Tests d'Enregistrement
```bash
# Vérifier les services enregistrés
curl http://localhost:8761/eureka/apps

# Format JSON
curl -H "Accept: application/json" http://localhost:8761/eureka/apps
```

## Monitoring et Logs

### Logs Disponibles
- Enregistrement/désenregistrement des services
- Health checks des instances
- Événements de découverte
- Erreurs de communication

### Métriques
- Nombre de services enregistrés
- Instances actives/inactives
- Temps de réponse
- Taux de renouvellement des baux

## Dépannage

### Problèmes Courants

#### Discovery Service ne démarre pas
```bash
# Vérifier les ports
netstat -an | findstr :8761
netstat -an | findstr :8762

# Vérifier les logs
docker-compose logs discovery-service
```

#### Services ne s'enregistrent pas
```bash
# Vérifier la configuration client
# Vérifier la connectivité réseau
curl http://localhost:8761/eureka/apps

# Vérifier les credentials
curl -u eureka:eureka123 http://localhost:8761/eureka/apps
```

#### Dashboard inaccessible
```bash
# Vérifier l'authentification
curl -u eureka:eureka123 http://localhost:8761

# Vérifier les logs de sécurité
docker-compose logs discovery-service | grep -i security
```

## URLs Importantes

- **Eureka Dashboard**: http://localhost:8761 (eureka/eureka123)
- **Health Check**: http://localhost:8762/actuator/health
- **Service Info**: http://localhost:8762/api/discovery/info
- **Service Status**: http://localhost:8762/api/discovery/status
- **Registered Services**: http://localhost:8762/api/discovery/services
- **Service Registry**: http://localhost:8761/eureka/apps

## Commandes Utiles

```bash
# Démarrer le discovery service
./start-docker.bat

# Arrêter le discovery service
docker-compose down

# Voir les logs en temps réel
docker-compose logs -f discovery-service

# Redémarrer uniquement le discovery service
docker-compose restart discovery-service

# Nettoyer et redémarrer
docker-compose down && docker system prune -f && docker-compose up --build -d

# Vérifier les services enregistrés
curl -u eureka:eureka123 http://localhost:8761/eureka/apps

# Monitoring en temps réel
watch -n 5 'curl -s http://localhost:8762/api/discovery/services'
```

## Intégration avec Gateway Service

Le Discovery Service s'intègre parfaitement avec le Gateway Service pour permettre :
- Découverte automatique des routes
- Load balancing dynamique
- Failover automatique
- Mise à jour des routes en temps réel

### Configuration Gateway avec Eureka
```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka:eureka123@localhost:8761/eureka/
```

## Sécurité et Bonnes Pratiques

### Recommandations de Sécurité
- Changer les credentials par défaut en production
- Utiliser HTTPS en production
- Configurer des réseaux Docker isolés
- Limiter l'accès aux endpoints sensibles

### Monitoring Recommandé
- Surveiller le nombre de services enregistrés
- Alertes sur les services indisponibles
- Monitoring des temps de réponse
- Logs centralisés

Le Discovery Service est maintenant prêt pour déploiement et servira de registre central pour tous les microservices de la plateforme de volley-ball.
