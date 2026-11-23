
-- =========================================================
-- PROJET : GestionCommerciale ERP
-- BASE DE DONNEES : gestion_commerciale
-- SCRIPT : 01_create_tables.sql
-- SGBD : PostgreSQL 18
-- =========================================================

-- 1. CATEGORIES
CREATE TABLE categorie (
    id_categorie BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. UNITES DE MESURE
CREATE TABLE unite_mesure (
    id_unite BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    symbole VARCHAR(15) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. TAXES
CREATE TABLE taxe (
    id_taxe BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    taux NUMERIC(5,2) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- 4. PRODUITS
CREATE TABLE produit (
    id_produit BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    prix_achat NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_vente NUMERIC(12,2) NOT NULL,
    seuil_minimal INTEGER NOT NULL DEFAULT 0,
    id_categorie BIGINT REFERENCES categorie(id_categorie),
    id_unite BIGINT NOT NULL REFERENCES unite_mesure(id_unite),
    id_taxe BIGINT NOT NULL REFERENCES taxe(id_taxe),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. TIERS (CLIENTS ET FOURNISSEURS)
CREATE TABLE tiers (
    id_tiers BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    type_tiers VARCHAR(20) NOT NULL,
    telephone VARCHAR(30),
    email VARCHAR(150),
    adresse TEXT,
    ville VARCHAR(100),
    ice VARCHAR(30),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. ROLES DES TIERS
CREATE TABLE tiers_role (
    id_tiers BIGINT NOT NULL REFERENCES tiers(id_tiers),
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_tiers, role)
);

-- 7. ROLES DES UTILISATEURS
CREATE TABLE role (
    id_role BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- 8. UTILISATEURS
CREATE TABLE utilisateur (
    id_utilisateur BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. ASSOCIATION UTILISATEURS / ROLES
CREATE TABLE utilisateur_role (
    id_utilisateur BIGINT NOT NULL
        REFERENCES utilisateur(id_utilisateur),
    id_role BIGINT NOT NULL
        REFERENCES role(id_role),
    PRIMARY KEY (id_utilisateur, id_role)
);

-- 10. ENTREPOTS
CREATE TABLE entrepot (
    id_entrepot BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse TEXT,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- 11. STOCK
CREATE TABLE stock (
    id_stock BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_produit BIGINT NOT NULL REFERENCES produit(id_produit),
    id_entrepot BIGINT NOT NULL REFERENCES entrepot(id_entrepot),
    quantite INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_stock_produit_entrepot
        UNIQUE (id_produit, id_entrepot)
);

-- 12. VENTES
CREATE TABLE vente (
    id_vente BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    id_tiers BIGINT NOT NULL REFERENCES tiers(id_tiers),
    id_utilisateur BIGINT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_vente TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    montant_ht NUMERIC(12,2) NOT NULL DEFAULT 0,
    montant_tva NUMERIC(12,2) NOT NULL DEFAULT 0,
    montant_ttc NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 13. LIGNES DE VENTE
CREATE TABLE ligne_vente (
    id_ligne_vente BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_vente BIGINT NOT NULL
        REFERENCES vente(id_vente) ON DELETE CASCADE,
    id_produit BIGINT NOT NULL REFERENCES produit(id_produit),
    designation VARCHAR(200) NOT NULL,
    quantite INTEGER NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    taux_tva NUMERIC(5,2) NOT NULL,
    remise NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_ht NUMERIC(12,2) NOT NULL,
    total_tva NUMERIC(12,2) NOT NULL,
    total_ttc NUMERIC(12,2) NOT NULL
);

-- 14. ACHATS
CREATE TABLE achat (
    id_achat BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    id_tiers BIGINT NOT NULL REFERENCES tiers(id_tiers),
    id_utilisateur BIGINT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_achat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    montant_ht NUMERIC(12,2) NOT NULL DEFAULT 0,
    montant_tva NUMERIC(12,2) NOT NULL DEFAULT 0,
    montant_ttc NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 15. LIGNES D'ACHAT
CREATE TABLE ligne_achat (
    id_ligne_achat BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_achat BIGINT NOT NULL
        REFERENCES achat(id_achat) ON DELETE CASCADE,
    id_produit BIGINT NOT NULL REFERENCES produit(id_produit),
    designation VARCHAR(200) NOT NULL,
    quantite INTEGER NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    taux_tva NUMERIC(5,2) NOT NULL,
    remise NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_ht NUMERIC(12,2) NOT NULL,
    total_tva NUMERIC(12,2) NOT NULL,
    total_ttc NUMERIC(12,2) NOT NULL
);

-- 16. PAIEMENTS
CREATE TABLE paiement (
    id_paiement BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_vente BIGINT REFERENCES vente(id_vente),
    id_achat BIGINT REFERENCES achat(id_achat),
    id_utilisateur BIGINT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_paiement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    montant NUMERIC(12,2) NOT NULL,
    mode_paiement VARCHAR(20) NOT NULL,
    reference_transaction VARCHAR(100),
    observation TEXT
);

-- 17. FACTURES
CREATE TABLE facture (
    id_facture BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    id_vente BIGINT NOT NULL UNIQUE REFERENCES vente(id_vente),
    date_facture DATE NOT NULL DEFAULT CURRENT_DATE,
    montant_ht NUMERIC(12,2) NOT NULL,
    montant_tva NUMERIC(12,2) NOT NULL,
    montant_ttc NUMERIC(12,2) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EMISE',
    chemin_pdf TEXT
);

-- 18. LIGNES DE FACTURE
CREATE TABLE ligne_facture (
    id_ligne_facture BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_facture BIGINT NOT NULL
        REFERENCES facture(id_facture) ON DELETE CASCADE,
    reference_produit VARCHAR(50),
    designation VARCHAR(200) NOT NULL,
    quantite INTEGER NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    taux_tva NUMERIC(5,2) NOT NULL,
    remise NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_ht NUMERIC(12,2) NOT NULL,
    total_tva NUMERIC(12,2) NOT NULL,
    total_ttc NUMERIC(12,2) NOT NULL
);

-- 19. MOUVEMENTS DE STOCK
CREATE TABLE mouvement_stock (
    id_mouvement BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_produit BIGINT NOT NULL REFERENCES produit(id_produit),
    id_entrepot BIGINT NOT NULL REFERENCES entrepot(id_entrepot),
    id_utilisateur BIGINT NOT NULL
        REFERENCES utilisateur(id_utilisateur),
    id_vente BIGINT REFERENCES vente(id_vente),
    id_achat BIGINT REFERENCES achat(id_achat),
    type_mouvement VARCHAR(30) NOT NULL,
    quantite INTEGER NOT NULL,
    motif TEXT,
    date_mouvement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- FIN DU SCRIPT
-- Les contraintes métier et index seront dans le script 02.
-- =========================================================
