package com.gestion.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gestion.model.Fournisseur;

public class FournisseurService {

    private final List<Fournisseur> fournisseurs = new ArrayList<>();

    private long prochainId = 1;

    private FournisseurService() {
        // Données de démonstration
        ajouterFournisseur(
            "Maroc Distribution",
            "0612345678",
            "contact@marocdistribution.ma",
            "Avenue Mohammed V",
            "Rabat",
            "Fournisseur de matériel"
        );
    }

    // Ajouter un fournisseur
    public Fournisseur ajouterFournisseur(
            String nom,
            String telephone,
            String email,
            String adresse,
            String ville,
            String description) {

        validerFournisseur(nom, telephone, email);

        Fournisseur fournisseur = new Fournisseur(
            genererId(),
            genererCodeFournisseur(),
            nom.trim(),
            telephone.trim(),
            email == null ? "" : email.trim(),
            adresse == null ? "" : adresse.trim(),
            ville == null ? "" : ville.trim(),
            description == null ? "" : description.trim()
        );

        fournisseurs.add(fournisseur);
        return fournisseur;
    }

    // Modifier un fournisseur
    public void modifierFournisseur(Fournisseur fournisseur) {
        if (fournisseur == null || trouverParId(fournisseur.getId()) == null) {
            throw new IllegalArgumentException("Fournisseur introuvable.");
        }

        validerFournisseur(
            fournisseur.getNom(),
            fournisseur.getTelephone(),
            fournisseur.getEmail()
        );
    }

    // Récupérer tous les fournisseurs
    public List<Fournisseur> getAllFournisseurs() {
        return Collections.unmodifiableList(
            new ArrayList<>(fournisseurs)
        );
    }

    // Rechercher un fournisseur
    public List<Fournisseur> rechercherFournisseurs(String recherche) {
        if (recherche == null || recherche.isBlank()) {
            return getAllFournisseurs();
        }

        String terme = recherche.toLowerCase().trim();

        return fournisseurs.stream()
            .filter(f ->
                f.getNom().toLowerCase().contains(terme)
                || f.getCodeFournisseur().toLowerCase().contains(terme)
                || f.getTelephone().toLowerCase().contains(terme)
                || f.getEmail().toLowerCase().contains(terme)
            )
            .toList();
    }

    // Désactiver un fournisseur
    public void desactiverFournisseur(Long id) {
        Fournisseur fournisseur = trouverParId(id);

        if (fournisseur == null) {
            throw new IllegalArgumentException("Fournisseur introuvable.");
        }

        fournisseur.setActif(false);
    }

    // Activer un fournisseur
    public void activerFournisseur(Long id) {
        Fournisseur fournisseur = trouverParId(id);

        if (fournisseur == null) {
            throw new IllegalArgumentException("Fournisseur introuvable.");
        }

        fournisseur.setActif(true);
    }

    // Trouver un fournisseur par ID
    public Fournisseur trouverParId(Long id) {
        return fournisseurs.stream()
            .filter(f -> f.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    // Générer un ID
    private Long genererId() {
        return prochainId++;
    }

    // Générer un code fournisseur
    private String genererCodeFournisseur() {
        return String.format("FOU-%03d", fournisseurs.size() + 1);
    }

    // Validation
    private void validerFournisseur(
            String nom,
            String telephone,
            String email) {

        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException(
                "Le nom du fournisseur est obligatoire."
            );
        }

        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException(
                "Le numéro de téléphone est obligatoire."
            );
        }

        if (email != null && !email.isBlank()
                && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                "L'adresse email est invalide."
            );
        }
    }

    // Singleton
    private static final FournisseurService INSTANCE =
        new FournisseurService();

    public static FournisseurService getInstance() {
        return INSTANCE;
    }
}
