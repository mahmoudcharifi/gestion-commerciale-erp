package com.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Facture {

    private Long id;
    private String reference;
    private Vente vente;
    private LocalDateTime dateEmission;

    private BigDecimal montantTotalHT;
    private BigDecimal montantTVA;
    private BigDecimal montantTotalTTC;

    private StatutFacture statut;

    public Facture(
            Long id,
            String reference,
            Vente vente
    ) {
        if (vente == null) {
            throw new IllegalArgumentException(
                    "La vente est obligatoire."
            );
        }

        if (vente.getStatutVente() != StatutVente.VALIDEE) {
            throw new IllegalArgumentException(
                    "Une facture ne peut être créée que pour une vente validée."
            );
        }

        this.id = id;
        this.reference = reference;
        this.vente = vente;
        this.dateEmission = LocalDateTime.now();

        // Copier les montants de la vente
        this.montantTotalHT = vente.getMontantTotalHT();
        this.montantTVA = vente.getMontantTVA();
        this.montantTotalTTC = vente.getMontantTotalTTC();

        this.statut = StatutFacture.EMISE;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public Vente getVente() {
        return vente;
    }

    public LocalDateTime getDateEmission() {
        return dateEmission;
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

    public StatutFacture getStatut() {
        return statut;
    }

    // Annuler une facture

    public void annuler() {
        if (statut == StatutFacture.ANNULEE) {
            throw new IllegalStateException(
                    "Cette facture est déjà annulée."
            );
        }

        statut = StatutFacture.ANNULEE;
    }
}
