
package com.gestion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.gestion.model.Client;
import com.gestion.model.Facture;
import com.gestion.model.LigneVente;
import com.gestion.model.Produit;
import com.gestion.model.StatutVente;
import com.gestion.model.Vente;

public class VenteService {

    private final List<Vente> ventes = new ArrayList<>();

    private final StockService stockService =
            StockService.getInstance();
    
    private final FactureService factureService =
        FactureService.getInstance();

    private long prochainId = 1;
    private long prochainNumero = 1;

    private VenteService() {
    }

    // =========================================
    // 1. CREER UNE NOUVELLE VENTE
    // =========================================

    public Vente creerVente(Client client) {

        if (client == null) {
            throw new IllegalArgumentException(
                    "Le client est obligatoire."
            );
        }

        if (!client.isActif()) {
            throw new IllegalArgumentException(
                    "Ce client est désactivé."
            );
        }

        String reference = String.format(
                "VEN-%03d", prochainNumero++
        );

        Vente vente = new Vente(
                prochainId++,
                reference,
                client
        );

        ventes.add(vente);

        return vente;
    }

    // =========================================
    // 2. AJOUTER UNE LIGNE DE VENTE
    // =========================================

    public void ajouterLigne(
            Vente vente,
            LigneVente ligne) {

        verifierVente(vente);
        verifierBrouillon(vente);

        if (ligne == null) {
            throw new IllegalArgumentException(
                    "La ligne de vente est obligatoire."
            );
        }

        vente.ajouterLigne(ligne);
    }

    // =========================================
    // 3. SUPPRIMER UNE LIGNE
    // =========================================

    public void supprimerLigne(
            Vente vente,
            LigneVente ligne) {

        verifierVente(vente);
        verifierBrouillon(vente);

        vente.supprimerLigne(ligne);
    }

    // =========================================
    // 4. APPLIQUER UNE REMISE GLOBALE
    // =========================================

    public void appliquerRemise(
            Vente vente,
            BigDecimal remise) {

        verifierVente(vente);
        verifierBrouillon(vente);

        vente.setRemiseGlobale(remise);
    }

    // =========================================
    // 5. VALIDER UNE VENTE
    // =========================================

    public void validerVente(Vente vente) {

        verifierVente(vente);
        verifierBrouillon(vente);

        if (vente.getLignes().isEmpty()) {
            throw new IllegalArgumentException(
                    "Une vente doit contenir au moins un produit."
            );
        }

        // Regrouper les quantités par produit
        Map<Produit, Integer> quantites =
                new IdentityHashMap<>();

        for (LigneVente ligne : vente.getLignes()) {

            Produit produit = ligne.getProduit();

            if (produit == null) {
                throw new IllegalArgumentException(
                        "Un produit de la vente est invalide."
                );
            }

            int nouvelleQuantite = quantites.getOrDefault(
                    produit, 0
            ) + ligne.getQuantite();

            quantites.put(produit, nouvelleQuantite);
        }

        // Vérifier toutes les quantités avant de modifier le stock
        for (Map.Entry<Produit, Integer> entree :
                quantites.entrySet()) {

            Produit produit = entree.getKey();
            int quantiteDemandee = entree.getValue();

            if (quantiteDemandee > produit.getQuantite()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour : "
                        + produit.getNom()
                        + ". Disponible : "
                        + produit.getQuantite()
                        + ", demandé : "
                        + quantiteDemandee
                );
            }
        }

        // Effectuer les sorties de stock
        for (Map.Entry<Produit, Integer> entree :
                quantites.entrySet()) {

            stockService.sortieStock(
                    entree.getKey(),
                    entree.getValue(),
                    "Vente " + vente.getReference()
            );
        }

        // Confirmer la vente
        vente.setStatutVente(StatutVente.VALIDEE);

