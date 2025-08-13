-- Script de création des tables pour medical-service
-- À exécuter dans pgAdmin 4 sur la base de données postgres

-- Table des dossiers médicaux
CREATE TABLE IF NOT EXISTS health_records (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL,
    player_name VARCHAR(255) NOT NULL,
    blessure_type VARCHAR(50),
    blessure_date DATE,
    traitement TEXT,
    status VARCHAR(50) NOT NULL,
    last_medical_checkup DATE,
    next_checkup_due DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des rendez-vous médicaux
CREATE TABLE IF NOT EXISTS medical_rendezvous (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL,
    player_name VARCHAR(255) NOT NULL,
    rendezvous_datetime TIMESTAMP NOT NULL,
    kine_name VARCHAR(255),
    lieu VARCHAR(255),
    priority VARCHAR(50),
    status VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_health_records_player_id ON health_records(player_id);
CREATE INDEX IF NOT EXISTS idx_health_records_status ON health_records(status);
CREATE INDEX IF NOT EXISTS idx_rendezvous_player_id ON medical_rendezvous(player_id);
CREATE INDEX IF NOT EXISTS idx_rendezvous_status ON medical_rendezvous(status);
CREATE INDEX IF NOT EXISTS idx_rendezvous_datetime ON medical_rendezvous(rendezvous_datetime);
