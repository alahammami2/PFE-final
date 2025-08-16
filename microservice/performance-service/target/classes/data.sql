-- =================================================================
-- DONNÉES DE TEST POUR PERFORMANCE-SERVICE
-- Insertion de données d'exemple pour tester le service
-- =================================================================

-- =================================================================
-- INSERTION DES JOUEURS DE TEST
-- =================================================================

INSERT INTO players (nom, prenom, email, telephone, date_naissance, position, numero_maillot, taille_cm, poids_kg, statut, date_debut_equipe) VALUES
-- Équipe principale
('Ben Ahmed', 'Mohamed', 'mohamed.benahmed@volleyball.tn', '+216 20 123 456', '1995-03-15', 'PASSEUR', 1, 180, 75.5, 'ACTIF', '2023-09-01'),
('Trabelsi', 'Youssef', 'youssef.trabelsi@volleyball.tn', '+216 22 234 567', '1997-07-22', 'ATTAQUANT', 7, 190, 85.0, 'ACTIF', '2023-09-01'),
('Khelifi', 'Amine', 'amine.khelifi@volleyball.tn', '+216 25 345 678', '1996-11-08', 'CENTRAL', 12, 195, 88.5, 'ACTIF', '2023-09-01'),
('Sassi', 'Mehdi', 'mehdi.sassi@volleyball.tn', '+216 27 456 789', '1998-01-30', 'LIBERO', 5, 175, 70.0, 'ACTIF', '2023-09-01'),
('Gharbi', 'Karim', 'karim.gharbi@volleyball.tn', '+216 29 567 890', '1994-05-12', 'POINTU', 9, 188, 82.0, 'ACTIF', '2023-09-01'),
('Bouazizi', 'Sami', 'sami.bouazizi@volleyball.tn', '+216 21 678 901', '1999-09-03', 'RECEPTEUR_ATTAQUANT', 14, 185, 78.5, 'ACTIF', '2023-09-01'),

-- Joueurs de réserve
('Jemli', 'Fares', 'fares.jemli@volleyball.tn', '+216 23 789 012', '2000-02-18', 'PASSEUR', 2, 178, 73.0, 'ACTIF', '2024-01-15'),
('Mnif', 'Wassim', 'wassim.mnif@volleyball.tn', '+216 26 890 123', '1999-06-25', 'ATTAQUANT', 11, 187, 80.5, 'ACTIF', '2024-01-15'),
('Chedly', 'Omar', 'omar.chedly@volleyball.tn', '+216 28 901 234', '1997-12-10', 'CENTRAL', 15, 192, 86.0, 'ACTIF', '2024-01-15'),
('Hamdi', 'Nizar', 'nizar.hamdi@volleyball.tn', '+216 24 012 345', '1998-08-07', 'LIBERO', 6, 172, 68.5, 'ACTIF', '2024-01-15'),

-- Joueurs avec statuts différents
('Mejri', 'Aymen', 'aymen.mejri@volleyball.tn', '+216 30 123 456', '1996-04-20', 'ATTAQUANT', 8, 189, 83.5, 'BLESSE', '2023-09-01'),
('Zouari', 'Bilel', 'bilel.zouari@volleyball.tn', '+216 31 234 567', '1995-10-15', 'CENTRAL', 13, 193, 87.0, 'SUSPENDU', '2023-09-01')
ON CONFLICT (email) DO NOTHING;

-- =================================================================
-- INSERTION DES ABSENCES DE TEST
-- =================================================================

INSERT INTO absences (player_id, date_debut, date_fin, type_absence, raison, statut, justifiee, commentaires) VALUES
-- Absences récentes
(1, '2024-07-15', '2024-07-17', 'MALADIE', 'Grippe saisonnière', 'APPROUVEE', true, 'Certificat médical fourni'),
(2, '2024-07-20', '2024-07-22', 'PERSONNEL', 'Mariage dans la famille', 'APPROUVEE', true, 'Demande approuvée par l''entraîneur'),
(3, '2024-07-25', NULL, 'BLESSURE', 'Entorse de la cheville', 'APPROUVEE', true, 'En cours de rééducation'),
(4, '2024-07-28', '2024-07-30', 'PROFESSIONNEL', 'Formation professionnelle', 'APPROUVEE', true, 'Formation obligatoire'),

-- Absences en attente
(5, '2024-08-05', '2024-08-07', 'VACANCES', 'Congés d''été', 'EN_ATTENTE', false, 'Demande de congés'),
(6, '2024-08-10', '2024-08-12', 'PERSONNEL', 'Déménagement', 'EN_ATTENTE', false, 'Besoin d''aide pour déménager'),

-- Absences refusées
(7, '2024-07-30', '2024-08-01', 'AUTRE', 'Sortie entre amis', 'REFUSEE', false, 'Raison non valable pendant la période de compétition'),

