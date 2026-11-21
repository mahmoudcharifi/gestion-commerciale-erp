package com.gestion.controller;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import com.gestion.model.LigneVente;
import com.gestion.model.StatutPaiement;
import com.gestion.model.StatutVente;
import com.gestion.model.Vente;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class SaleDetailsController {

    @FXML private Label referenceLabel;
    @FXML private Label dateLabel;
    @FXML private Label clientLabel;
    @FXML private Label telephoneLabel;
    @FXML private Label emailLabel;

    @FXML private Label saleStatusLabel;
    @FXML private Label paymentStatusLabel;

    @FXML private Label totalHTLabel;
    @FXML private Label discountLabel;
    @FXML private Label vatLabel;
    @FXML private Label totalTTCLabel;
    @FXML private Label paidLabel;
    @FXML private Label remainingLabel;

    @FXML private TableView<LigneVente> productsTable;

    @FXML private TableColumn<LigneVente, String> referenceColumn;
    @FXML private TableColumn<LigneVente, String> productColumn;
    @FXML private TableColumn<LigneVente, String> priceColumn;
    @FXML private TableColumn<LigneVente, String> quantityColumn;
    @FXML private TableColumn<LigneVente, String> totalColumn;
    @FXML
    private Label referenceInfoLabel;

    private Vente vente;

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurerTableau();
    }

    public void setVente(Vente vente) {

        if (vente == null) {
            throw new IllegalArgumentException(
                    "La vente est obligatoire."
            );
        }

        this.vente = vente;



        referenceLabel.setText(vente.getReference());
        referenceInfoLabel.setText(vente.getReference());

        dateLabel.setText(
                vente.getDateVente().format(FORMAT_DATE)
        );

        clientLabel.setText(
                vente.getClient().getNomAffichage()
        );

        telephoneLabel.setText(
                valeurOuDefaut(vente.getClient().getTelephone())
        );

        emailLabel.setText(
                valeurOuDefaut(vente.getClient().getEmail())
        );

        saleStatusLabel.setText(
                formaterStatutVente(vente.getStatutVente())
        );

        paymentStatusLabel.setText(
                formaterStatutPaiement(vente.getStatutPaiement())
        );

        totalHTLabel.setText(formatMontant(vente.getMontantTotalHT()));
        discountLabel.setText(formatMontant(vente.getRemiseGlobale()));
        vatLabel.setText(formatMontant(vente.getMontantTVA()));
        totalTTCLabel.setText(formatMontant(vente.getMontantTotalTTC()));
        paidLabel.setText(formatMontant(vente.getMontantPaye()));
        remainingLabel.setText(formatMontant(vente.getResteAPayer()));

        productsTable.setItems(
                FXCollections.observableArrayList(vente.getLignes())
        );
    }

    private void configurerTableau() {

        referenceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getReferenceProduit()
                )
        );

        productColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNomProduit()
                )
        );

        priceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatMontant(data.getValue().getPrixUnitaire())
                )
        );

        quantityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(data.getValue().getQuantite())
                )
        );

        totalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatMontant(data.getValue().getSousTotalHT())
                )
        );
    }

    private String formatMontant(java.math.BigDecimal montant) {
        return montant.setScale(2, RoundingMode.HALF_UP) + " DH";
    }

    private String valeurOuDefaut(String valeur) {
        return valeur == null || valeur.isBlank()
                ? "Non renseigné"
                : valeur;
    }

    private String formaterStatutVente(StatutVente statut) {
        return switch (statut) {
            case BROUILLON -> "Brouillon";
            case VALIDEE -> "Validée";
            case ANNULEE -> "Annulée";
        };
    }

    private String formaterStatutPaiement(StatutPaiement statut) {
        return switch (statut) {
            case NON_PAYE -> "Non payé";
            case PARTIELLEMENT_PAYE -> "Partiellement payé";
            case PAYE -> "Payé";
        };
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) referenceLabel.getScene().getWindow();
        stage.close();
    }
}
