
package com.gestion.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LigneVente {

    private Long id;
    private Produit produit;

    private String referenceProduit;
    private String nomProduit;

    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal tauxTVA;

    // Constructeur principal
    public LigneVente(
            Long id,
            Produit produit,
            int quantite,
            BigDecimal prixUnitaire,
            BigDecimal tauxTVA) {

        if (produit == null) {
            throw new IllegalArgumentException(
                    "Le produit est obligatoire."
            );
        }

        if (quantite <= 0) {
            throw new IllegalArgumentException(
                    "La quantité doit être supérieure à zéro."
            );
        }

        if (prixUnitaire == null
                || prixUnitaire.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Le prix unitaire est invalide."
            );
        }

        if (tauxTVA == null
                || tauxTVA.compareTo(BigDecimal.ZERO) < 0
                || tauxTVA.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "Le taux de TVA doit être compris entre 0 et 100."
            );
        }

        this.id = id;
        this.produit = produit;

        this.referenceProduit = produit.getReference();
        this.nomProduit = produit.getNom();

        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.tauxTVA = tauxTVA;
    }

    // Constructeur utilisant automatiquement la TVA du produit
    public LigneVente(
            Long id,
            Produit produit,
            int quantite,
            BigDecimal prixUnitaire) {

        this(
                id,
                produit,
                quantite,
                prixUnitaire,
                produit != null && produit.getTauxTVA() != null
                        ? produit.getTauxTVA()
                        : BigDecimal.ZERO
        );
    }

    public BigDecimal getSousTotalHT() {
        return prixUnitaire.multiply(
                BigDecimal.valueOf(quantite)
        );
    }

    public BigDecimal getMontantTVA() {
        return getSousTotalHT()
                .multiply(tauxTVA)
                .divide(
                        new BigDecimal("100"),
                        2,
                        RoundingMode.HALF_UP
                );
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

    public String getReferenceProduit() {
        return referenceProduit;
    }

    public String getNomProduit() {
        return nomProduit;
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

    public void setQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException(
                    "La quantité doit être supérieure à zéro."
            );
        }

        this.quantite = quantite;
    }
}
