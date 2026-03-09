-- Insertion de l'utilisateur admin si absent
-- Mot de passe : admin123 (BCrypt)
INSERT IGNORE INTO user (username, password, role)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lKzDQJzGlJaw3mtmq', 'GESTIONNAIRE');

-- Insertion des charges du référentiel si absentes (nécessite que l'admin ait l'id=1)
INSERT IGNORE INTO charge (id, nom_charge, type_charge, created_by) VALUES
(1,  'Essence Fiat',                'VEHICULES',         1),
(2,  'Essence Ford',                'VEHICULES',         1),
(3,  'Assurance Ford',              'VEHICULES',         1),
(4,  'Assurance Fiat',              'VEHICULES',         1),
(5,  'Touring Secours Ford',        'VEHICULES',         1),
(6,  'Touring Secours Fiat',        'VEHICULES',         1),
(7,  'Amende Ford',                 'VEHICULES',         1),
(8,  'Amende Fiat',                 'VEHICULES',         1),
(9,  'Entretien Ford / Control',    'VEHICULES',         1),
(10, 'Entretien Fiat / Control',    'VEHICULES',         1),
(11, 'Crédit Ford',                 'VEHICULES',         1),
(12, 'Entrepôt',                    'INFRASTRUCTURE',    1),
(13, 'Téléphone',                   'INFRASTRUCTURE',    1),
(14, 'Système GPS',                 'INFRASTRUCTURE',    1),
(15, 'Assurance Perso',             'FISCALES_SOCIALES', 1),
(16, 'TVA',                         'FISCALES_SOCIALES', 1),
(17, 'Congé Payé',                  'FISCALES_SOCIALES', 1),
(18, 'Afsca',                       'FISCALES_SOCIALES', 1),
(19, 'Comptable',                   'SERVICES_EXTERNES', 1);
