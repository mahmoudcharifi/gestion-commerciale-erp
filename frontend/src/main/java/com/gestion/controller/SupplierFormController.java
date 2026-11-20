package com.gestion.controller;

import com.gestion.model.Fournisseur;
import com.gestion.service.FournisseurService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SupplierFormController {

    @FXML private Label formTitleLabel;
    @FXML private Label errorLabel;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;

    @FXML private TextArea descriptionField;

    private final FournisseurService fournisseurService =
            FournisseurService.getInstance();

    private Stage stage;
    private Fournisseur fournisseurAModifier;
    private Runnable onSupplierSaved;

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnSupplierSaved(Runnable callback) {
        this.onSupplierSaved = callback;
    }

    // Préparer le formulaire en mode modification
    public void setSupplierToEdit(Fournisseur fournisseur) {

        if (fournisseur == null) {
            return;
        }

        this.fournisseurAModifier = fournisseur;

        formTitleLabel.setText("Modifier le fournisseur");

        nameField.setText(fournisseur.getNom());
        phoneField.setText(fournisseur.getTelephone());
        emailField.setText(fournisseur.getEmail());
        addressField.setText(fournisseur.getAdresse());
        cityField.setText(fournisseur.getVille());
        descriptionField.setText(fournisseur.getDescription());
    }

    @FXML
    private void handleSave() {

        errorLabel.setText("");

        String nom = nameField.getText().trim();
        String telephone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        String adresse = addressField.getText().trim();
        String ville = cityField.getText().trim();
        String description = descriptionField.getText().trim();

        try {

            validerChamps(nom, telephone, email);

            if (fournisseurAModifier == null) {

                // Ajouter un fournisseur
                fournisseurService.ajouterFournisseur(
                        nom,
                        telephone,
                        email,
                        adresse,
                        ville,
                        description
                );

            } else {

                // Modifier un fournisseur existant
                fournisseurAModifier.setNom(nom);
                fournisseurAModifier.setTelephone(telephone);
                fournisseurAModifier.setEmail(email);
                fournisseurAModifier.setAdresse(adresse);
                fournisseurAModifier.setVille(ville);
                fournisseurAModifier.setDescription(description);

                fournisseurService.modifierFournisseur(
                        fournisseurAModifier
                );
            }

            if (onSupplierSaved != null) {
                onSupplierSaved.run();
            }

            fermerFormulaire();

        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            afficherErreur("Une erreur est survenue lors de l'enregistrement.");
            e.printStackTrace();
        }
    }

    private void validerChamps(
            String nom,
            String telephone,
            String email) {

        if (nom.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom du fournisseur est obligatoire."
            );
        }

        if (telephone.isBlank()) {
            throw new IllegalArgumentException(
                    "Le numéro de téléphone est obligatoire."
            );
        }

        if (!email.isBlank()
                && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                    "Veuillez saisir une adresse email valide."
            );
        }
    }

    @FXML
    private void handleCancel() {
        fermerFormulaire();
    }

    private void fermerFormulaire() {
        if (stage != null) {
            stage.close();
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
