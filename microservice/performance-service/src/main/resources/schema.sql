-- =================================================================
-- SCHÉMA DE BASE DE DONNÉES POUR PERFORMANCE-SERVICE
-- Service de gestion des performances et absences des joueurs
-- =================================================================

-- Suppression des tables existantes (ordre inverse des dépendances)
DROP TABLE IF EXISTS performances CASCADE;
DROP TABLE IF EXISTS absences CASCADE;
DROP TABLE IF EXISTS players CASCADE;

-- =================================================================
-- TABLE PLAYERS (Joueurs)
-- =================================================================
CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    telephone VARCHAR(20),
    date_naissance DATE NOT NULL,
    position VARCHAR(50) NOT NULL CHECK (position IN ('PASSEUR', 'ATTAQUANT', 'CENTRAL', 'LIBERO', 'POINTU', 'RECEPTEUR_ATTAQUANT')),
    numero_maillot INTEGER UNIQUE CHECK (numero_maillot > 0 AND numero_maillot <= 99),
    taille_cm INTEGER CHECK (taille_cm > 0),
    poids_kg DECIMAL(5,2) CHECK (poids_kg > 0),
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'BLESSE', 'SUSPENDU', 'INACTIF', 'TRANSFERE')),
    date_debut_equipe DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index pour optimiser les recherches
CREATE INDEX idx_players_nom ON players(nom);
CREATE INDEX idx_players_prenom ON players(prenom);
CREATE INDEX idx_players_email ON players(email);
CREATE INDEX idx_players_position ON players(position);
CREATE INDEX idx_players_statut ON players(statut);
CREATE INDEX idx_players_actif ON players(actif);
CREATE INDEX idx_players_numero_maillot ON players(numero_maillot);

-- =================================================================
-- TABLE ABSENCES (Absences des joueurs)
-- =================================================================
CREATE TABLE absences (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    date_debut DATE NOT NULL,
    date_fin DATE,
    type_absence VARCHAR(20) NOT NULL CHECK (type_absence IN ('MALADIE', 'BLESSURE', 'PERSONNEL', 'PROFESSIONNEL', 'VACANCES', 'AUTRE')),
    raison VARCHAR(500),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE', 'APPROUVEE', 'REFUSEE', 'ANNULEE')),
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    commentaires VARCHAR(1000),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Contrainte : date_fin >= date_debut si date_fin est définie
    CONSTRAINT chk_absences_dates CHECK (date_fin IS NULL OR date_fin >= date_debut)
);

-- Index pour optimiser les recherches
CREATE INDEX idx_absences_player_id ON absences(player_id);
CREATE INDEX idx_absences_date_debut ON absences(date_debut);
CREATE INDEX idx_absences_date_fin ON absences(date_fin);
CREATE INDEX idx_absences_type ON absences(type_absence);
CREATE INDEX idx_absences_statut ON absences(statut);
CREATE INDEX idx_absences_justifiee ON absences(justifiee);
CREATE INDEX idx_absences_periode ON absences(date_debut, date_fin);

-- =================================================================
-- TABLE PERFORMANCES (Performances des joueurs)
-- =================================================================
CREATE TABLE performances (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    date_performance DATE NOT NULL,
    type_performance VARCHAR(20) NOT NULL CHECK (type_performance IN ('MATCH', 'ENTRAINEMENT', 'COMPETITION', 'TOURNOI', 'AMICAL')),
    
    -- Statistiques offensives
    attaques_totales INTEGER NOT NULL DEFAULT 0 CHECK (attaques_totales >= 0),
    attaques_reussies INTEGER NOT NULL DEFAULT 0 CHECK (attaques_reussies >= 0),
    aces INTEGER NOT NULL DEFAULT 0 CHECK (aces >= 0),
    
    -- Statistiques défensives
    blocs INTEGER NOT NULL DEFAULT 0 CHECK (blocs >= 0),
    receptions_totales INTEGER NOT NULL DEFAULT 0 CHECK (receptions_totales >= 0),
    receptions_reussies INTEGER NOT NULL DEFAULT 0 CHECK (receptions_reussies >= 0),
    defenses INTEGER NOT NULL DEFAULT 0 CHECK (defenses >= 0),
    
    -- Statistiques de service
    services_totaux INTEGER NOT NULL DEFAULT 0 CHECK (services_totaux >= 0),
    services_reussis INTEGER NOT NULL DEFAULT 0 CHECK (services_reussis >= 0),
    
    -- Erreurs
    erreurs_attaque INTEGER NOT NULL DEFAULT 0 CHECK (erreurs_attaque >= 0),
    erreurs_service INTEGER NOT NULL DEFAULT 0 CHECK (erreurs_service >= 0),
    erreurs_reception INTEGER NOT NULL DEFAULT 0 CHECK (erreurs_reception >= 0),
    
    -- Temps et évaluation
    temps_jeu_minutes INTEGER NOT NULL DEFAULT 0 CHECK (temps_jeu_minutes >= 0),
    note_globale DECIMAL(3,1) CHECK (note_globale >= 0 AND note_globale <= 10),
    commentaires VARCHAR(1000),
    
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes de cohérence des statistiques
    CONSTRAINT chk_attaques_coherence CHECK (attaques_reussies <= attaques_totales),
    CONSTRAINT chk_receptions_coherence CHECK (receptions_reussies <= receptions_totales),
    CONSTRAINT chk_services_coherence CHECK (services_reussis <= services_totaux),
    
    -- Contrainte d'unicité : un joueur ne peut avoir qu'une performance par date et type
    UNIQUE(player_id, date_performance, type_performance)
);