        try {
            // Générer automatiquement la facture
            Facture facture = factureService.genererFacture(vente);

            System.out.println(
                    "Facture générée avec succès : "
                    + facture.getReference()
            );

        } catch (RuntimeException e) {

            // En cas d'échec, remettre la vente en brouillon
            vente.setStatutVente(StatutVente.BROUILLON);

            // Restaurer le stock
            for (Map.Entry<Produit, Integer> entree : quantites.entrySet()) {
                stockService.entreeStock(
                        entree.getKey(),
                        entree.getValue(),
                        "Annulation validation vente " + vente.getReference()
                );
            }

            throw new IllegalStateException(
                    "La vente n'a pas pu être validée car sa facture n'a pas pu être générée.",
                    e
            );
        }
    }

    // =========================================
    // 6. ANNULER UNE VENTE
    // =========================================

    public void annulerVente(Vente vente) {

        verifierVente(vente);

        if (vente.getStatutVente() ==
                StatutVente.ANNULEE) {

            throw new IllegalStateException(
                    "Cette vente est déjà annulée."
            );
        }

        if (vente.getStatutVente() ==
                StatutVente.BROUILLON) {

            vente.setStatutVente(StatutVente.ANNULEE);
            return;
        }

        if (vente.getMontantPaye().compareTo(
                BigDecimal.ZERO) > 0) {

            throw new IllegalStateException(
                    "Cette vente comporte des paiements. "
                    + "Un remboursement doit être traité "
                    + "avant son annulation."
            );
        }

        // Regrouper les quantités des produits
        Map<Produit, Integer> quantites =
                new IdentityHashMap<>();

        for (LigneVente ligne : vente.getLignes()) {

            Produit produit = ligne.getProduit();

            quantites.merge(
                    produit,
                    ligne.getQuantite(),
                    Integer::sum
            );
        }

        // Restituer le stock
        for (Map.Entry<Produit, Integer> entree :
                quantites.entrySet()) {

            stockService.entreeStock(
                    entree.getKey(),
                    entree.getValue(),
                    "Annulation vente " + vente.getReference()
            );
        }

        vente.setStatutVente(StatutVente.ANNULEE);
    }

    // =========================================
    // 7. ENREGISTRER UN PAIEMENT
    // =========================================

    public void enregistrerPaiement(
            Vente vente,
            BigDecimal montant) {

        verifierVente(vente);

        if (vente.getStatutVente() !=
                StatutVente.VALIDEE) {

            throw new IllegalStateException(
                    "Seules les ventes validées "
                    + "peuvent recevoir un paiement."
            );
        }

        vente.enregistrerPaiement(montant);
    }

    // =========================================
    // 8. RECUPERER TOUTES LES VENTES
    // =========================================

    public List<Vente> getAllVentes() {

        return Collections.unmodifiableList(
                new ArrayList<>(ventes)
        );
    }

    // =========================================
    // 9. RECHERCHER UNE VENTE
    // =========================================

    public List<Vente> rechercherVentes(String recherche) {

        if (recherche == null || recherche.isBlank()) {
            return getAllVentes();
        }

        String terme = recherche.toLowerCase().trim();

        return ventes.stream()
                .filter(vente ->
                        vente.getReference()
                                .toLowerCase()
                                .contains(terme)
                        ||
                        vente.getClient()
                                .getNomAffichage()
                                .toLowerCase()
                                .contains(terme)
                )
                .toList();
    }

    // =========================================
    // 10. TROUVER UNE VENTE PAR ID
    // =========================================

    public Vente trouverParId(Long id) {

        return ventes.stream()
                .filter(vente -> vente.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // =========================================
    // 11. CALCULER LE CHIFFRE D'AFFAIRES
    // =========================================

    public BigDecimal calculerChiffreAffaires() {

        return ventes.stream()
                .filter(vente ->
                        vente.getStatutVente() ==
                                StatutVente.VALIDEE
                )
                .map(Vente::getMontantTotalTTC)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================================
    // 12. COMPTER LES VENTES
    // =========================================

    public long compterVentes() {
        return ventes.size();
    }

    public long compterVentesValidees() {

        return ventes.stream()
                .filter(vente ->
                        vente.getStatutVente() ==
                                StatutVente.VALIDEE
                )
                .count();
    }

    public long compterVentesAnnulees() {

        return ventes.stream()
                .filter(vente ->
                        vente.getStatutVente() ==
                                StatutVente.ANNULEE
                )
                .count();
    }

    // =========================================
    // 13. VALIDATIONS INTERNES
    // =========================================

    private void verifierVente(Vente vente) {

        if (vente == null || !ventes.contains(vente)) {
            throw new IllegalArgumentException(
                    "Vente introuvable."
            );
        }
    }

    private void verifierBrouillon(Vente vente) {

        if (vente.getStatutVente() !=
                StatutVente.BROUILLON) {

            throw new IllegalStateException(
                    "Cette opération est autorisée "
                    + "uniquement sur une vente en brouillon."
            );
        }
    }

    // =========================================
    // 14. SINGLETON
    // =========================================

    private static final VenteService INSTANCE =
            new VenteService();

    public static VenteService getInstance() {
        return INSTANCE;
    }

    // Supprimer un brouillon créé accidentellement
    public void supprimerBrouillon(Vente vente) {

        verifierVente(vente);
        verifierBrouillon(vente);

        ventes.remove(vente);
    }
}
