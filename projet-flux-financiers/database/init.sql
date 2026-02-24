-- ============================================================================
-- SCRIPT D'INITIALISATION BASE DE DONNÉES
-- Application : Gestion des Flux Financiers
-- Version : 2.1 FINAL
-- ============================================================================

USE flux_financiers;

-- ============================================================================
-- SUPPRESSION DES TABLES SI ELLES EXISTENT (ordre inverse des FK)
-- ============================================================================

DROP TABLE IF EXISTS paiement_charge;
DROP TABLE IF EXISTS paiement_employe;
DROP TABLE IF EXISTS versement;
DROP TABLE IF EXISTS charge;
DROP TABLE IF EXISTS employe;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS user;

-- ============================================================================
-- CRÉATION DES TABLES
-- ============================================================================

-- Table USER
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('GESTIONNAIRE') NOT NULL DEFAULT 'GESTIONNAIRE',
  CONSTRAINT uk_user_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table CLIENT
CREATE TABLE client (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_client_user FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE RESTRICT,
  INDEX idx_client_created_by (created_by),
  INDEX idx_client_nom (nom),
  INDEX idx_client_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table VERSEMENT
CREATE TABLE versement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  client_id BIGINT NOT NULL,
  montant_ttc DECIMAL(10,2) NOT NULL CHECK (montant_ttc > 0),
  date_versement DATE NOT NULL,
  mode_paiement ENUM('ESPECES','VIREMENT','CHEQUE','CARTE_BANCAIRE'),
  remarque VARCHAR(500),
  CONSTRAINT fk_versement_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE RESTRICT,
  INDEX idx_versement_client (client_id),
  INDEX idx_versement_date (date_versement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table EMPLOYE
CREATE TABLE employe (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  statut ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_employe_user FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE RESTRICT,
  INDEX idx_employe_statut (statut),
  INDEX idx_employe_nom (nom),
  INDEX idx_employe_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table PAIEMENT_PERSONNEL
CREATE TABLE paiement_employe (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employe_id BIGINT NOT NULL,
  type_paiement ENUM('SALAIRE','PRIME') NOT NULL,
  mode_paiement ENUM('ESPECES','VIREMENT','CHEQUE','CARTE_BANCAIRE') NOT NULL,
  montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
  date_paiement DATE NOT NULL,
  remarque VARCHAR(500),
  CONSTRAINT fk_paiement_employe_employe FOREIGN KEY (employe_id) REFERENCES employe(id) ON DELETE RESTRICT,
  INDEX idx_paiement_employe_employe (employe_id),
  INDEX idx_paiement_employe_date (date_paiement),
  INDEX idx_paiement_employe_type (type_paiement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table CHARGE
CREATE TABLE charge (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom_charge VARCHAR(200) NOT NULL,
  type_charge ENUM('VEHICULES','INFRASTRUCTURE','FISCALES_SOCIALES','SERVICES_EXTERNES') NOT NULL,
  created_by BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_charge_user FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE RESTRICT,
  INDEX idx_charge_type (type_charge),
  INDEX idx_charge_nom (nom_charge),
  INDEX idx_charge_created_by (created_by),
  INDEX idx_charge_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table PAIEMENT_CHARGE
CREATE TABLE paiement_charge (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  charge_id BIGINT NOT NULL,
  montant DECIMAL(10,2) NOT NULL CHECK (montant > 0),
  date_paiement DATE NOT NULL,
  mode_paiement ENUM('ESPECES','VIREMENT','CHEQUE','CARTE_BANCAIRE') NOT NULL,
  remarque VARCHAR(500),
  CONSTRAINT fk_paiement_charge_charge FOREIGN KEY (charge_id) REFERENCES charge(id) ON DELETE RESTRICT,
  INDEX idx_paiement_charge_charge (charge_id),
  INDEX idx_paiement_charge_date (date_paiement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- INSERTION DES DONNÉES INITIALES
-- ============================================================================

-- Utilisateur gestionnaire par défaut
-- Mot de passe: admin123 (hashé avec BCrypt)
INSERT INTO user (username, password, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lKzDQJzGlJaw3mtmq', 'GESTIONNAIRE');

-- Charges du référentiel (19 charges) - created_by = 1 (admin)
INSERT INTO charge (nom_charge, type_charge, created_by) VALUES
-- VEHICULES (11 charges)
('Essence Fiat', 'VEHICULES', 1),
('Essence Ford', 'VEHICULES', 1),
('Assurance Ford', 'VEHICULES', 1),
('Assurance Fiat', 'VEHICULES', 1),
('Touring Secours Ford', 'VEHICULES', 1),
('Touring Secours Fiat', 'VEHICULES', 1),
('Amende Ford', 'VEHICULES', 1),
('Amende Fiat', 'VEHICULES', 1),
('Entretien Ford / Control', 'VEHICULES', 1),
('Entretien Fiat / Control', 'VEHICULES', 1),
('Crédit Ford', 'VEHICULES', 1),

-- INFRASTRUCTURE (3 charges)
('Entrepôt', 'INFRASTRUCTURE', 1),
('Téléphone', 'INFRASTRUCTURE', 1),
('Système GPS', 'INFRASTRUCTURE', 1),

-- FISCALES_SOCIALES (4 charges)
('Assurance Perso', 'FISCALES_SOCIALES', 1),
('TVA', 'FISCALES_SOCIALES', 1),
('Congé Payé', 'FISCALES_SOCIALES', 1),
('Afsca', 'FISCALES_SOCIALES', 1),

-- SERVICES_EXTERNES (1 charge)
('Comptable', 'SERVICES_EXTERNES', 1);

-- ============================================================================
-- VUES UTILES
-- ============================================================================

-- Vue : Synthèse des recettes par client
CREATE VIEW v_recettes_par_client AS
SELECT 
  c.id,
  c.nom,
  c.prenom,
  COUNT(v.id) AS nb_versements,
  COALESCE(SUM(v.montant_ttc), 0) AS total_versements
FROM client c
LEFT JOIN versement v ON c.id = v.client_id
GROUP BY c.id, c.nom, c.prenom;

-- Vue : Synthèse des dépenses personnel par employé
CREATE VIEW v_depenses_par_employe AS
SELECT 
  e.id,
  e.nom,
  e.prenom,
  e.statut,
  COUNT(p.id) AS nb_paiements,
  COALESCE(SUM(p.montant), 0) AS total_paiements
FROM employe e
LEFT JOIN paiement_employe p ON e.id = p.employe_id
GROUP BY e.id, e.nom, e.prenom, e.statut;

-- Vue : Synthèse des paiements par charge
CREATE VIEW v_paiements_par_charge AS
SELECT 
  ch.id,
  ch.nom_charge,
  ch.type_charge,
  u.username AS created_by_username,
  ch.created_at,
  COUNT(pc.id) AS nb_paiements,
  COALESCE(SUM(pc.montant), 0) AS total_paiements
FROM charge ch
LEFT JOIN paiement_charge pc ON ch.id = pc.charge_id
LEFT JOIN user u ON ch.created_by = u.id
GROUP BY ch.id, ch.nom_charge, ch.type_charge, u.username, ch.created_at;

-- Vue : Synthèse financière globale
CREATE VIEW v_synthese_financiere AS
SELECT 
  (SELECT COALESCE(SUM(montant_ttc), 0) FROM versement) AS total_recettes,
  (SELECT COALESCE(SUM(montant), 0) FROM paiement_employe) AS total_depenses_personnel,
  (SELECT COALESCE(SUM(montant), 0) FROM paiement_charge) AS total_depenses_charges,
  (SELECT COALESCE(SUM(montant_ttc), 0) FROM versement) - 
  ((SELECT COALESCE(SUM(montant), 0) FROM paiement_employe) + 
   (SELECT COALESCE(SUM(montant), 0) FROM paiement_charge)) AS solde;

-- Vue : Traçabilité des créations
CREATE VIEW v_tracabilite_creations AS
SELECT 
  u.username,
  'CLIENT' AS type_entite,
  COUNT(DISTINCT c.id) AS nb_crees
FROM user u
LEFT JOIN client c ON c.created_by = u.id
GROUP BY u.username
UNION ALL
SELECT 
  u.username,
  'EMPLOYE' AS type_entite,
  COUNT(DISTINCT e.id) AS nb_crees
FROM user u
LEFT JOIN employe e ON e.created_by = u.id
GROUP BY u.username
UNION ALL
SELECT 
  u.username,
  'CHARGE' AS type_entite,
  COUNT(DISTINCT ch.id) AS nb_crees
FROM user u
LEFT JOIN charge ch ON ch.created_by = u.id
GROUP BY u.username;

-- ============================================================================
-- VÉRIFICATION
-- ============================================================================

-- Afficher le nombre d'enregistrements par table
SELECT 'Users' AS Table_Name, COUNT(*) AS Count FROM user
UNION ALL
SELECT 'Charges' AS Table_Name, COUNT(*) AS Count FROM charge;

-- Afficher les charges par catégorie
SELECT type_charge, COUNT(*) AS nb_charges 
FROM charge 
GROUP BY type_charge 
ORDER BY type_charge;

-- ============================================================================
-- FIN DU SCRIPT D'INITIALISATION
-- ============================================================================