-- Index pour optimiser les recherches
CREATE INDEX idx_performances_player_id ON performances(player_id);
CREATE INDEX idx_performances_date ON performances(date_performance);
CREATE INDEX idx_performances_type ON performances(type_performance);
CREATE INDEX idx_performances_note ON performances(note_globale);
CREATE INDEX idx_performances_player_date ON performances(player_id, date_performance);
CREATE INDEX idx_performances_player_type ON performances(player_id, type_performance);

-- =================================================================
-- VUES UTILES POUR LES STATISTIQUES
-- =================================================================

-- Vue des statistiques moyennes par joueur
CREATE VIEW v_player_average_stats AS
SELECT 
    p.id as player_id,
    p.nom,
    p.prenom,
    p.position,
    COUNT(perf.id) as total_performances,
    ROUND(AVG(perf.attaques_totales), 2) as moy_attaques_totales,
    ROUND(AVG(perf.attaques_reussies), 2) as moy_attaques_reussies,
    ROUND(AVG(perf.aces), 2) as moy_aces,
    ROUND(AVG(perf.blocs), 2) as moy_blocs,
    ROUND(AVG(perf.note_globale), 2) as moy_note_globale,
    ROUND(AVG(CASE WHEN perf.attaques_totales > 0 THEN (perf.attaques_reussies::DECIMAL / perf.attaques_totales * 100) END), 2) as pourcentage_attaque
FROM players p
LEFT JOIN performances perf ON p.id = perf.player_id
WHERE p.actif = TRUE
GROUP BY p.id, p.nom, p.prenom, p.position;

-- Vue des absences en cours
CREATE VIEW v_current_absences AS
SELECT 
    a.*,
    p.nom,
    p.prenom,
    p.position,
    CASE 
        WHEN a.date_fin IS NULL THEN CURRENT_DATE - a.date_debut
        ELSE a.date_fin - a.date_debut
    END as duree_jours
FROM absences a
JOIN players p ON a.player_id = p.id
WHERE a.date_debut <= CURRENT_DATE 
  AND (a.date_fin IS NULL OR a.date_fin >= CURRENT_DATE)
  AND a.statut IN ('EN_ATTENTE', 'APPROUVEE');

-- =================================================================
-- TRIGGERS POUR MISE À JOUR AUTOMATIQUE DES TIMESTAMPS
-- =================================================================
-- Note: Triggers temporarily disabled for initial setup
-- TODO: Re-enable triggers after successful application startup

-- =================================================================
-- COMMENTAIRES SUR LES TABLES
-- =================================================================

COMMENT ON TABLE players IS 'Table des joueurs de volleyball avec leurs informations personnelles et sportives';
COMMENT ON TABLE absences IS 'Table des absences des joueurs avec gestion des statuts et justifications';
COMMENT ON TABLE performances IS 'Table des performances détaillées des joueurs lors des matchs et entraînements';

COMMENT ON VIEW v_player_average_stats IS 'Vue des statistiques moyennes par joueur actif';
COMMENT ON VIEW v_current_absences IS 'Vue des absences actuellement en cours';

-- =================================================================
-- FIN DU SCHÉMA
-- =================================================================