-- Absences longues
(11, '2024-06-01', '2024-08-31', 'BLESSURE', 'Rupture des ligaments croisés', 'APPROUVEE', true, 'Blessure grave nécessitant une longue rééducation'),
(12, '2024-07-01', '2024-08-15', 'AUTRE', 'Suspension disciplinaire', 'APPROUVEE', false, 'Comportement inapproprié lors du match')
ON CONFLICT DO NOTHING;

-- =================================================================
-- INSERTION DES PERFORMANCES DE TEST
-- =================================================================

INSERT INTO performances (player_id, date_performance, type_performance, aces, receptions_totales, receptions_reussies, services_totaux, services_reussis, erreurs_service, erreurs_reception, note_globale, commentaires) VALUES

-- Performances de Mohamed (Passeur)
(1, '2024-07-01', 'MATCH', 2, 15, 13, 8, 20, 18, 2, 2, 90, 8.5, 'Excellente distribution, bon leadership'),
(1, '2024-07-03', 'ENTRAINEMENT', 1, 20, 17, 5, 25, 22, 3, 3, 120, 7.5, 'Bon entraînement, à améliorer la précision'),
(1, '2024-07-05', 'MATCH', 3, 18, 16, 10, 22, 20, 2, 2, 95, 9.0, 'Performance exceptionnelle'),

-- Performances de Youssef (Attaquant)
(2, '2024-07-01', 'MATCH', 1, 8, 6, 4, 15, 12, 3, 2, 85, 8.0, 'Bon pourcentage d''attaque'),
(2, '2024-07-03', 'ENTRAINEMENT', 0, 10, 8, 3, 18, 15, 3, 2, 110, 7.0, 'Beaucoup d''erreurs d''attaque'),
(2, '2024-07-05', 'MATCH', 2, 12, 10, 6, 16, 14, 2, 2, 90, 8.5, 'Très bonne performance offensive'),

-- Performances d'Amine (Central)
(3, '2024-07-01', 'MATCH', 0, 5, 4, 2, 10, 8, 2, 1, 80, 7.5, 'Bon travail en défense'),
(3, '2024-07-03', 'ENTRAINEMENT', 1, 8, 6, 3, 12, 10, 2, 2, 100, 7.0, 'Entraînement correct'),
(3, '2024-07-05', 'MATCH', 1, 6, 5, 4, 14, 12, 2, 1, 85, 8.5, 'Excellente performance défensive'),

-- Performances de Mehdi (Libéro)
(4, '2024-07-01', 'MATCH', 0, 35, 30, 15, 0, 0, 0, 5, 90, 8.5, 'Excellente défense'),
(4, '2024-07-03', 'ENTRAINEMENT', 0, 40, 32, 18, 0, 0, 0, 8, 120, 7.5, 'Bon entraînement défensif'),
(4, '2024-07-05', 'MATCH', 0, 38, 35, 20, 0, 0, 0, 3, 95, 9.0, 'Performance défensive exceptionnelle'),

-- Performances de Karim (Pointu)
(5, '2024-07-01', 'MATCH', 3, 12, 9, 5, 18, 15, 3, 3, 88, 7.5, 'Bon match général'),
(5, '2024-07-03', 'ENTRAINEMENT', 2, 15, 12, 4, 20, 17, 3, 3, 115, 7.5, 'Entraînement régulier'),

-- Performances de Sami (Récepteur-Attaquant)
(6, '2024-07-01', 'MATCH', 1, 25, 20, 8, 16, 13, 3, 5, 92, 7.0, 'Performance mitigée'),
(6, '2024-07-03', 'ENTRAINEMENT', 2, 28, 23, 10, 18, 15, 3, 5, 118, 7.5, 'Amélioration notable'),

-- Performances des joueurs de réserve
(7, '2024-07-10', 'ENTRAINEMENT', 1, 18, 15, 6, 20, 17, 3, 3, 90, 7.0, 'Bon potentiel'),
(8, '2024-07-10', 'ENTRAINEMENT', 0, 10, 8, 3, 15, 12, 3, 2, 85, 6.5, 'Doit s''améliorer'),
(9, '2024-07-10', 'ENTRAINEMENT', 0, 6, 5, 2, 12, 10, 2, 1, 80, 7.0, 'Bon travail défensif'),
(10, '2024-07-10', 'ENTRAINEMENT', 0, 30, 25, 12, 0, 0, 0, 5, 90, 7.5, 'Bonne défense'),

