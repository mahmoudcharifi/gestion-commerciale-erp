
package com.gestion.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vente {

    private Long id;
    private String reference;

    private Client client;

    private List<LigneVente> lignes = new ArrayList<>();

    private LocalDateTime dateVente;

    private BigDecimal remiseGlobale;
    private BigDecimal montantTotalHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTotalTTC;

    private BigDecimal montantPaye;

    private StatutVente statutVente;
    private StatutPaiement statutPaiement;

    public Vente(Long id, String reference, Client client) {

        if (client == null) {
            throw new IllegalArgumentException(
                "Le client est obligatoire."
            );
        }

        this.id = id;
        this.reference = reference;
        this.client = client;

        this.dateVente = LocalDateTime.now();

        this.remiseGlobale = BigDecimal.ZERO;
        this.montantTotalHT = BigDecimal.ZERO;
        this.montantTVA = BigDecimal.ZERO;
        this.montantTotalTTC = BigDecimal.ZERO;
        this.montantPaye = BigDecimal.ZERO;

        this.statutVente = StatutVente.BROUILLON;
        this.statutPaiement = StatutPaiement.NON_PAYE;
    }

    // Ajouter une ligne de vente
    public void ajouterLigne(LigneVente ligne) {

        if (ligne == null) {
            throw new IllegalArgumentException(
                "La ligne de vente est obligatoire."
            );
        }

        if (statutVente != StatutVente.BROUILLON) {
            throw new IllegalStateException(
                "Impossible de modifier une vente déjà validée."
            );
        }

        lignes.add(ligne);
        recalculerTotaux();
    }

    // Supprimer une ligne
    public void supprimerLigne(LigneVente ligne) {

        if (statutVente != StatutVente.BROUILLON) {
            throw new IllegalStateException(
                "Impossible de modifier une vente déjà validée."
            );
        }

        lignes.remove(ligne);
        recalculerTotaux();
    }

    // Appliquer une remise globale
    public void setRemiseGlobale(BigDecimal remiseGlobale) {

        if (statutVente != StatutVente.BROUILLON) {
            throw new IllegalStateException(
                "Impossible de modifier une vente déjà validée."
            );
        }

        if (remiseGlobale == null
                || remiseGlobale.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "La remise ne peut pas être négative."
            );
        }

        this.remiseGlobale = remiseGlobale;
        recalculerTotaux();
    }

    // Calculer les montants
    public void recalculerTotaux() {

        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (LigneVente ligne : lignes) {
            totalHT = totalHT.add(ligne.getSousTotalHT());
            totalTVA = totalTVA.add(ligne.getMontantTVA());
        }

        if (remiseGlobale.compareTo(totalHT) > 0) {
            throw new IllegalArgumentException(
                "La remise ne peut pas dépasser le total HT."
            );
        }

        BigDecimal tauxRemise = totalHT.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : remiseGlobale.divide(
                    totalHT, 10, RoundingMode.HALF_UP
                );

        BigDecimal reductionTVA = totalTVA.multiply(tauxRemise);

        this.montantTotalHT = totalHT.subtract(remiseGlobale);

        this.montantTVA = totalTVA.subtract(
            reductionTVA.setScale(2, RoundingMode.HALF_UP)
        );

        this.montantTotalTTC = montantTotalHT.add(montantTVA);
    }

    // Enregistrer un paiement
    public void enregistrerPaiement(BigDecimal montant) {

        if (statutVente != StatutVente.VALIDEE) {
            throw new IllegalStateException(
                "Le paiement concerne uniquement une vente validée."
            );
        }

        if (montant == null
                || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Le montant du paiement doit être positif."
            );
        }

        BigDecimal nouveauMontant = montantPaye.add(montant);

        if (nouveauMontant.compareTo(montantTotalTTC) > 0) {
            throw new IllegalArgumentException(
                "Le montant payé dépasse le total de la vente."
            );
        }

        montantPaye = nouveauMontant;
        actualiserStatutPaiement();
    }

    private void actualiserStatutPaiement() {

        if (montantPaye.compareTo(BigDecimal.ZERO) == 0) {
            statutPaiement = StatutPaiement.NON_PAYE;
        } else if (montantPaye.compareTo(montantTotalTTC) < 0) {
            statutPaiement = StatutPaiement.PARTIELLEMENT_PAYE;
        } else {
            statutPaiement = StatutPaiement.PAYE;
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public Client getClient() {
        return client;
    }

    public List<LigneVente> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public BigDecimal getRemiseGlobale() {
        return remiseGlobale;
    }

    public BigDecimal getMontantTotalHT() {
        return montantTotalHT;
    }

    public BigDecimal getMontantTVA() {
        return montantTVA;
    }

    public BigDecimal getMontantTotalTTC() {
        return montantTotalTTC;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public BigDecimal getResteAPayer() {
        return montantTotalTTC.subtract(montantPaye);
    }

    public StatutVente getStatutVente() {
        return statutVente;
    }

    public StatutPaiement getStatutPaiement() {
        return statutPaiement;
    }

    // Modification du statut commercial
    public void setStatutVente(StatutVente statutVente) {
        this.statutVente = statutVente;
    }
}
