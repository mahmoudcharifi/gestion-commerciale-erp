
package com.gestion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gestion.model.Achat;
import com.gestion.model.Fournisseur;
import com.gestion.model.LigneAchat;
import com.gestion.model.Produit;
import com.gestion.model.StatutAchat;

public class AchatService {

    private final List<Achat> achats = new ArrayList<>();
    private final StockService stockService = StockService.getInstance();

    private long prochainIdAchat = 1;
    private long prochaineReference = 1;
    private long prochaineIdLigne = 1;

    private static final AchatService INSTANCE = new AchatService();

    private AchatService() {
    }

    public static AchatService getInstance() {
        return INSTANCE;
    }

    // Créer un achat
    public Achat creerAchat(Fournisseur fournisseur) {

        if (fournisseur == null) {
            throw new IllegalArgumentException(
                "Veuillez sélectionner un fournisseur."
            );
        }

        if (!fournisseur.isActif()) {
            throw new IllegalArgumentException(
                "Impossible de créer un achat avec un fournisseur inactif."
            );
        }

        Achat achat = new Achat(
            prochainIdAchat++,
            genererReference(),
            fournisseur
        );

        achats.add(achat);
        return achat;
    }

    // Ajouter un produit à un achat
    public void ajouterLigne(
            Achat achat,
            Produit produit,
            int quantite,
            BigDecimal prixUnitaire,
            BigDecimal tauxTVA) {

        verifierAchatBrouillon(achat);

        LigneAchat ligne = new LigneAchat(
            prochaineIdLigne++,
            produit,
            quantite,
            prixUnitaire,
            tauxTVA
        );

        achat.ajouterLigne(ligne);
    }

    // Supprimer une ligne
    public void supprimerLigne(Achat achat, Long idLigne) {
        verifierAchatBrouillon(achat);
        achat.supprimerLigne(idLigne);
    }

    // Valider un achat et mettre à jour le stock
    public void validerAchat(Achat achat) {

        verifierAchatBrouillon(achat);

        if (achat.getLignes().isEmpty()) {
            throw new IllegalArgumentException(
                "Impossible de valider un achat sans produits."
            );
        }

        // Vérifier toutes les lignes avant de modifier le stock.
        for (LigneAchat ligne : achat.getLignes()) {
            if (ligne.getProduit() == null) {
                throw new IllegalArgumentException(
                    "Un produit de l'achat est invalide."
                );
            }

            if (ligne.getQuantite() <= 0) {
                throw new IllegalArgumentException(
                    "Une quantité d'achat est invalide."
                );
            }
        }

        // Ajouter les quantités au stock.
        for (LigneAchat ligne : achat.getLignes()) {
            stockService.entreeStock(
                ligne.getProduit(),
                ligne.getQuantite(),
                "Achat " + achat.getReference()
            );
        }

        achat.setStatut(StatutAchat.VALIDE);
    }

    // Annuler un achat
    public void annulerAchat(Achat achat) {

        verifierAchat(achat);

        if (achat.getStatut() == StatutAchat.ANNULE) {
            throw new IllegalStateException(
                "Cet achat est déjà annulé."
            );
        }

        if (achat.getStatut() == StatutAchat.VALIDE) {
            // Vérifier que les produits sont toujours disponibles.
            for (LigneAchat ligne : achat.getLignes()) {
                if (ligne.getProduit().getQuantite() < ligne.getQuantite()) {
                    throw new IllegalStateException(
                        "Impossible d'annuler l'achat : stock insuffisant pour "
                        + ligne.getProduit().getNom()
                    );
                }
            }

            // Retirer les quantités précédemment ajoutées.
            for (LigneAchat ligne : achat.getLignes()) {
                stockService.sortieStock(
                    ligne.getProduit(),
                    ligne.getQuantite(),
                    "Annulation achat " + achat.getReference()
                );
            }
        }

        achat.setStatut(StatutAchat.ANNULE);
    }

    public List<Achat> getAllAchats() {
        return Collections.unmodifiableList(
            new ArrayList<>(achats)
        );
    }

    public Achat trouverParId(Long id) {
        return achats.stream()
            .filter(achat -> achat.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public BigDecimal calculerTotalAchats() {
        return achats.stream()
            .filter(achat -> achat.getStatut() == StatutAchat.VALIDE)
            .map(Achat::getMontantTotalTTC)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long compterAchats() {
        return achats.size();
    }

    public long compterAchatsValides() {
        return achats.stream()
            .filter(achat -> achat.getStatut() == StatutAchat.VALIDE)
            .count();
    }

    public long compterAchatsAnnules() {
        return achats.stream()
            .filter(achat -> achat.getStatut() == StatutAchat.ANNULE)
            .count();
    }

    private String genererReference() {
        return String.format("ACH-%03d", prochaineReference++);
    }

    private void verifierAchat(Achat achat) {
        if (achat == null || !achats.contains(achat)) {
            throw new IllegalArgumentException(
                "Cet achat n'existe pas dans le système."
            );
        }
    }

    private void verifierAchatBrouillon(Achat achat) {
        verifierAchat(achat);

        if (achat.getStatut() != StatutAchat.BROUILLON) {
            throw new IllegalStateException(
                "Cette opération est autorisée uniquement sur un achat en brouillon."
            );
        }
    }
}
