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
('Zouari', 'Bilel', 'bilel.zouari@volleyball.tn', '+216 31 234 567', '1995-10-15', 'CENTRAL', 13, 193, 87.0, 'SUSPENDU', '2023-09-01');

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
(12, '2024-07-01', '2024-08-15', 'AUTRE', 'Suspension disciplinaire', 'APPROUVEE', false, 'Comportement inapproprié lors du match');

-- =================================================================
-- INSERTION DES PERFORMANCES DE TEST
-- =================================================================

INSERT INTO performances (player_id, date_performance, type_performance, attaques_totales, attaques_reussies, aces, blocs, receptions_totales, receptions_reussies, defenses, services_totaux, services_reussis, erreurs_attaque, erreurs_service, erreurs_reception, temps_jeu_minutes, note_globale, commentaires) VALUES

-- Performances de Mohamed (Passeur)
(1, '2024-07-01', 'MATCH', 5, 4, 2, 1, 15, 13, 8, 20, 18, 1, 2, 2, 90, 8.5, 'Excellente distribution, bon leadership'),
(1, '2024-07-03', 'ENTRAINEMENT', 8, 6, 1, 0, 20, 17, 5, 25, 22, 2, 3, 3, 120, 7.5, 'Bon entraînement, à améliorer la précision'),
(1, '2024-07-05', 'MATCH', 6, 5, 3, 2, 18, 16, 10, 22, 20, 1, 2, 2, 95, 9.0, 'Performance exceptionnelle'),

-- Performances de Youssef (Attaquant)
(2, '2024-07-01', 'MATCH', 25, 18, 1, 3, 8, 6, 4, 15, 12, 7, 3, 2, 85, 8.0, 'Bon pourcentage d''attaque'),
(2, '2024-07-03', 'ENTRAINEMENT', 30, 20, 0, 2, 10, 8, 3, 18, 15, 10, 3, 2, 110, 7.0, 'Beaucoup d''erreurs d''attaque'),
(2, '2024-07-05', 'MATCH', 28, 22, 2, 4, 12, 10, 6, 16, 14, 6, 2, 2, 90, 8.5, 'Très bonne performance offensive'),

-- Performances d'Amine (Central)
(3, '2024-07-01', 'MATCH', 15, 10, 0, 8, 5, 4, 2, 10, 8, 5, 2, 1, 80, 7.5, 'Bon travail au bloc'),
(3, '2024-07-03', 'ENTRAINEMENT', 18, 12, 1, 6, 8, 6, 3, 12, 10, 6, 2, 2, 100, 7.0, 'Entraînement correct'),
(3, '2024-07-05', 'MATCH', 20, 15, 1, 10, 6, 5, 4, 14, 12, 5, 2, 1, 85, 8.5, 'Excellente performance au bloc'),

-- Performances de Mehdi (Libéro)
(4, '2024-07-01', 'MATCH', 0, 0, 0, 0, 35, 30, 15, 0, 0, 0, 0, 5, 90, 8.5, 'Excellente défense'),
(4, '2024-07-03', 'ENTRAINEMENT', 0, 0, 0, 0, 40, 32, 18, 0, 0, 0, 0, 8, 120, 7.5, 'Bon entraînement défensif'),
(4, '2024-07-05', 'MATCH', 0, 0, 0, 0, 38, 35, 20, 0, 0, 0, 0, 3, 95, 9.0, 'Performance défensive exceptionnelle'),

-- Performances de Karim (Pointu)
(5, '2024-07-01', 'MATCH', 22, 15, 3, 2, 12, 9, 5, 18, 15, 7, 3, 3, 88, 7.5, 'Bon match général'),
(5, '2024-07-03', 'ENTRAINEMENT', 25, 18, 2, 1, 15, 12, 4, 20, 17, 7, 3, 3, 115, 7.5, 'Entraînement régulier'),

-- Performances de Sami (Récepteur-Attaquant)
(6, '2024-07-01', 'MATCH', 20, 14, 1, 1, 25, 20, 8, 16, 13, 6, 3, 5, 92, 7.0, 'Performance mitigée'),
(6, '2024-07-03', 'ENTRAINEMENT', 24, 17, 2, 2, 28, 23, 10, 18, 15, 7, 3, 5, 118, 7.5, 'Amélioration notable'),

-- Performances des joueurs de réserve
(7, '2024-07-10', 'ENTRAINEMENT', 12, 8, 1, 0, 18, 15, 6, 20, 17, 4, 3, 3, 90, 7.0, 'Bon potentiel'),
(8, '2024-07-10', 'ENTRAINEMENT', 18, 12, 0, 2, 10, 8, 3, 15, 12, 6, 3, 2, 85, 6.5, 'Doit s''améliorer'),
(9, '2024-07-10', 'ENTRAINEMENT', 14, 9, 0, 5, 6, 5, 2, 12, 10, 5, 2, 1, 80, 7.0, 'Bon travail au bloc'),
(10, '2024-07-10', 'ENTRAINEMENT', 0, 0, 0, 0, 30, 25, 12, 0, 0, 0, 0, 5, 90, 7.5, 'Bonne défense'),

-- Performances de compétition
(1, '2024-07-15', 'COMPETITION', 7, 6, 4, 2, 20, 18, 12, 25, 23, 1, 2, 2, 100, 9.5, 'Performance exceptionnelle en compétition'),
(2, '2024-07-15', 'COMPETITION', 30, 24, 3, 5, 10, 8, 6, 18, 16, 6, 2, 2, 95, 9.0, 'Excellent match de compétition'),
(3, '2024-07-15', 'COMPETITION', 22, 17, 2, 12, 8, 7, 5, 16, 14, 5, 2, 1, 90, 9.0, 'Domination au bloc'),
(4, '2024-07-15', 'COMPETITION', 0, 0, 0, 0, 42, 38, 22, 0, 0, 0, 0, 4, 100, 9.5, 'Défense de haut niveau'),

-- Performances récentes (dernière semaine)
(1, '2024-08-01', 'MATCH', 8, 7, 3, 1, 16, 14, 9, 23, 21, 1, 2, 2, 88, 8.5, 'Très bon match récent'),
(2, '2024-08-01', 'MATCH', 26, 19, 2, 3, 9, 7, 4, 17, 15, 7, 2, 2, 86, 8.0, 'Performance solide'),
(4, '2024-08-01', 'MATCH', 0, 0, 0, 0, 36, 32, 16, 0, 0, 0, 0, 4, 88, 8.5, 'Défense efficace'),

-- Performances d'entraînement récentes
(5, '2024-08-03', 'ENTRAINEMENT', 20, 15, 2, 2, 14, 11, 6, 19, 16, 5, 3, 3, 105, 7.5, 'Entraînement productif'),
(6, '2024-08-03', 'ENTRAINEMENT', 22, 16, 1, 1, 26, 21, 9, 17, 14, 6, 3, 5, 110, 7.5, 'Progression constante');

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
