-- Insertion de données de test pour le microservice finance-service

-- Insertion des budgets
INSERT INTO budgets (id_finance, montant) VALUES 
('BUD-2024-001', 50000.00),
('BUD-2024-002', 75000.00),
('BUD-2024-003', 30000.00);

-- Insertion des catégories de budget
INSERT INTO categories_budget (id_categorie, nom_categorie, montant_alloue, budget_id) VALUES 
(1, 'Équipements Sportifs', 15000.00, 1),
(2, 'Salaires Entraîneurs', 20000.00, 1),
(3, 'Déplacements', 10000.00, 1),
(4, 'Sponsors', 5000.00, 1),
(5, 'Formation', 25000.00, 2),
(6, 'Matériel Médical', 15000.00, 2),
(7, 'Marketing', 20000.00, 2),
(8, 'Infrastructure', 15000.00, 2),
(9, 'Événements', 12000.00, 3),
(10, 'Assurances', 8000.00, 3),
(11, 'Frais Administratifs', 10000.00, 3);

-- Insertion des dépenses
INSERT INTO depenses (id_depense, montant, date, description, categorie_budget_id) VALUES 
-- Équipements Sportifs
(1, 2500.00, '2024-01-15', 'Achat de ballons de volleyball professionnels', 1),
(2, 3000.00, '2024-01-20', 'Filets de volleyball de compétition', 1),
(3, 1800.00, '2024-02-05', 'Chaussures de sport pour l''équipe', 1),
(4, 2200.00, '2024-02-15', 'Maillots officiels de l''équipe', 1),

-- Salaires Entraîneurs
(5, 4000.00, '2024-01-31', 'Salaire entraîneur principal - Janvier', 2),
(6, 4000.00, '2024-02-29', 'Salaire entraîneur principal - Février', 2),
(7, 2500.00, '2024-01-31', 'Salaire entraîneur adjoint - Janvier', 2),
(8, 2500.00, '2024-02-29', 'Salaire entraîneur adjoint - Février', 2),

-- Déplacements
(9, 1200.00, '2024-01-10', 'Transport pour match à l''extérieur', 3),
(10, 800.00, '2024-01-25', 'Hébergement équipe déplacement', 3),
(11, 950.00, '2024-02-08', 'Carburant bus équipe', 3),

-- Sponsors
(12, 1500.00, '2024-01-05', 'Événement de présentation aux sponsors', 4),
(13, 800.00, '2024-02-12', 'Matériel promotionnel sponsors', 4),

-- Formation
(14, 5000.00, '2024-01-15', 'Stage de formation entraîneurs', 5),
(15, 3500.00, '2024-02-01', 'Séminaire tactiques avancées', 5),
(16, 2800.00, '2024-02-20', 'Formation premiers secours', 5),

-- Matériel Médical
(17, 1200.00, '2024-01-08', 'Trousse de premiers secours complète', 6),
(18, 800.00, '2024-01-22', 'Matériel de rééducation', 6),
(19, 600.00, '2024-02-10', 'Produits de soins sportifs', 6),

-- Marketing
(20, 3000.00, '2024-01-12', 'Campagne publicitaire locale', 7),
(21, 2500.00, '2024-02-05', 'Site web officiel de l''équipe', 7),
(22, 1800.00, '2024-02-18', 'Réseaux sociaux et communication', 7),

-- Infrastructure
(23, 4000.00, '2024-01-20', 'Rénovation vestiaires', 8),
(24, 2200.00, '2024-02-15', 'Éclairage terrain d''entraînement', 8),

-- Événements
(25, 3500.00, '2024-01-30', 'Organisation tournoi local', 9),
(26, 2800.00, '2024-02-25', 'Gala de fin de saison', 9),

-- Assurances
(27, 2000.00, '2024-01-01', 'Assurance responsabilité civile', 10),
(28, 1500.00, '2024-01-01', 'Assurance matériel sportif', 10),

-- Frais Administratifs
(29, 1200.00, '2024-01-15', 'Frais de comptabilité', 11),
(30, 800.00, '2024-02-01', 'Fournitures de bureau', 11),
(31, 600.00, '2024-02-15', 'Frais bancaires', 11);
