
package com.gestion.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.gestion.model.Achat;
import com.gestion.model.LigneAchat;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class AchatDetailsController {

    @FXML private Label referenceLabel;
    @FXML private Label referenceInfoLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;

    @FXML private Label fournisseurLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label villeLabel;

    @FXML private TableView<LigneAchat> produitsTable;

    @FXML private TableColumn<LigneAchat, String> referenceColumn;
    @FXML private TableColumn<LigneAchat, String> produitColumn;
    @FXML private TableColumn<LigneAchat, String> prixColumn;
    @FXML private TableColumn<LigneAchat, String> quantiteColumn;
    @FXML private TableColumn<LigneAchat, String> tvaColumn;
    @FXML private TableColumn<LigneAchat, String> totalColumn;

    @FXML private Label totalHTLabel;
    @FXML private Label totalTVALabel;
    @FXML private Label totalTTCLabel;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NumberFormat currencyFormatter =
            NumberFormat.getNumberInstance(Locale.FRANCE);

    @FXML
    public void initialize() {

        currencyFormatter.setMinimumFractionDigits(2);
        currencyFormatter.setMaximumFractionDigits(2);

        configurerTableau();
    }

    private void configurerTableau() {

        referenceColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getProduit().getReference()
                )
        );

        produitColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getProduit().getNom()
                )
        );

        prixColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterMontant(cell.getValue().getPrixUnitaire())
                        + " DH"
                )
        );

        quantiteColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.valueOf(cell.getValue().getQuantite())
                )
        );

        tvaColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getTauxTVA().toPlainString() + " %"
                )
        );

        totalColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        formaterMontant(cell.getValue().getTotalTTC())
                        + " DH"
                )
        );
    }

    public void setAchat(Achat achat) {

        if (achat == null) {
            throw new IllegalArgumentException(
                    "L'achat à afficher est obligatoire."
            );
        }

        // Informations générales
        referenceLabel.setText(achat.getReference());
        referenceInfoLabel.setText(achat.getReference());

        dateLabel.setText(
                achat.getDateAchat().format(dateFormatter)
        );

        statusLabel.setText(
                formaterStatut(achat.getStatut().toString())
        );

        // Informations fournisseur
        fournisseurLabel.setText(
                valeurOuDefaut(achat.getFournisseur().getNom())
        );

        telephoneLabel.setText(
                valeurOuDefaut(achat.getFournisseur().getTelephone())
        );

        emailLabel.setText(
                valeurOuDefaut(achat.getFournisseur().getEmail())
        );

        villeLabel.setText(
                valeurOuDefaut(achat.getFournisseur().getVille())
        );

        // Produits achetés
        produitsTable.setItems(
                FXCollections.observableArrayList(achat.getLignes())
        );

        // Récapitulatif financier
        totalHTLabel.setText(
                formaterMontant(achat.getMontantTotalHT()) + " DH"
        );

        totalTVALabel.setText(
                formaterMontant(achat.getMontantTVA()) + " DH"
        );

        totalTTCLabel.setText(
                formaterMontant(achat.getMontantTotalTTC()) + " DH"
        );
    }

    private String formaterStatut(String statut) {

        return switch (statut) {
            case "BROUILLON" -> "Brouillon";
            case "VALIDE" -> "Validé";
            case "ANNULE" -> "Annulé";
            default -> statut;
        };
    }

    private String formaterMontant(BigDecimal montant) {

        return currencyFormatter.format(montant);
    }

    private String valeurOuDefaut(String valeur) {

        return valeur == null || valeur.isBlank()
                ? "Non renseigné"
                : valeur;
    }

    @FXML
    private void handleClose() {

        Stage stage = (Stage) referenceLabel.getScene().getWindow();
        stage.close();
    }
}
