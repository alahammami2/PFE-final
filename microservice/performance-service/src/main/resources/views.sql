-- =================================================================
-- VUES UTILES POUR LES STATISTIQUES
-- Ce script est exécuté après la création des tables par Hibernate
-- =================================================================

-- Supprimer les vues existantes pour éviter les erreurs de modification de colonnes
DROP VIEW IF EXISTS v_player_average_stats CASCADE;
DROP VIEW IF EXISTS v_current_absences CASCADE;

-- Vue des statistiques moyennes par joueur
CREATE VIEW v_player_average_stats AS
SELECT 
    p.id as player_id,
    p.nom,
    p.prenom,
    p.position,
    COUNT(perf.id) as total_performances,
    -- Attaques retirées
    ROUND(AVG(perf.aces), 2) as moy_aces,
    -- Blocs retirés
    ROUND(AVG(perf.note_globale), 2) as moy_note_globale,
    NULL::numeric as pourcentage_attaque
FROM players p
LEFT JOIN performances perf ON p.id = perf.player_id
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
-- COMMENTAIRES SUR LES VUES
-- =================================================================

COMMENT ON VIEW v_player_average_stats IS 'Vue des statistiques moyennes par joueur actif';
COMMENT ON VIEW v_current_absences IS 'Vue des absences actuellement en cours';
