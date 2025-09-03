# Plateforme Intelligente pour Gérer une Équipe de Volley-ball

## Description du Projet

Cette plateforme est un système complet de gestion d'équipe de volley-ball qui intègre plusieurs microservices pour offrir une solution complète et intelligente.

## Architecture

Le projet est basé sur une architecture microservices avec les composants suivants :

### Frontend
- **Angular** : Interface utilisateur moderne et responsive
- **PrimeNG** : Composants UI avancés
- **Tailwind CSS** : Framework CSS utilitaire

### Microservices Backend

#### Services Principaux
- **Auth Service** : Gestion de l'authentification et des autorisations
- **Gateway Service** : Point d'entrée principal de l'API
- **Discovery Service** : Service de découverte des microservices
- **Admin Request Service** : Gestion des demandes administratives

#### Services Métier
- **Medical Service** : Gestion des données médicales des joueurs
- **Performance Service** : Suivi des performances et statistiques
- **Planning Service** : Gestion de la planification et des événements
- **Finance Service** : Gestion financière et budgétaire
- **Chatbot Gateway** : Interface d'intelligence artificielle

### Technologies Utilisées
- **Java 17** avec **Spring Boot**
- **Maven** pour la gestion des dépendances
- **Docker** pour la conteneurisation
- **Jenkins** pour l'intégration continue

## Structure du Projet

```
├── frontend/                 # Application Angular
├── microservice/            # Microservices Java
│   ├── auth-service/        # Service d'authentification
│   ├── gateway-service/     # Service de passerelle
│   ├── discovery-service/   # Service de découverte
│   ├── admin-request-service/ # Service des demandes admin
│   ├── medical-service/     # Service médical
│   ├── performance-service/ # Service de performance
│   ├── planning-service/    # Service de planification
│   ├── finance-service/     # Service financier
│   └── chatbot-gateway/    # Passerelle chatbot
├── docker-compose.yml       # Configuration Docker
└── Jenkinsfile             # Pipeline CI/CD
```

## Fonctionnalités Principales

### Gestion des Joueurs
- Profils complets avec informations personnelles
- Suivi médical et historique des blessures
- Statistiques de performance détaillées

### Gestion de l'Équipe
- Planification des entraînements et matchs
- Gestion des rôles et permissions
- Communication interne

### Suivi des Performances
- Métriques de performance en temps réel
- Analyses statistiques avancées
- Rapports personnalisés

### Gestion Administrative
- Gestion des demandes et autorisations
- Suivi financier et budgétaire
- Reporting et analytics

## Installation et Démarrage

### Prérequis
- Java 17+
- Node.js 18+
- Docker et Docker Compose
- Maven 3.6+

### Démarrage Rapide

1. **Cloner le repository**
   ```bash
   git clone https://github.com/alahammami2/PFE-final.git
   cd PFE-final
   ```

2. **Démarrer avec Docker**
   ```bash
   docker-compose up -d
   ```

3. **Ou démarrer manuellement**
   ```bash
   # Backend
   cd microservice
   mvn clean install
   mvn spring-boot:run
   
   # Frontend
   cd frontend
   npm install
   npm start
   ```

## Configuration

### Variables d'Environnement
Créez un fichier `.env` à la racine du projet :

```env
# Base de données
DB_HOST=localhost
DB_PORT=5432
DB_NAME=volleyball_db
DB_USER=admin
DB_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Services
AUTH_SERVICE_URL=http://localhost:8081
GATEWAY_SERVICE_URL=http://localhost:8080
```

## API Documentation

Chaque microservice expose sa propre documentation API via Swagger UI :
- Gateway Service : `http://localhost:8080/swagger-ui.html`
- Auth Service : `http://localhost:8081/swagger-ui.html`
- Medical Service : `http://localhost:8082/swagger-ui.html`
- Performance Service : `http://localhost:8083/swagger-ui.html`
- Planning Service : `http://localhost:8084/swagger-ui.html`
- Finance Service : `http://localhost:8085/swagger-ui.html`

## Déploiement

### Production
```bash
# Build des images Docker
docker-compose -f docker-compose.prod.yml build

# Déploiement
docker-compose -f docker-compose.prod.yml up -d
```

### Développement
```bash
# Démarrage des services de développement
docker-compose -f docker-compose.dev.yml up -d
```

## Tests

### Tests Unitaires
```bash
# Backend
cd microservice
mvn test

# Frontend
cd frontend
npm test
```

### Tests d'Intégration
```bash
# Tests E2E
cd frontend
npm run e2e
```

## Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## Support

Pour toute question ou problème :
- Ouvrir une issue sur GitHub
- Contacter l'équipe de développement

## Auteurs

- **Ala Hammami** - *Développement principal* - [alahammami2](https://github.com/alahammami2)

## Remerciements

- Équipe pédagogique du PFE
- Communauté open source
- Tous les contributeurs du projet
