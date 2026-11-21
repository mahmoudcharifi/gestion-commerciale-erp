
package com.gestion.model;

import java.math.BigDecimal;

public class LigneAchat {

    private Long id;
    private Produit produit;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal tauxTVA;

    public LigneAchat(
            Long id,
            Produit produit,
            int quantite,
            BigDecimal prixUnitaire,
            BigDecimal tauxTVA) {

        if (produit == null) {
            throw new IllegalArgumentException("Le produit est obligatoire.");
        }

        if (quantite <= 0) {
            throw new IllegalArgumentException(
                "La quantité doit être supérieure à zéro."
            );
        }

        if (prixUnitaire == null ||
                prixUnitaire.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix d'achat est invalide.");
        }

        if (tauxTVA == null ||
                tauxTVA.compareTo(BigDecimal.ZERO) < 0 ||
                tauxTVA.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Le taux de TVA est invalide.");
        }

        this.id = id;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.tauxTVA = tauxTVA;
    }

    public BigDecimal getSousTotalHT() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    public BigDecimal getMontantTVA() {
        return getSousTotalHT()
                .multiply(tauxTVA)
                .divide(new BigDecimal("100"), 2,
                        java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalTTC() {
        return getSousTotalHT().add(getMontantTVA());
    }

    public Long getId() {
        return id;
    }

    public Produit getProduit() {
        return produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public BigDecimal getTauxTVA() {
        return tauxTVA;
    }
}
