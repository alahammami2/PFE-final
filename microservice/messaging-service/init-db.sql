-- Script d'initialisation de la base de données messaging_db
-- Ce script est exécuté automatiquement lors du démarrage du conteneur PostgreSQL

-- Créer la base de données si elle n'existe pas déjà
-- (PostgreSQL crée automatiquement la base via POSTGRES_DB)

-- Accorder tous les privilèges à l'utilisateur messaging_user
GRANT ALL PRIVILEGES ON DATABASE messaging_db TO messaging_user;
GRANT ALL ON SCHEMA public TO messaging_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO messaging_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO messaging_user;

-- Créer des extensions utiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Message de confirmation
SELECT 'Base de données messaging_db initialisée avec succès' as status;
