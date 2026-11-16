package com.gestion.controller;

import com.gestion.model.Categorie;
import com.gestion.service.CategorieService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryFormController {

    @FXML
    private Label formTitle;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label errorLabel;

    private CategorieService categorieService;
    private Categorie categorieToEdit;

    private boolean saved = false;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    public void setCategorieService(CategorieService service) {
        this.categorieService = service;
    }

    public void configureForAdd() {
        categorieToEdit = null;
        formTitle.setText("Nouvelle catégorie");
        nameField.clear();
        descriptionField.clear();
    }

    public void configureForEdit(Categorie categorie) {
        categorieToEdit = categorie;

        formTitle.setText("Modifier la catégorie");
        nameField.setText(categorie.getNom());
        descriptionField.setText(categorie.getDescription());
    }

    @FXML
    private void save() {
        String nom = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (nom.isEmpty()) {
            showError("Veuillez saisir le nom de la catégorie.");
            nameField.requestFocus();
            return;
        }

        try {
            if (categorieToEdit == null) {
                categorieService.addCategorie(nom, description);
            } else {
                categorieService.updateCategorie(
                    categorieToEdit.getId(),
                    nom,
                    description
                );
            }

            saved = true;
            closeWindow();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
