# DOCUMENTATION FINANCE-SERVICE

## Vue d'ensemble
Le microservice `finance-service` gère tous les aspects financiers de l'équipe de volleyball, incluant les budgets, catégories de budget, dépenses, sponsors et salaires.

## Architecture Technique

### Technologies utilisées
- **Framework**: Spring Boot 3.2.0
- **Langage**: Java 17
- **Base de données**: PostgreSQL 15
- **ORM**: Hibernate/JPA
- **Conteneurisation**: Docker & Docker Compose
- **Port**: 8087 (application), 5437 (PostgreSQL)

### Structure du projet
```
finance-service/
├── src/main/java/com/volleyball/finance/
│   ├── FinanceServiceApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── model/
│   │   ├── Budget.java
│   │   ├── CategorieBudget.java
│   │   └── Depense.java
│   ├── repository/
│   │   ├── BudgetRepository.java
│   │   ├── CategorieBudgetRepository.java
│   │   └── DepenseRepository.java
│   ├── service/
│   │   ├── BudgetService.java
│   │   ├── CategorieBudgetService.java
│   │   └── DepenseService.java
│   └── controller/
│       ├── BudgetController.java
│       ├── CategorieBudgetController.java
│       └── DepenseController.java
├── src/main/resources/
│   ├── application.yml
│   └── data.sql
├── Dockerfile
├── docker-compose.yml
├── init-db.sql
└── start-docker.bat
```

## Modèle de données

### Entité Budget
- **id**: Clé primaire auto-générée
- **idFinance**: Identifiant unique du budget (String)
- **montant**: Montant total du budget (Double)
- **categories**: Liste des catégories de budget associées

### Entité CategorieBudget
- **id**: Clé primaire auto-générée
- **idCategorie**: Identifiant de la catégorie (Integer)
- **nomCategorie**: Nom de la catégorie (String)
- **montantAlloue**: Montant alloué à cette catégorie (Double)
- **budget**: Budget parent (relation ManyToOne)
- **depenses**: Liste des dépenses de cette catégorie

### Entité Depense
- **id**: Clé primaire auto-générée
- **idDepense**: Identifiant de la dépense (Integer)
- **montant**: Montant de la dépense (Double)
- **date**: Date de la dépense (LocalDate)
- **description**: Description de la dépense (String)
- **categorieBudget**: Catégorie parent (relation ManyToOne)

## API Endpoints

### Base URL: `http://localhost:8087/api/finance`

## BUDGETS ENDPOINTS

### CRUD Operations
- **POST** `/budgets` - Créer un nouveau budget
- **GET** `/budgets` - Lister tous les budgets
- **GET** `/budgets/{id}` - Obtenir un budget par ID
- **GET** `/budgets/by-id-finance?idFinance={idFinance}` - Obtenir un budget par ID finance
- **PUT** `/budgets/{id}` - Mettre à jour un budget
- **DELETE** `/budgets/{id}` - Supprimer un budget

### Business Logic
- **GET** `/budgets/minimum-amount?montantMin={montant}` - Budgets avec montant minimum
- **GET** `/budgets/range?montantMin={min}&montantMax={max}` - Budgets dans une fourchette
- **GET** `/budgets/total` - Montant total de tous les budgets
- **GET** `/budgets/count` - Nombre total de budgets
- **GET** `/budgets/{id}/with-categories` - Budget avec ses catégories
- **GET** `/budgets/by-category?nomCategorie={nom}` - Budgets par nom de catégorie
- **PUT** `/budgets/{id}/ajuster?nouveauMontant={montant}` - Ajuster le montant d'un budget
- **POST** `/budgets/{id}/consulter` - Consulter un budget

## CATÉGORIES BUDGET ENDPOINTS

### CRUD Operations
- **POST** `/categories-budget` - Créer une nouvelle catégorie
- **GET** `/categories-budget` - Lister toutes les catégories
- **GET** `/categories-budget/{id}` - Obtenir une catégorie par ID
- **GET** `/categories-budget/by-id-categorie?idCategorie={id}` - Obtenir par ID catégorie
- **PUT** `/categories-budget/{id}` - Mettre à jour une catégorie
- **DELETE** `/categories-budget/{id}` - Supprimer une catégorie

### Business Logic
- **GET** `/categories-budget/search?nomCategorie={nom}` - Rechercher par nom
- **GET** `/categories-budget/budget/{budgetId}` - Catégories d'un budget
- **GET** `/categories-budget/budget/{budgetId}/ordered` - Catégories triées par montant
- **GET** `/categories-budget/minimum-amount?montant={montant}` - Catégories avec montant minimum
- **GET** `/categories-budget/range?montantMin={min}&montantMax={max}` - Catégories dans une fourchette
- **GET** `/categories-budget/budget/{budgetId}/total` - Montant total alloué par budget
- **GET** `/categories-budget/{id}/with-depenses` - Catégorie avec ses dépenses
- **GET** `/categories-budget/with-remaining-budget` - Catégories avec budget restant
- **GET** `/categories-budget/budget/{budgetId}/count` - Nombre de catégories par budget
- **PUT** `/categories-budget/{id}/ajuster?nouveauMontant={montant}` - Ajuster le montant alloué

## DÉPENSES ENDPOINTS

### CRUD Operations
- **POST** `/depenses` - Créer une nouvelle dépense
- **GET** `/depenses` - Lister toutes les dépenses
- **GET** `/depenses/{id}` - Obtenir une dépense par ID
- **GET** `/depenses/by-id-depense?idDepense={id}` - Obtenir par ID dépense
- **PUT** `/depenses/{id}` - Mettre à jour une dépense
- **DELETE** `/depenses/{id}` - Supprimer une dépense

