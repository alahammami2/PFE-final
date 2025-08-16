-- Création automatique de la table users pour PostgreSQL
-- Ce script est exécuté automatiquement au démarrage de l'application

-- Créer la table users si elle n'existe pas
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('ADMIN', 'COACH', 'JOUEUR', 'RESPONSABLE_FINANCIER', 'STAFF_MEDICAL', 'INVITE')),
    actif BOOLEAN DEFAULT TRUE,
    telephone VARCHAR(20),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP
);

-- Créer un index sur l'email pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Créer un index sur le rôle
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Créer un index sur le statut actif
CREATE INDEX IF NOT EXISTS idx_users_actif ON users(actif);

-- Index sur le téléphone (recherche éventuelle)
CREATE INDEX IF NOT EXISTS idx_users_telephone ON users(telephone);
