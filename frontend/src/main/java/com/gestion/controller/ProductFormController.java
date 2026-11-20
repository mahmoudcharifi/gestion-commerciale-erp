package com.gestion.controller;

import java.math.BigDecimal;
import java.util.function.Consumer;

import com.gestion.model.Categorie;
import com.gestion.model.Produit;
import com.gestion.service.CategorieService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProductFormController {

    @FXML
    private TextField referenceField;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField tauxTVAField;

    @FXML
    private Label errorLabel;

    private Stage stage;
    private Consumer<Produit> onProductCreated;
    private Produit productToEdit;

    private final CategorieService categorieService =
            CategorieService.getInstance();

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {

        categoryComboBox.getItems().clear();

        for (Categorie categorie : categorieService.getAllCategories()) {
            categoryComboBox.getItems().add(categorie.getNom());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnProductCreated(Consumer<Produit> callback) {
        this.onProductCreated = callback;
    }

    @FXML
    private void saveProduct() {

        try {

            String reference = referenceField.getText().trim();
            String nom = nameField.getText().trim();
            String categorie = categoryComboBox.getValue();

            if (reference.isEmpty()
                    || nom.isEmpty()
                    || categorie == null
                    || priceField.getText().isBlank()
                    || quantityField.getText().isBlank()
                    || tauxTVAField.getText().isBlank()) {

                errorLabel.setText(
                        "Veuillez remplir tous les champs."
                );
                return;
            }

            BigDecimal prix = new BigDecimal(
                    priceField.getText().trim().replace(',', '.')
            );

            int quantite = Integer.parseInt(
                    quantityField.getText().trim()
            );

            BigDecimal tauxTVA = new BigDecimal(
                    tauxTVAField.getText().trim().replace(',', '.')
            );

            if (prix.compareTo(BigDecimal.ZERO) < 0 || quantite < 0) {

                errorLabel.setText(
                        "Le prix et la quantité doivent être positifs."
                );
                return;
            }

            if (tauxTVA.compareTo(BigDecimal.ZERO) < 0
                    || tauxTVA.compareTo(new BigDecimal("100")) > 0) {

                errorLabel.setText(
                        "Le taux de TVA doit être compris entre 0 et 100 %."
                );
                return;
            }

            Produit produit;

            if (productToEdit != null) {

                produit = productToEdit;

                produit.setNom(nom);
                produit.setCategorie(categorie);
                produit.setPrixVente(prix);
                produit.setQuantite(quantite);
                produit.setTauxTVA(tauxTVA);

            } else {

                produit = new Produit(
                        reference,
                        nom,
                        categorie,
                        prix,
                        quantite
                );

                produit.setTauxTVA(tauxTVA);
            }

            if (onProductCreated != null) {
                onProductCreated.accept(produit);
            }

            stage.close();

        } catch (NumberFormatException e) {

            errorLabel.setText(
                    "Vérifiez le format du prix, de la quantité et de la TVA."
            );

        } catch (IllegalArgumentException e) {

            errorLabel.setText(e.getMessage());
        }
    }

    public void setProductToEdit(Produit produit) {

        this.productToEdit = produit;

        referenceField.setText(produit.getReference());
        nameField.setText(produit.getNom());
        categoryComboBox.setValue(produit.getCategorie());
        priceField.setText(produit.getPrixVente().toString());
        quantityField.setText(String.valueOf(produit.getQuantite()));

        tauxTVAField.setText(
                produit.getTauxTVA() == null
                        ? "0"
                        : produit.getTauxTVA().toPlainString()
        );
    }
}