### Business Logic
- **GET** `/depenses/categorie/{categorieBudgetId}` - Dépenses d'une catégorie
- **GET** `/depenses/categorie/{categorieBudgetId}/ordered` - Dépenses triées par date
- **GET** `/depenses/periode?dateDebut={debut}&dateFin={fin}` - Dépenses par période
- **GET** `/depenses/minimum-amount?montant={montant}` - Dépenses avec montant minimum
- **GET** `/depenses/range?montantMin={min}&montantMax={max}` - Dépenses dans une fourchette
- **GET** `/depenses/search?description={description}` - Rechercher par description
- **GET** `/depenses/annee/{annee}` - Dépenses par année
- **GET** `/depenses/mois/{mois}/annee/{annee}` - Dépenses par mois et année
- **GET** `/depenses/categorie/{categorieBudgetId}/total` - Montant total par catégorie
- **GET** `/depenses/periode/total?dateDebut={debut}&dateFin={fin}` - Montant total par période
- **GET** `/depenses/categorie/{categorieBudgetId}/count` - Nombre de dépenses par catégorie
- **GET** `/depenses/recent?dateDebut={date}` - Dépenses récentes triées par montant
- **PUT** `/depenses/{id}/modifier?nouveauMontant={montant}&nouvelleDescription={desc}` - Modifier une dépense
- **POST** `/depenses/{id}/consulter` - Consulter une dépense

## Exemples de requêtes

### Créer un budget
```json
POST /api/finance/budgets
{
    "idFinance": "BUD-2024-004",
    "montant": 60000.00
}
```

### Créer une catégorie de budget
```json
POST /api/finance/categories-budget
{
    "idCategorie": 12,
    "nomCategorie": "Nutrition Sportive",
    "montantAlloue": 8000.00,
    "budget": {
        "id": 1
    }
}
```

### Créer une dépense
```json
POST /api/finance/depenses
{
    "idDepense": 32,
    "montant": 1500.00,
    "date": "2024-03-15",
    "description": "Compléments alimentaires pour l'équipe",
    "categorieBudget": {
        "id": 12
    }
}
```

## Configuration et déploiement

### Configuration de base de données
- **URL**: `jdbc:postgresql://localhost:5437/finance_db`
- **Utilisateur**: `finance_user`
- **Mot de passe**: `root`
- **Port PostgreSQL**: 5437

### Déploiement avec Docker
```bash
# Démarrer le service
./start-docker.bat

# Ou manuellement
mvn clean package -DskipTests
docker-compose up --build -d
```

### Vérification du déploiement
```bash
# Vérifier les conteneurs
docker-compose ps

# Tester l'API
curl http://localhost:8087/api/finance/budgets

# Voir les logs
docker-compose logs -f finance-service
```

## Tests avec PowerShell

### Test des budgets
```powershell
# Lister tous les budgets
Invoke-WebRequest -Uri "http://localhost:8087/api/finance/budgets" -Method GET

# Créer un budget
$budget = @{
    idFinance = "BUD-2024-TEST"
    montant = 25000.00
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8087/api/finance/budgets" -Method POST -Body $budget -ContentType "application/json"
```

### Test des catégories
```powershell
# Lister toutes les catégories
Invoke-WebRequest -Uri "http://localhost:8087/api/finance/categories-budget" -Method GET

# Obtenir les catégories d'un budget
Invoke-WebRequest -Uri "http://localhost:8087/api/finance/categories-budget/budget/1" -Method GET
```

### Test des dépenses
```powershell
# Lister toutes les dépenses
Invoke-WebRequest -Uri "http://localhost:8087/api/finance/depenses" -Method GET

# Obtenir les dépenses d'une catégorie
Invoke-WebRequest -Uri "http://localhost:8087/api/finance/depenses/categorie/1" -Method GET

# Obtenir les dépenses par période
Invoke-WebRequest -Uri "http://localhost:8087/api/finance/depenses/periode?dateDebut=2024-01-01&dateFin=2024-02-29" -Method GET
```

## Données de test incluses

Le service inclut des données de test avec :
- **3 budgets** avec différents montants
- **11 catégories** couvrant tous les aspects financiers
- **31 dépenses** réparties sur différentes catégories et périodes

### Catégories disponibles
1. Équipements Sportifs
2. Salaires Entraîneurs
3. Déplacements
4. Sponsors
5. Formation
6. Matériel Médical
7. Marketing
8. Infrastructure
9. Événements
10. Assurances
11. Frais Administratifs

## Dépannage

### Problèmes courants
1. **Port 5437 occupé**: Changer le port dans docker-compose.yml
2. **Erreur de connexion DB**: Vérifier que PostgreSQL est démarré
3. **Erreur 404**: Vérifier l'URL de base `/api/finance`

### Logs utiles
```bash
# Logs du service
docker-compose logs finance-service

# Logs de la base de données
docker-compose logs finance-postgres

# Logs en temps réel
docker-compose logs -f
```

## Intégration avec autres microservices

Ce service peut être intégré avec :
- **performance-service**: Pour lier les performances aux budgets de formation
- **planning-service**: Pour associer les événements aux dépenses
- **admin-request-service**: Pour les demandes budgétaires

## Sécurité

- Authentification désactivée pour le développement
- CORS activé pour tous les domaines
- Validation des données d'entrée avec Bean Validation
- Gestion des erreurs avec ResponseEntity

---

**Service prêt pour déploiement et tests!**
**Base URL**: http://localhost:8087/api/finance
