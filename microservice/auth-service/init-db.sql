-- Script d'initialisation de la base de données PostgreSQL pour auth-service

-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('ADMIN', 'COACH', 'JOUEUR', 'RESPONSABLE_FINANCIER', 'STAFF_MEDICAL', 'INVITE')),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP
);

-- Création des index
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_actif ON users(actif);

-- Insertion de données de test (seulement si la table est vide)
INSERT INTO users (nom, prenom, email, mot_de_passe, role, actif, date_creation) 
SELECT 'Admin', 'System', 'admin@cok.tn', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@cok.tn');

-- Vérification des données insérées
SELECT id, nom, prenom, email, role, actif FROM users;
