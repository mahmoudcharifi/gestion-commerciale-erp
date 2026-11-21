
package com.gestion.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gestion.model.Facture;
import com.gestion.model.StatutFacture;
import com.gestion.model.StatutVente;
import com.gestion.model.Vente;

public class FactureService {

    private final List<Facture> factures = new ArrayList<>();

    private long prochainId = 1;
    private long prochainNumero = 1;

    private FactureService() {
    }

    // =========================================
    // 1. GENERER AUTOMATIQUEMENT UNE FACTURE
    // =========================================

    public Facture genererFacture(Vente vente) {

        if (vente == null) {
            throw new IllegalArgumentException(
                    "La vente est obligatoire."
            );
        }

        if (vente.getStatutVente() != StatutVente.VALIDEE) {
            throw new IllegalStateException(
                    "Une facture ne peut être générée que pour une vente validée."
            );
        }

        // Empêcher la génération de plusieurs factures
        // pour une même vente.
        boolean factureExiste = factures.stream()
                .anyMatch(facture -> facture.getVente() == vente);

        if (factureExiste) {
            throw new IllegalStateException(
                    "Cette vente possède déjà une facture."
            );
        }

        String reference = String.format(
                "FAC-%03d", prochainNumero++
        );

        Facture facture = new Facture(
                prochainId++,
                reference,
                vente
        );

        factures.add(facture);

        return facture;
    }

    // =========================================
    // 2. RECUPERER TOUTES LES FACTURES
    // =========================================

    public List<Facture> getAllFactures() {

        return Collections.unmodifiableList(
                new ArrayList<>(factures)
        );
    }

    // =========================================
    // 3. RECHERCHER UNE FACTURE
    // =========================================

    public List<Facture> rechercherFactures(String recherche) {

        if (recherche == null || recherche.isBlank()) {
            return getAllFactures();
        }

        String terme = recherche.toLowerCase().trim();

        return factures.stream()
                .filter(facture ->
                        facture.getReference()
                                .toLowerCase()
                                .contains(terme)
                        ||
                        facture.getVente()
                                .getReference()
                                .toLowerCase()
                                .contains(terme)
                        ||
                        facture.getVente()
                                .getClient()
                                .getNomAffichage()
                                .toLowerCase()
                                .contains(terme)
                )
                .toList();
    }

    // =========================================
    // 4. TROUVER UNE FACTURE PAR ID
    // =========================================

    public Facture trouverParId(Long id) {

        return factures.stream()
                .filter(facture -> facture.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // =========================================
    // 5. TROUVER LA FACTURE D'UNE VENTE
    // =========================================

    public Facture trouverParVente(Vente vente) {

        if (vente == null) {
            return null;
        }

        return factures.stream()
                .filter(facture -> facture.getVente() == vente)
                .findFirst()
                .orElse(null);
    }

    // =========================================
    // 6. ANNULER UNE FACTURE
    // =========================================

    public void annulerFacture(Facture facture) {

        if (facture == null || !factures.contains(facture)) {
            throw new IllegalArgumentException(
                    "Facture introuvable."
            );
        }

        facture.annuler();
    }

    // =========================================
    // 7. STATISTIQUES
    // =========================================

    public long compterFactures() {
        return factures.size();
    }

    public long compterFacturesEmises() {

        return factures.stream()
                .filter(facture ->
                        facture.getStatut() == StatutFacture.EMISE
                )
                .count();
    }

    public long compterFacturesAnnulees() {

        return factures.stream()
                .filter(facture ->
                        facture.getStatut() == StatutFacture.ANNULEE
                )
                .count();
    }

    // =========================================
    // 8. SINGLETON
    // =========================================

    private static final FactureService INSTANCE =
            new FactureService();

    public static FactureService getInstance() {
        return INSTANCE;
    }
}
