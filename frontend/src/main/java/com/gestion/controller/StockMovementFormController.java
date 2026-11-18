
package com.gestion.controller;

import com.gestion.model.Produit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StockMovementFormController {

    @FXML private Label titleLabel;
    @FXML private ComboBox<Produit> productComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField reasonField;
    @FXML private Button confirmButton;

    private boolean entree;
    private boolean valide = false;

    @FXML
    public void initialize() {
        ObservableList<Produit> produits =
                FXCollections.observableArrayList(
                        ProductsController.getProducts()
                );

        productComboBox.setItems(produits);

        productComboBox.setCellFactory(listView ->
                new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(Produit produit, boolean empty) {
                        super.updateItem(produit, empty);

                        setText(empty || produit == null
                                ? null
                                : produit.getReference() + " - " + produit.getNom());
                    }
                });

        productComboBox.setButtonCell(
                new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(Produit produit, boolean empty) {
                        super.updateItem(produit, empty);

                        setText(empty || produit == null
                                ? null
                                : produit.getReference() + " - " + produit.getNom());
                    }
                });
    }

    public void configurer(boolean entree) {
        this.entree = entree;

        titleLabel.setText(entree
                ? "Entrée de stock"
                : "Sortie de stock");

        confirmButton.setText(entree
                ? "Enregistrer l'entrée"
                : "Enregistrer la sortie");
    }

    @FXML
    private void handleConfirm() {
        Produit produit = productComboBox.getValue();

        if (produit == null) {
            afficherErreur("Veuillez sélectionner un produit.");
            return;
        }

        try {
            int quantite = Integer.parseInt(quantityField.getText().trim());

            if (quantite <= 0) {
                afficherErreur("La quantité doit être supérieure à zéro.");
                return;
            }

            if (reasonField.getText() == null
                    || reasonField.getText().isBlank()) {
                afficherErreur("Veuillez saisir le motif du mouvement.");
                return;
            }

            valide = true;
            fermer();

        } catch (NumberFormatException e) {
            afficherErreur("Veuillez saisir une quantité entière valide.");
        }
    }

    @FXML
    private void handleCancel() {
        fermer();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermer() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    public boolean isValide() {
        return valide;
    }

    public Produit getProduit() {
        return productComboBox.getValue();
    }

    public int getQuantite() {
        return Integer.parseInt(quantityField.getText().trim());
    }

    public String getMotif() {
        return reasonField.getText().trim();
    }

    public boolean isEntree() {
        return entree;
    }
}
