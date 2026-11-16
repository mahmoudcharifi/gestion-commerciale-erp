
package com.gestion.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gestion.model.MouvementStock;
import com.gestion.model.Produit;
import com.gestion.model.TypeMouvement;

public class StockService {

    private final List<MouvementStock> mouvements =
            new ArrayList<>();

    private long prochainId = 1;

    // Entrée de stock
    public void entreeStock(
            Produit produit,
            int quantite,
            String motif) {

        verifierProduit(produit);
        verifierQuantite(quantite);
        verifierMotif(motif);

        produit.setQuantite(
            produit.getQuantite() + quantite
        );

        enregistrerMouvement(
            produit,
            TypeMouvement.ENTREE,
            quantite,
            motif
        );
    }

    // Sortie de stock
    public void sortieStock(
            Produit produit,
            int quantite,
            String motif) {

        verifierProduit(produit);
        verifierQuantite(quantite);
        verifierMotif(motif);

        if (quantite > produit.getQuantite()) {
            throw new IllegalArgumentException(
                "Stock insuffisant. Quantité disponible : "
                + produit.getQuantite()
            );
        }

        produit.setQuantite(
            produit.getQuantite() - quantite
        );

        enregistrerMouvement(
            produit,
            TypeMouvement.SORTIE,
            quantite,
            motif
        );
    }

    // Enregistrer un mouvement
    private void enregistrerMouvement(
            Produit produit,
            TypeMouvement type,
            int quantite,
            String motif) {

        MouvementStock mouvement = new MouvementStock(
            prochainId++,
            produit,
            type,
            quantite,
            motif.trim(),
            LocalDateTime.now()
        );

        mouvements.add(mouvement);
    }

    // Récupérer l'historique
    public List<MouvementStock> getAllMouvements() {
        return Collections.unmodifiableList(
            new ArrayList<>(mouvements)
        );
    }

    // Vérifications
    private void verifierProduit(Produit produit) {
        if (produit == null) {
            throw new IllegalArgumentException(
                "Le produit est obligatoire."
            );
        }
    }

    private void verifierQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException(
                "La quantité doit être supérieure à zéro."
            );
        }
    }

    private void verifierMotif(String motif) {
        if (motif == null || motif.isBlank()) {
            throw new IllegalArgumentException(
                "Le motif du mouvement est obligatoire."
            );
        }
    }
}
