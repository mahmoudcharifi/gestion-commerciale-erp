
package com.gestion;

import com.gestion.model.Categorie;
import com.gestion.service.CategorieService;

public class TestCategorie {

    public static void main(String[] args) {

        CategorieService service = new CategorieService();

        // Afficher les catégories initiales
        System.out.println("=== CATÉGORIES INITIALES ===");

        for (Categorie categorie : service.getAllCategories()) {
            System.out.println(
                    categorie.getId() + " - " + categorie.getNom()
            );
        }

        // Ajouter une catégorie
        service.addCategorie(
                "Électronique",
                "Produits électroniques"
        );

        System.out.println("\n=== APRÈS AJOUT ===");

        for (Categorie categorie : service.getAllCategories()) {
            System.out.println(categorie.getNom());
        }

        // Modifier une catégorie
        service.updateCategorie(
                5L,
                "Électronique et multimédia",
                "Produits électroniques et multimédias"
        );

        System.out.println("\n=== APRÈS MODIFICATION ===");

        for (Categorie categorie : service.getAllCategories()) {
            System.out.println(categorie.getNom());
        }

        // Supprimer une catégorie
        service.deleteCategorie(5L);

        System.out.println("\n=== APRÈS SUPPRESSION ===");

        for (Categorie categorie : service.getAllCategories()) {
            System.out.println(categorie.getNom());
        }
    }
}
