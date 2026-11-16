
package com.gestion.service;

import java.util.ArrayList;
import java.util.List;

import com.gestion.model.Categorie;

public class CategorieService {

    // Liste des catégories
    private final List<Categorie> categories = new ArrayList<>();

    // Générateur d'identifiants
    private Long nextId = 1L;

    private static final CategorieService INSTANCE =
            new CategorieService();

    public static CategorieService getInstance() {
        return INSTANCE;
    }


    // Constructeur
    public CategorieService() {

        // Catégories initiales
        addCategorie("Informatique", "Matériel informatique");
        addCategorie("Accessoires", "Accessoires informatiques");
        addCategorie("Mobilier", "Mobilier de bureau");
        addCategorie("Fournitures", "Fournitures de bureau");
    }

    // Récupérer toutes les catégories
    public List<Categorie> getAllCategories() {
        return new ArrayList<>(categories);
    }

    // Ajouter une catégorie
    public void addCategorie(String nom, String description) {

        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom de la catégorie est obligatoire."
            );
        }

        if (existsByName(nom)) {
            throw new IllegalArgumentException(
                    "Cette catégorie existe déjà."
            );
        }

        Categorie categorie = new Categorie(
                nextId++,
                nom.trim(),
                description
        );

        categories.add(categorie);
    }

    // Modifier une catégorie
    public void updateCategorie(
            Long id,
            String nouveauNom,
            String nouvelleDescription
    ) {

        for (Categorie categorie : categories) {

            if (categorie.getId().equals(id)) {

                if (nouveauNom == null || nouveauNom.isBlank()) {
                    throw new IllegalArgumentException(
                            "Le nom de la catégorie est obligatoire."
                    );
                }

                for (Categorie autre : categories) {
                    if (!autre.getId().equals(id)
                            && autre.getNom().equalsIgnoreCase(nouveauNom.trim())) {
                        throw new IllegalArgumentException(
                                "Cette catégorie existe déjà."
                        );
                    }
                }

                categorie.setNom(nouveauNom.trim());
                categorie.setDescription(nouvelleDescription);

                return;
            }
        }

        throw new IllegalArgumentException(
                "Catégorie introuvable."
        );
    }

    // Supprimer une catégorie
    public void deleteCategorie(Long id) {

        boolean removed = categories.removeIf(
                categorie -> categorie.getId().equals(id)
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Catégorie introuvable."
            );
        }
    }

    // Vérifier si une catégorie existe déjà
    private boolean existsByName(String nom) {

        return categories.stream().anyMatch(
                categorie -> categorie.getNom()
                        .equalsIgnoreCase(nom.trim())
        );
    }

    public void deleteCategorie(Long id, boolean categorieUtilisee) {

        if (categorieUtilisee) {
            throw new IllegalArgumentException(
                "Impossible de supprimer cette catégorie : elle est utilisée par un ou plusieurs produits."
            );
        }

        deleteCategorie(id);
    }
}
