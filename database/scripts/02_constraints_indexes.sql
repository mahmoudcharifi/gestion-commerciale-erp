
-- =========================================================
-- PROJET : GestionCommerciale ERP
-- SCRIPT : 02_constraints_indexes.sql
-- SGBD : PostgreSQL 18
-- =========================================================

-- =========================================================
-- 1. CONTRAINTES SUR LES CATEGORIES
-- =========================================================

ALTER TABLE categorie
ADD CONSTRAINT chk_categorie_nom
CHECK (LENGTH(TRIM(nom)) > 0);


-- =========================================================
-- 2. CONTRAINTES SUR LES TAXES
-- =========================================================

ALTER TABLE taxe
ADD CONSTRAINT chk_taxe_taux
CHECK (taux >= 0 AND taux <= 100);


-- =========================================================
-- 3. CONTRAINTES SUR LES PRODUITS
-- =========================================================

ALTER TABLE produit
ADD CONSTRAINT chk_produit_prix_achat
CHECK (prix_achat >= 0);

ALTER TABLE produit
ADD CONSTRAINT chk_produit_prix_vente
CHECK (prix_vente >= 0);

ALTER TABLE produit
ADD CONSTRAINT chk_produit_seuil_minimal
CHECK (seuil_minimal >= 0);


-- =========================================================
-- 4. CONTRAINTES SUR LES ROLES DES TIERS
-- =========================================================

ALTER TABLE tiers_role
ADD CONSTRAINT chk_tiers_role
CHECK (role IN ('CLIENT', 'FOURNISSEUR'));


-- =========================================================
-- 5. CONTRAINTES SUR LES UTILISATEURS
-- =========================================================

ALTER TABLE utilisateur
ADD CONSTRAINT chk_utilisateur_email
CHECK (POSITION('@' IN email) > 1);


-- =========================================================
-- 6. CONTRAINTES SUR LES STOCKS
-- =========================================================

ALTER TABLE stock
ADD CONSTRAINT chk_stock_quantite
CHECK (quantite >= 0);


-- =========================================================
-- 7. CONTRAINTES SUR LES VENTES
-- =========================================================

ALTER TABLE vente
ADD CONSTRAINT chk_vente_statut
CHECK (statut IN ('BROUILLON', 'VALIDEE', 'ANNULEE'));

ALTER TABLE vente
ADD CONSTRAINT chk_vente_montants
CHECK (
    montant_ht >= 0
    AND montant_tva >= 0
    AND montant_ttc >= 0
);


-- =========================================================
-- 8. CONTRAINTES SUR LES LIGNES DE VENTE
-- =========================================================

ALTER TABLE ligne_vente
ADD CONSTRAINT chk_ligne_vente_quantite
CHECK (quantite > 0);

ALTER TABLE ligne_vente
ADD CONSTRAINT chk_ligne_vente_prix
CHECK (prix_unitaire >= 0);

ALTER TABLE ligne_vente
ADD CONSTRAINT chk_ligne_vente_tva
CHECK (taux_tva >= 0 AND taux_tva <= 100);

ALTER TABLE ligne_vente
ADD CONSTRAINT chk_ligne_vente_remise
CHECK (
    remise >= 0
    AND remise <= quantite * prix_unitaire
);

ALTER TABLE ligne_vente
ADD CONSTRAINT chk_ligne_vente_totaux
CHECK (
    total_ht >= 0
    AND total_tva >= 0
    AND total_ttc >= 0
);


-- =========================================================
-- 9. CONTRAINTES SUR LES ACHATS
-- =========================================================

ALTER TABLE achat
ADD CONSTRAINT chk_achat_statut
CHECK (statut IN ('BROUILLON', 'VALIDEE', 'ANNULEE'));

ALTER TABLE achat
ADD CONSTRAINT chk_achat_montants
CHECK (
    montant_ht >= 0
    AND montant_tva >= 0
    AND montant_ttc >= 0
);


-- =========================================================
-- 10. CONTRAINTES SUR LES LIGNES D'ACHAT
-- =========================================================

ALTER TABLE ligne_achat
ADD CONSTRAINT chk_ligne_achat_quantite
CHECK (quantite > 0);

ALTER TABLE ligne_achat
ADD CONSTRAINT chk_ligne_achat_prix
CHECK (prix_unitaire >= 0);

ALTER TABLE ligne_achat
ADD CONSTRAINT chk_ligne_achat_tva
CHECK (taux_tva >= 0 AND taux_tva <= 100);

ALTER TABLE ligne_achat
ADD CONSTRAINT chk_ligne_achat_remise
CHECK (
    remise >= 0
    AND remise <= quantite * prix_unitaire
);

ALTER TABLE ligne_achat
ADD CONSTRAINT chk_ligne_achat_totaux
CHECK (
    total_ht >= 0
    AND total_tva >= 0
    AND total_ttc >= 0
);


-- =========================================================
-- 11. CONTRAINTES SUR LES PAIEMENTS
-- =========================================================

-- Un paiement concerne une vente OU un achat,
-- jamais les deux simultanément.

ALTER TABLE paiement
ADD CONSTRAINT chk_paiement_document
CHECK (
    (id_vente IS NOT NULL AND id_achat IS NULL)
    OR
    (id_vente IS NULL AND id_achat IS NOT NULL)
);

