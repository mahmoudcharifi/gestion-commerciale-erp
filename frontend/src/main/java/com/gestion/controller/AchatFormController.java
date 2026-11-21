package com.gestion.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.gestion.model.Achat;
import com.gestion.model.Fournisseur;
import com.gestion.model.LigneAchat;
import com.gestion.model.Produit;
import com.gestion.service.AchatService;
import com.gestion.service.FournisseurService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AchatFormController {

    @FXML private ComboBox<Fournisseur> fournisseurComboBox;
    @FXML private DatePicker dateAchatPicker;

    @FXML private ComboBox<Produit> produitComboBox;
    @FXML private TextField quantiteField;
    @FXML private TextField prixAchatField;
    @FXML private TextField tauxTVAField;

    @FXML private TableView<LigneAchat> lignesTable;
    @FXML private TableColumn<LigneAchat, String> produitColumn;
    @FXML private TableColumn<LigneAchat, String> quantiteColumn;
    @FXML private TableColumn<LigneAchat, String> prixColumn;
    @FXML private TableColumn<LigneAchat, String> tvaColumn;
    @FXML private TableColumn<LigneAchat, String> totalColumn;
    @FXML private TableColumn<LigneAchat, Void> actionColumn;

    @FXML private Label totalHTLabel;
    @FXML private Label totalTVALabel;
    @FXML private Label totalTTCLabel;

    private final AchatService achatService = AchatService.getInstance();
    private final FournisseurService fournisseurService =
            FournisseurService.getInstance();

    private final ObservableList<LigneAchat> lignes =
            FXCollections.observableArrayList();

    private Achat achat;

    private final NumberFormat formatMontant =
            NumberFormat.getNumberInstance(Locale.FRANCE);

    @FXML
    public void initialize() {

        formatMontant.setMinimumFractionDigits(2);
        formatMontant.setMaximumFractionDigits(2);

        dateAchatPicker.setValue(LocalDate.now());

        chargerFournisseurs();
        chargerProduits();
        configurerTableau();

        lignesTable.setItems(lignes);

        actualiserTotaux();
    }

    private void chargerFournisseurs() {

        List<Fournisseur> fournisseurs =
                fournisseurService.getAllFournisseurs()
                        .stream()
                        .filter(Fournisseur::isActif)
                        .toList();

        fournisseurComboBox.setItems(
                FXCollections.observableArrayList(fournisseurs)
        );

        fournisseurComboBox.setCellFactory(listView ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(Fournisseur fournisseur,
                                              boolean empty) {
                        super.updateItem(fournisseur, empty);

                        setText(empty || fournisseur == null
                                ? null
                                : fournisseur.getNom());
                    }
                }
        );

        fournisseurComboBox.setButtonCell(
                new ListCell<>() {
                    @Override
                    protected void updateItem(Fournisseur fournisseur,
                                              boolean empty) {
                        super.updateItem(fournisseur, empty);

                        setText(empty || fournisseur == null
                                ? null
                                : fournisseur.getNom());
                    }
                }
        );
    }

    private void chargerProduits() {

        produitComboBox.setItems(
                FXCollections.observableArrayList(
                        ProductsController.getProducts()
                )
        );

        produitComboBox.setCellFactory(listView ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(Produit produit, boolean empty) {
                        super.updateItem(produit, empty);

                        setText(empty || produit == null
                                ? null
                                : produit.getReference() + " - "
                                  + produit.getNom());
                    }
                }
        );

        produitComboBox.setButtonCell(
                new ListCell<>() {
                    @Override
                    protected void updateItem(Produit produit, boolean empty) {
                        super.updateItem(produit, empty);

                        setText(empty || produit == null
                                ? null
                                : produit.getReference() + " - "
                                  + produit.getNom());
                    }
                }
        );
    }

    private void configurerTableau() {

        produitColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getProduit().getNom()
                )
        );

        quantiteColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.valueOf(cell.getValue().getQuantite())
                )
        );

        prixColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formater(cell.getValue().getPrixUnitaire()) + " DH"
                )
        );

        tvaColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getTauxTVA().toPlainString() + " %"
                )
        );

        totalColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formater(cell.getValue().getTotalTTC()) + " DH"
                )
        );

        configurerActionsLigne();
    }

    private void configurerActionsLigne() {

        actionColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button supprimerButton =
                            new Button("Supprimer");

                    {
                        supprimerButton.setOnAction(event -> {
                            LigneAchat ligne = getTableView()
                                    .getItems().get(getIndex());

                            lignes.remove(ligne);
                            actualiserTotaux();
                        });

                        supprimerButton.getStyleClass()
                                .add("danger-button");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        setGraphic(empty ? null : supprimerButton);
                    }
                }
        );
    }

    @FXML
    private void handleAjouterProduit() {

        try {

            Produit produit = produitComboBox.getValue();

            if (produit == null) {
                throw new IllegalArgumentException(
                        "Veuillez sélectionner un produit."
                );
            }

            int quantite = Integer.parseInt(
                    quantiteField.getText().trim()
            );

            BigDecimal prix = new BigDecimal(
                    prixAchatField.getText().trim().replace(",", ".")
            );

            BigDecimal tauxTVA = new BigDecimal(
                    tauxTVAField.getText().trim().replace(",", ".")
            );

            // Vérifier si le produit existe déjà dans les lignes.
            boolean existe = lignes.stream()
                    .anyMatch(ligne -> ligne.getProduit() == produit);

            if (existe) {
                throw new IllegalArgumentException(
                        "Ce produit existe déjà dans l'achat. "
                        + "Supprimez sa ligne avant de la remplacer."
                );
            }

            LigneAchat ligne = new LigneAchat(
                    (long) lignes.size() + 1,
                    produit,
                    quantite,
                    prix,
                    tauxTVA
            );

            lignes.add(ligne);

            actualiserTotaux();
            viderChampsProduit();

        } catch (NumberFormatException e) {

            afficherErreur(
                    "Veuillez saisir une quantité, un prix et un taux de TVA valides."
            );

        } catch (IllegalArgumentException e) {

            afficherErreur(e.getMessage());
        }
    }

    private void actualiserTotaux() {

        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (LigneAchat ligne : lignes) {
            totalHT = totalHT.add(ligne.getSousTotalHT());
            totalTVA = totalTVA.add(ligne.getMontantTVA());
        }

        BigDecimal totalTTC = totalHT.add(totalTVA);

        totalHTLabel.setText(formater(totalHT) + " DH");
        totalTVALabel.setText(formater(totalTVA) + " DH");
        totalTTCLabel.setText(formater(totalTTC) + " DH");
    }

    private void viderChampsProduit() {
        produitComboBox.setValue(null);
        quantiteField.clear();
        prixAchatField.clear();
        tauxTVAField.clear();
    }

    private String formater(BigDecimal montant) {
        return formatMontant.format(
                montant.setScale(2, RoundingMode.HALF_UP)
        );
    }

    @FXML
    private void handleEnregistrer() {

        try {
            verifierFormulaire();

            creerAchat();

            fermerFenetre();

        } catch (Exception e) {
            afficherErreur(e.getMessage());
        }
    }

    @FXML
    private void handleEnregistrerEtValider() {

        try {
            verifierFormulaire();

            creerAchat();

            achatService.validerAchat(achat);

            afficherInformation(
                    "L'achat a été enregistré et validé. "
                    + "Le stock a été mis à jour."
            );

            fermerFenetre();

        } catch (Exception e) {
            afficherErreur(e.getMessage());
        }
    }

    private void verifierFormulaire() {

        if (fournisseurComboBox.getValue() == null) {
            throw new IllegalArgumentException(
                    "Veuillez sélectionner un fournisseur."
            );
        }

        if (dateAchatPicker.getValue() == null) {
            throw new IllegalArgumentException(
                    "Veuillez sélectionner une date d'achat."
            );
        }

        if (lignes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ajoutez au moins un produit à l'achat."
            );
        }
    }

    private void creerAchat() {

        achat = achatService.creerAchat(
                fournisseurComboBox.getValue()
        );

        LocalDate date = dateAchatPicker.getValue();

        achat.setDateAchat(
                LocalDateTime.of(date, java.time.LocalTime.now())
        );

        for (LigneAchat ligne : lignes) {

            achatService.ajouterLigne(
                    achat,
                    ligne.getProduit(),
                    ligne.getQuantite(),
                    ligne.getPrixUnitaire(),
                    ligne.getTauxTVA()
            );
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) fournisseurComboBox.getScene().getWindow();
        stage.close();
    }

    private void afficherErreur(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Impossible d'effectuer cette opération");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Achat");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
