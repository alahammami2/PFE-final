# Test de Persistance des Données - Auth Service

## Problème Résolu ✅

Le problème était dans le `DatabaseInitializer.java` qui supprimait **TOUS** les utilisateurs à chaque redémarrage avec :
```java
jdbcTemplate.execute("DELETE FROM users");
```

## Solution Implémentée ✅

1. **Suppression de la ligne DELETE** - Plus de suppression automatique
2. **Utilisation de ON CONFLICT DO NOTHING** - Les utilisateurs de test ne sont insérés que s'ils n'existent pas
3. **Configuration SQL init désactivée** - `mode: never` pour éviter les conflits
4. **Hibernate ddl-auto: update** - Gestion automatique des schémas

## Comment Tester

### 1. Démarrer le service
```bash
cd microservice/auth-service
mvn spring-boot:run
```

### 2. Vérifier les utilisateurs de test
- Admin: `admin@cok.tn` / `password123`
- Coach: `coach@cok.tn` / `password123`
- Joueur: `joueur@cok.tn` / `password123`

### 3. Créer un nouvel utilisateur via l'API
```bash
curl -X POST http://localhost:8081/api/auth/create-user \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Nouveau",
    "prenom": "Utilisateur",
    "email": "nouveau@test.com",
    "motDePasse": "password123",
    "role": "JOUEUR"
  }'
```

### 4. Redémarrer le service
```bash
# Arrêter avec Ctrl+C
# Puis redémarrer
mvn spring-boot:run
```

### 5. Vérifier que le nouvel utilisateur existe toujours
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nouveau@test.com",
    "motDePasse": "password123"
  }'
```

## Résultat Attendu ✅

- Les utilisateurs de test sont toujours présents
- Le nouvel utilisateur créé persiste après redémarrage
- Aucune perte de données lors des redémarrages

## Logs à Surveiller

```
✅ Utilisateur admin de test créé (ou existe déjà)
✅ Utilisateur coach de test créé (ou existe déjà)  
✅ Utilisateur joueur de test créé (ou existe déjà)
📊 Total: X utilisateur(s) dans la base de données
🎉 Initialisation des données de test terminée !
```