ALTER TABLE paiement
ADD CONSTRAINT chk_paiement_montant
CHECK (montant > 0);

ALTER TABLE paiement
ADD CONSTRAINT chk_paiement_mode
CHECK (mode_paiement IN ('ESPECES', 'CARTE'));


-- =========================================================
-- 12. CONTRAINTES SUR LES FACTURES
-- =========================================================

ALTER TABLE facture
ADD CONSTRAINT chk_facture_statut
CHECK (
    statut IN (
        'EMISE',
        'PARTIELLEMENT_PAYEE',
        'PAYEE',
        'ANNULEE'
    )
);

ALTER TABLE facture
ADD CONSTRAINT chk_facture_montants
CHECK (
    montant_ht >= 0
    AND montant_tva >= 0
    AND montant_ttc >= 0
);


-- =========================================================
-- 13. CONTRAINTES SUR LES LIGNES DE FACTURE
-- =========================================================

ALTER TABLE ligne_facture
ADD CONSTRAINT chk_ligne_facture_quantite
CHECK (quantite > 0);

ALTER TABLE ligne_facture
ADD CONSTRAINT chk_ligne_facture_prix
CHECK (prix_unitaire >= 0);

ALTER TABLE ligne_facture
ADD CONSTRAINT chk_ligne_facture_tva
CHECK (taux_tva >= 0 AND taux_tva <= 100);

ALTER TABLE ligne_facture
ADD CONSTRAINT chk_ligne_facture_remise
CHECK (
    remise >= 0
    AND remise <= quantite * prix_unitaire
);

ALTER TABLE ligne_facture
ADD CONSTRAINT chk_ligne_facture_totaux
CHECK (
    total_ht >= 0
    AND total_tva >= 0
    AND total_ttc >= 0
);


-- =========================================================
-- 14. CONTRAINTES SUR LES MOUVEMENTS DE STOCK
-- =========================================================

ALTER TABLE mouvement_stock
ADD CONSTRAINT chk_mouvement_type
CHECK (
    type_mouvement IN (
        'ENTREE_ACHAT',
        'SORTIE_VENTE',
        'AJUSTEMENT_ENTREE',
        'AJUSTEMENT_SORTIE'
    )
);

ALTER TABLE mouvement_stock
ADD CONSTRAINT chk_mouvement_quantite
CHECK (quantite > 0);

-- Une entrée d'achat doit être liée à un achat.
-- Une sortie de vente doit être liée à une vente.
-- Un ajustement manuel exige un motif.

ALTER TABLE mouvement_stock
ADD CONSTRAINT chk_mouvement_origine
CHECK (
    (
        type_mouvement = 'ENTREE_ACHAT'
        AND id_achat IS NOT NULL
        AND id_vente IS NULL
    )
    OR
    (
        type_mouvement = 'SORTIE_VENTE'
        AND id_vente IS NOT NULL
        AND id_achat IS NULL
    )
    OR
    (
        type_mouvement IN (
            'AJUSTEMENT_ENTREE',
            'AJUSTEMENT_SORTIE'
        )
        AND id_vente IS NULL
        AND id_achat IS NULL
        AND motif IS NOT NULL
        AND LENGTH(TRIM(motif)) > 0
    )
);


-- =========================================================
-- 15. INDEX POUR OPTIMISER LES REQUETES
-- =========================================================

-- Produits
CREATE INDEX idx_produit_categorie
ON produit(id_categorie);

CREATE INDEX idx_produit_nom
ON produit(nom);

-- Tiers
CREATE INDEX idx_tiers_nom
ON tiers(nom);

-- Ventes
CREATE INDEX idx_vente_tiers
ON vente(id_tiers);

CREATE INDEX idx_vente_utilisateur
ON vente(id_utilisateur);

CREATE INDEX idx_vente_date
ON vente(date_vente);

CREATE INDEX idx_vente_statut
ON vente(statut);

-- Lignes de vente
CREATE INDEX idx_ligne_vente_vente
ON ligne_vente(id_vente);

CREATE INDEX idx_ligne_vente_produit
ON ligne_vente(id_produit);

-- Achats
CREATE INDEX idx_achat_tiers
ON achat(id_tiers);

CREATE INDEX idx_achat_utilisateur
ON achat(id_utilisateur);

CREATE INDEX idx_achat_date
ON achat(date_achat);

-- Lignes d'achat
CREATE INDEX idx_ligne_achat_achat
ON ligne_achat(id_achat);

CREATE INDEX idx_ligne_achat_produit
ON ligne_achat(id_produit);

-- Paiements
CREATE INDEX idx_paiement_vente
ON paiement(id_vente);

CREATE INDEX idx_paiement_achat
ON paiement(id_achat);

CREATE INDEX idx_paiement_date
ON paiement(date_paiement);

-- Factures
CREATE INDEX idx_facture_date
ON facture(date_facture);

-- Stock
CREATE INDEX idx_stock_produit
ON stock(id_produit);

CREATE INDEX idx_stock_entrepot
ON stock(id_entrepot);

-- Mouvements de stock
CREATE INDEX idx_mouvement_produit
ON mouvement_stock(id_produit);

CREATE INDEX idx_mouvement_date
ON mouvement_stock(date_mouvement);

CREATE INDEX idx_mouvement_utilisateur
ON mouvement_stock(id_utilisateur);

-- =========================================================
-- FIN DU SCRIPT
-- =========================================================
