package com.gestion.model;

import java.time.LocalDateTime;

public class MouvementStock {

    private Long id;
    private Produit produit;
    private TypeMouvement type;
    private int quantite;
    private String motif;
    private LocalDateTime dateMouvement;

    public MouvementStock(
            Long id,
            Produit produit,
            TypeMouvement type,
            int quantite,
            String motif,
            LocalDateTime dateMouvement) {

        this.id = id;
        this.produit = produit;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
        this.dateMouvement = dateMouvement;
    }

    public Long getId() {
        return id;
    }

    public Produit getProduit() {
        return produit;
    }

    public TypeMouvement getType() {
        return type;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getMotif() {
        return motif;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }
}
