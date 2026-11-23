
BEGIN;

-- 1. ROLES UTILISATEURS
INSERT INTO role (nom, description)
VALUES
('ADMIN', 'Administrateur de l application'),
('GESTIONNAIRE', 'Gestion des achats, ventes et stocks'),
('VENDEUR', 'Gestion des ventes et des clients');

-- 2. UNITES DE MESURE
INSERT INTO unite_mesure (nom, symbole)
VALUES
('Pièce', 'pcs'),
('Kilogramme', 'kg'),
('Litre', 'L'),
('Carton', 'ctn');

-- 3. TAXES
INSERT INTO taxe (nom, taux)
VALUES
('TVA 20%', 20.00),
('TVA 14%', 14.00),
('TVA 10%', 10.00),
('TVA 0%', 0.00);

-- 4. CATEGORIES
INSERT INTO categorie (nom, description)
VALUES
('Informatique', 'Ordinateurs et matériel informatique'),
('Accessoires', 'Accessoires informatiques'),
('Périphériques', 'Claviers, souris et périphériques');

-- 5. ENTREPOT PRINCIPAL
INSERT INTO entrepot (nom, adresse)
VALUES
('Entrepôt principal', 'Adresse à compléter');

COMMIT;
