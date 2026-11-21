
package com.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Achat {

    private Long id;
    private String reference;
    private Fournisseur fournisseur;
    private List<LigneAchat> lignes;
    private LocalDateTime dateAchat;

    private BigDecimal montantTotalHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTotalTTC;

    private StatutAchat statut;

    public Achat(Long id, String reference, Fournisseur fournisseur) {

        if (fournisseur == null) {
            throw new IllegalArgumentException(
                "Le fournisseur est obligatoire."
            );
        }

        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException(
                "La référence de l'achat est obligatoire."
            );
        }

        this.id = id;
        this.reference = reference;
        this.fournisseur = fournisseur;
        this.lignes = new ArrayList<>();
        this.dateAchat = LocalDateTime.now();

        this.montantTotalHT = BigDecimal.ZERO;
        this.montantTVA = BigDecimal.ZERO;
        this.montantTotalTTC = BigDecimal.ZERO;

        this.statut = StatutAchat.BROUILLON;
    }

    public void ajouterLigne(LigneAchat ligne) {

        if (statut != StatutAchat.BROUILLON) {
            throw new IllegalStateException(
                "Impossible de modifier un achat qui n'est plus en brouillon."
            );
        }

        if (ligne == null) {
            throw new IllegalArgumentException(
                "La ligne d'achat est obligatoire."
            );
        }

        lignes.add(ligne);
        recalculerTotaux();
    }

    public void supprimerLigne(Long idLigne) {

        if (statut != StatutAchat.BROUILLON) {
            throw new IllegalStateException(
                "Impossible de modifier un achat qui n'est plus en brouillon."
            );
        }

        boolean supprimee = lignes.removeIf(
            ligne -> ligne.getId().equals(idLigne)
        );

        if (!supprimee) {
            throw new IllegalArgumentException(
                "La ligne d'achat est introuvable."
            );
        }

        recalculerTotaux();
    }

    public void recalculerTotaux() {

        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (LigneAchat ligne : lignes) {
            totalHT = totalHT.add(ligne.getSousTotalHT());
            totalTVA = totalTVA.add(ligne.getMontantTVA());
        }

        this.montantTotalHT = totalHT;
        this.montantTVA = totalTVA;
        this.montantTotalTTC = totalHT.add(totalTVA);
    }

    public void setStatut(StatutAchat statut) {
        if (statut == null) {
            throw new IllegalArgumentException(
                "Le statut de l'achat est obligatoire."
            );
        }

        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public List<LigneAchat> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    public LocalDateTime getDateAchat() {
        return dateAchat;
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

    public StatutAchat getStatut() {
        return statut;
    }

    
    public void setDateAchat(LocalDateTime dateAchat) {
        if (statut != StatutAchat.BROUILLON) {
            throw new IllegalStateException(
                "Impossible de modifier la date d'un achat validé ou annulé."
            );
        }

        if (dateAchat == null) {
            throw new IllegalArgumentException(
                "La date d'achat est obligatoire."
            );
        }

        this.dateAchat = dateAchat;
    }
}
