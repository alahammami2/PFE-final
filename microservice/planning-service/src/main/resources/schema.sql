-- Script de création de la table events pour le planning-service

-- Création de la table events
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ENTRAINEMENT', 'MATCH', 'REUNION', 'COMPETITION', 'EVENEMENT', 'AUTRE')),
    lieu VARCHAR(200),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP
);

-- Création des index
CREATE INDEX IF NOT EXISTS idx_events_actif ON events(actif);
CREATE INDEX IF NOT EXISTS idx_events_type ON events(type);
CREATE INDEX IF NOT EXISTS idx_events_date_debut ON events(date_debut);
CREATE INDEX IF NOT EXISTS idx_events_date_fin ON events(date_fin);

-- Insertion de données de test
INSERT INTO events (titre, description, date_debut, date_fin, type, lieu, actif, date_creation) 
SELECT 'Entraînement technique', 'Entraînement focalisé sur les techniques de base', 
       CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '2 hours',
       'ENTRAINEMENT', 'Gymnase principal', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE titre = 'Entraînement technique');

INSERT INTO events (titre, description, date_debut, date_fin, type, lieu, actif, date_creation) 
SELECT 'Match amical', 'Match amical contre l''équipe locale', 
       CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '3 days' + INTERVAL '3 hours',
       'MATCH', 'Salle de sport municipale', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM events WHERE titre = 'Match amical');

-- Vérification des données insérées
SELECT id, titre, type, date_debut, lieu FROM events WHERE actif = true; 