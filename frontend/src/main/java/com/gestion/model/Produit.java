
package com.gestion.model;

import java.math.BigDecimal;

public class Produit {

    // Attributs
    private String reference;
    private String nom;
    private String categorie;
    private BigDecimal prixVente;
    private int quantite;
    private int seuilMinimal;
    private BigDecimal tauxTVA;
    private BigDecimal prixAchat;
    
    

    // Constructeur
    public Produit(
            String reference,
            String nom,
            String categorie,
            BigDecimal prixVente,
            int quantite) {

        this.reference = reference;
        this.nom = nom;
        this.categorie = categorie;
        this.prixVente = prixVente;
        this.quantite = quantite;
        this.seuilMinimal = 5;
        this.tauxTVA = BigDecimal.ZERO;
        this.prixAchat = BigDecimal.ZERO;
    }

    // Getters
    public String getReference() {
        return reference;
    }

    public String getNom() {
        return nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public BigDecimal getPrixVente() {
        return prixVente;
    }

    public int getQuantite() {
        return quantite;
    }

    public int getSeuilMinimal() {
        return seuilMinimal;
    }

    // Setters
    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setSeuilMinimal(int seuilMinimal) {
        if (seuilMinimal < 0) {
            throw new IllegalArgumentException(
                "Le seuil minimal ne peut pas être négatif."
            );
        }

        this.seuilMinimal = seuilMinimal;
    }

    // Statut du stock
    public String getStatutStock() {

            if (quantite == 0) {
                return "Rupture";
            }

            if (quantite <= seuilMinimal) {
                return "Stock faible";
            }

            return "Disponible";
        }
        public BigDecimal getTauxTVA() {
        return tauxTVA;
    }

    public void setTauxTVA(BigDecimal tauxTVA) {

        if (tauxTVA == null
                || tauxTVA.compareTo(BigDecimal.ZERO) < 0
                || tauxTVA.compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "Le taux de TVA doit être compris entre 0 et 100 %."
            );
        }

        this.tauxTVA = tauxTVA;
    }


    public BigDecimal getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(BigDecimal prixAchat) {

        if (prixAchat == null || prixAchat.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Le prix d'achat doit être positif ou égal à zéro."
            );
        }

        this.prixAchat = prixAchat;
    }
}
