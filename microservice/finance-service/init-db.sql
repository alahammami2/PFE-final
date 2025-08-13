-- Script d'initialisation de la base de données pour finance-service

-- Création de l'utilisateur et de la base de données (si nécessaire)
-- Ces commandes sont généralement exécutées par le conteneur PostgreSQL

-- Accorder tous les privilèges à l'utilisateur finance_user
GRANT ALL PRIVILEGES ON DATABASE finance_db TO finance_user;
GRANT ALL ON SCHEMA public TO finance_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO finance_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO finance_user;

-- Définir l'utilisateur par défaut pour les futures tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO finance_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO finance_user;
