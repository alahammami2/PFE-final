# Performance Files API - Documentation

## Vue d'ensemble
Le microservice performance-service inclut maintenant la gestion des fichiers associés aux performances des joueurs. Cette fonctionnalité permet de stocker, organiser et récupérer différents types de fichiers (vidéos, analyses, statistiques, photos) liés aux performances.

## Base URL
- **Base URL**: `http://localhost:8083/api/performance/files`
- **Format**: JSON
- **Auth**: Aucune en développement

## Modèle de données

### Entité PerformanceFile
```json
{
  "id": 1,
  "originalName": "match_2024_07_01.mp4",
  "fileType": "mp4",
  "fileSize": 52428800,
  "filePath": "/uploads/videos/match_2024_07_01.mp4",
  "uploadDate": "2024-07-01T10:30:00",
  "performanceId": 1,
  "performanceInfo": "Mohamed Ben Ahmed - 2024-07-01"
}
```

### Types de fichiers supportés
- **Documents**: `pdf`, `doc`, `docx`, `xls`, `xlsx`
- **Images**: `jpg`, `jpeg`, `png`
- **Vidéos**: `mp4`, `avi`, `mov`

### Limites
- **Taille maximale**: 100 MB par fichier
- **Nom de fichier**: 255 caractères max
- **Chemin de fichier**: 500 caractères max
- **Type de fichier**: 10 caractères max

## Endpoints API

### 1. CRUD Operations

#### Créer un fichier
```http
POST /api/performance/files
Content-Type: application/json

{
  "originalName": "analyse_technique_mohamed.pdf",
  "fileType": "pdf",
  "fileSize": 2097152,
  "filePath": "/uploads/analyses/analyse_technique_mohamed.pdf",
  "performanceId": 1
}
```

**Réponse**: `201 Created`
```json
{
  "id": 1,
  "originalName": "analyse_technique_mohamed.pdf",
  "fileType": "pdf",
  "fileSize": 2097152,
  "filePath": "/uploads/analyses/analyse_technique_mohamed.pdf",
  "uploadDate": "2024-07-01T10:30:00",
  "performanceId": 1,
  "performanceInfo": "Mohamed Ben Ahmed - 2024-07-01"
}
```

#### Lister tous les fichiers
```http
GET /api/performance/files
```

**Réponse**: `200 OK`
```json
[
  {
    "id": 1,
    "originalName": "match_2024_07_01.mp4",
    "fileType": "mp4",
    "fileSize": 52428800,
    "filePath": "/uploads/videos/match_2024_07_01.mp4",
    "uploadDate": "2024-07-01T10:30:00",
    "performanceId": 1,
    "performanceInfo": "Mohamed Ben Ahmed - 2024-07-01"
  }
]
```

#### Obtenir un fichier par ID
```http
GET /api/performance/files/{id}
```

#### Mettre à jour un fichier
```http
PUT /api/performance/files/{id}
Content-Type: application/json

{
  "originalName": "analyse_technique_mohamed_v2.pdf",
  "fileType": "pdf",
  "fileSize": 3145728,
  "filePath": "/uploads/analyses/analyse_technique_mohamed_v2.pdf",
  "performanceId": 1
}
```

#### Supprimer un fichier
```http
DELETE /api/performance/files/{id}
```

**Réponse**: `204 No Content`

### 2. Recherche et Filtrage

#### Fichiers par performance
```http
GET /api/performance/files/performance/{performanceId}
```

#### Fichiers par type
```http
GET /api/performance/files/type/{fileType}
```

#### Fichiers par taille minimale
```http
GET /api/performance/files/min-size?minSize=1048576
```

#### Fichiers par période
```http
GET /api/performance/files/period?dateDebut=2024-07-01T00:00:00&dateFin=2024-07-31T23:59:59
```

#### Recherche par nom
```http
GET /api/performance/files/search?searchTerm=match
```

### 3. Statistiques et Analyses

#### Fichiers récents
```http
GET /api/performance/files/recent
```

#### Fichiers les plus volumineux
```http
GET /api/performance/files/largest
```

#### Statistiques générales
```http
GET /api/performance/files/statistics
```

**Réponse**: `200 OK`
```json
{
  "totalFiles": 25,
  "totalSize": 1073741824,
  "statsByType": [
    ["mp4", 10, 536870912],
    ["pdf", 8, 16777216],
    ["jpg", 5, 10485760],
    ["xlsx", 2, 2097152]
  ]
}
```

## Exemples d'utilisation

### 1. Upload d'une vidéo de match
```bash
curl -X POST http://localhost:8083/api/performance/files \
  -H "Content-Type: application/json" \
  -d '{
    "originalName": "match_2024_08_01.mp4",
    "fileType": "mp4",
    "fileSize": 104857600,
    "filePath": "/uploads/videos/match_2024_08_01.mp4",
    "performanceId": 15
  }'
```

### 2. Recherche de fichiers PDF
```bash
curl "http://localhost:8083/api/performance/files/type/pdf"
```

### 3. Fichiers d'une performance spécifique
```bash
curl "http://localhost:8083/api/performance/files/performance/1"
```

### 4. Statistiques des fichiers
```bash
curl "http://localhost:8083/api/performance/files/statistics"
```

## Gestion des erreurs

### Erreurs de validation
```json
{
  "error": "Type de fichier non autorisé: txt"
}
```

### Erreurs de taille
```json
{
  "error": "La taille du fichier ne peut pas dépasser 100 MB"
}
```

### Fichier non trouvé
```json
{
  "error": "Fichier non trouvé avec l'ID: 999"
}
```

## Cas d'usage

### 1. Gestion des vidéos de match
- Upload de vidéos complètes de matchs
- Association avec les performances des joueurs
- Recherche par date et performance

### 2. Analyses techniques
- Stockage de rapports d'analyse PDF
- Association avec les performances individuelles
- Historique des analyses par joueur

### 3. Statistiques et rapports
- Fichiers Excel de statistiques
- Rapports de performance
- Données pour analyse externe

### 4. Photos et médias
- Photos de match et d'entraînement
- Captures d'écran d'analyse
- Documentation visuelle

## Intégration avec les performances

Chaque fichier peut être associé à une performance spécifique via le champ `performanceId`. Cette relation permet de :
- Organiser les fichiers par performance
- Filtrer les fichiers par joueur et date
- Maintenir la cohérence des données

## Sécurité et validation

- Validation des types de fichiers autorisés
- Contrôle de la taille maximale
- Validation des chemins de fichiers
- Gestion des erreurs de validation

## Performance et optimisation

- Index sur les champs de recherche fréquents
- Requêtes optimisées pour les listes
- Pagination pour les grandes collections
- Cache des statistiques fréquemment demandées