-- Performances de compétition
(1, '2024-07-15', 'COMPETITION', 4, 20, 18, 12, 25, 23, 2, 2, 100, 9.5, 'Performance exceptionnelle en compétition'),
(2, '2024-07-15', 'COMPETITION', 3, 10, 8, 6, 18, 16, 2, 2, 95, 9.0, 'Excellent match de compétition'),
(3, '2024-07-15', 'COMPETITION', 2, 8, 7, 5, 16, 14, 2, 1, 90, 9.0, 'Bonne présence au filet'),
(4, '2024-07-15', 'COMPETITION', 0, 42, 38, 22, 0, 0, 0, 4, 100, 9.5, 'Défense de haut niveau'),

-- Performances récentes (dernière semaine)
(1, '2024-08-01', 'MATCH', 3, 16, 14, 23, 21, 2, 2, 8.5, 'Très bon match récent'),
(2, '2024-08-01', 'MATCH', 2, 9, 7, 17, 15, 2, 2, 8.0, 'Performance solide'),
(4, '2024-08-01', 'MATCH', 0, 36, 32, 0, 0, 0, 4, 8.5, 'Défense efficace'),

-- Performances d'entraînement récentes
(5, '2024-08-03', 'ENTRAINEMENT', 2, 14, 11, 19, 16, 3, 3, 7.5, 'Entraînement productif'),
(6, '2024-08-03', 'ENTRAINEMENT', 1, 26, 21, 17, 14, 3, 5, 7.5, 'Progression constante')
ON CONFLICT (player_id, date_performance, type_performance) DO NOTHING;

-- =================================================================
-- INSERTION DES FICHIERS DE PERFORMANCE DE TEST
-- =================================================================

INSERT INTO performance_files (original_name, file_type, file_size, file_path, performance_id) VALUES
-- Fichiers vidéo de matchs
('match_2024_07_01_equipe_principale.mp4', 'mp4', 52428800, '/uploads/videos/match_2024_07_01_equipe_principale.mp4', 1),
('match_2024_07_01_equipe_principale.mp4', 'mp4', 52428800, '/uploads/videos/match_2024_07_01_equipe_principale.mp4', 2),
('match_2024_07_01_equipe_principale.mp4', 'mp4', 52428800, '/uploads/videos/match_2024_07_01_equipe_principale.mp4', 3),
('match_2024_07_01_equipe_principale.mp4', 'mp4', 52428800, '/uploads/videos/match_2024_07_01_equipe_principale.mp4', 4),

-- Fichiers d'analyse technique
('analyse_technique_mohamed_2024_07_01.pdf', 'pdf', 2097152, '/uploads/analyses/analyse_technique_mohamed_2024_07_01.pdf', 1),
('analyse_technique_youssef_2024_07_01.pdf', 'pdf', 2097152, '/uploads/analyses/analyse_technique_youssef_2024_07_01.pdf', 2),
('analyse_technique_amine_2024_07_01.pdf', 'pdf', 2097152, '/uploads/analyses/analyse_technique_amine_2024_07_01.pdf', 3),

-- Fichiers de statistiques
('statistiques_match_2024_07_01.xlsx', 'xlsx', 1048576, '/uploads/statistiques/statistiques_match_2024_07_01.xlsx', 1),
('statistiques_entrainement_2024_07_03.xlsx', 'xlsx', 1048576, '/uploads/statistiques/statistiques_entrainement_2024_07_03.xlsx', 2),

-- Photos de match
('photo_match_2024_07_01_01.jpg', 'jpg', 2097152, '/uploads/photos/photo_match_2024_07_01_01.jpg', 1),
('photo_match_2024_07_01_02.jpg', 'jpg', 2097152, '/uploads/photos/photo_match_2024_07_01_02.jpg', 1),
('photo_match_2024_07_01_03.jpg', 'jpg', 2097152, '/uploads/photos/photo_match_2024_07_01_03.jpg', 2),

-- Fichiers de compétition
('video_competition_2024_07_15.mp4', 'mp4', 104857600, '/uploads/competitions/video_competition_2024_07_15.mp4', 13),
('analyse_competition_2024_07_15.pdf', 'pdf', 4194304, '/uploads/competitions/analyse_competition_2024_07_15.pdf', 13),

-- Fichiers sans performance associée
('rapport_general_equipe_2024.pdf', 'pdf', 5242880, '/uploads/rapports/rapport_general_equipe_2024.pdf', NULL),
('planning_entrainements_2024.xlsx', 'xlsx', 1048576, '/uploads/plannings/planning_entrainements_2024.xlsx', NULL)
ON CONFLICT DO NOTHING;

-- =================================================================
-- VÉRIFICATION DES DONNÉES INSÉRÉES
-- =================================================================

-- Affichage du nombre d'enregistrements insérés
-- SELECT 'Joueurs insérés: ' || COUNT(*) FROM players;
-- SELECT 'Absences insérées: ' || COUNT(*) FROM absences;
-- SELECT 'Performances insérées: ' || COUNT(*) FROM performances;

-- =================================================================
-- FIN DES DONNÉES DE TEST
-- =================================================================
