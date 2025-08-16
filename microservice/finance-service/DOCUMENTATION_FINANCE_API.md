# Finance Service — API & Postman Tests

## Base
- **Base URL**: `http://localhost:8087/api/finance`
- **Format**: JSON
- **Auth**: aucune en dev

## Modèles (actuels)
- **Budget**: `{ id, montant }`
- **Recette** (ex-"catégorie budget", routes: `/categories-budget`): `{ id, date, description, montant, categorie }`
- **Depense**: `{ id, montant, date, description, statut, categorie }`

## Budgets
- POST `/budgets` — créer
- GET `/budgets` — lister
- GET `/budgets/{id}` — détail
- PUT `/budgets/{id}` — mettre à jour
- DELETE `/budgets/{id}` — supprimer
- GET `/budgets/minimum-amount?montantMin={min}`
- GET `/budgets/range?montantMin={min}&montantMax={max}`
- GET `/budgets/total`
- GET `/budgets/count`

Exemples Postman
- Créer budget
```http
POST {{baseUrl}}/budgets
Content-Type: application/json

{
  "montant": 60000.00
}
```
- Lister budgets
```http
GET {{baseUrl}}/budgets
```

## Recettes (routes `/categories-budget`)
- POST `/categories-budget` — créer une recette
- GET `/categories-budget` — lister
- GET `/categories-budget/{id}` — détail
- PUT `/categories-budget/{id}` — mettre à jour
- DELETE `/categories-budget/{id}` — supprimer
- GET `/categories-budget/search?nomCategorie={term}` — recherche (libellé legacy)

Payload création/maj
```json
{
  "date": "2025-01-15",
  "description": "Sponsor local",
  "montant": 12000.0,
  "categorie": "SPONSOR"
}
```

Requêtes rapides
```http
POST {{baseUrl}}/categories-budget
Content-Type: application/json

{
  "date": "2025-02-01",
  "description": "Billetterie match",
  "montant": 5400,
  "categorie": "BILLETTERIE"
}
```
```http
GET {{baseUrl}}/categories-budget
```

## Dépenses
- POST `/depenses` — créer
- GET `/depenses` — lister
- GET `/depenses/{id}` — détail
- PUT `/depenses/{id}` — mettre à jour
- DELETE `/depenses/{id}` — supprimer
- GET `/depenses/periode?dateDebut=YYYY-MM-DD&dateFin=YYYY-MM-DD`
- GET `/depenses/minimum-amount?montant={min}`
- GET `/depenses/range?montantMin={min}&montantMax={max}`
- GET `/depenses/search?description={term}`
- GET `/depenses/annee/{annee}`
- GET `/depenses/mois/{mois}/annee/{annee}`
- GET `/depenses/recent?dateDebut=YYYY-MM-DD`
- PUT `/depenses/{id}/modifier?nouveauMontant={m}&nouvelleDescription={txt}`

Payload création/maj
```json
{
  "montant": 1500.00,
  "date": "2025-03-15",
  "description": "Transport match retour",
  "statut": "PAYEE",
  "categorie": "DEPLACEMENT"
}
```

Requêtes rapides
```http
POST {{baseUrl}}/depenses
Content-Type: application/json

{
  "montant": 320.0,
  "date": "2025-02-10",
  "description": "Ravitaillement eau",
  "statut": "EN_ATTENTE",
  "categorie": "LOGISTIQUE"
}
```
```http
GET {{baseUrl}}/depenses/periode?dateDebut=2025-02-01&dateFin=2025-02-28
```

## Collection Postman (variables utiles)
- `baseUrl`: `http://localhost:8087/api/finance`
- Headers par défaut: `Content-Type: application/json`

## Notes
- Les routes Recettes utilisent encore le préfixe `/categories-budget`, mais le modèle attendu est celui d’une recette `{date, description, montant, categorie}`.
- Les dépenses n’utilisent plus `idDepense` ni le lien `categorieBudget`.
