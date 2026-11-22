
package com.gestion.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.gestion.model.Client;
import com.gestion.model.Vente;
import com.gestion.service.ClientService;
import com.gestion.service.VenteService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController {

    // =========================================
    // STATISTIQUES
    // =========================================

    @FXML
    private Label chiffreAffairesLabel;

    @FXML
    private Label totalProduitsLabel;

    @FXML
    private Label totalClientsLabel;

    @FXML
    private Label stockFaibleLabel;

    // =========================================
    // TABLEAU DES ACTIVITÉS RÉCENTES
    // =========================================

    @FXML
    private TableView<Vente> activiteTable;

    @FXML
    private TableColumn<Vente, String> referenceColumn;

    @FXML
    private TableColumn<Vente, String> clientColumn;

    @FXML
    private TableColumn<Vente, String> dateColumn;

    @FXML
    private TableColumn<Vente, String> montantColumn;

    @FXML
    private TableColumn<Vente, String> statutColumn;

    // =========================================
    // FORMATS
    // =========================================

    private static final DateTimeFormatter FORMAT_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat FORMAT_MONTANT =
            NumberFormat.getNumberInstance(Locale.FRANCE);

    // =========================================
    // INITIALISATION
    // =========================================

    @FXML
    public void initialize() {

        FORMAT_MONTANT.setMinimumFractionDigits(2);
        FORMAT_MONTANT.setMaximumFractionDigits(2);

        chargerStatistiques();
        configurerTableauActivites();
        chargerActivitesRecentes();

        System.out.println("Dashboard initialisé avec succès !");
    }

    // =========================================
    // CHARGER LES STATISTIQUES
    // =========================================

    private void chargerStatistiques() {

        chargerStatistiquesProduits();
        chargerStatistiquesClients();
        chargerChiffreAffaires();
    }

    // =========================================
    // STATISTIQUES DES PRODUITS
    // =========================================

    private void chargerStatistiquesProduits() {

        ProductsController.assurerInitialisation();

        int totalProduits =
                ProductsController.getProducts().size();

        long produitsStockFaible =
                ProductsController.getProducts()
                        .stream()
                        .filter(produit ->
                                produit.getQuantite()
                                        <= produit.getSeuilMinimal())
                        .count();

        totalProduitsLabel.setText(
                String.valueOf(totalProduits)
        );

        stockFaibleLabel.setText(
                String.valueOf(produitsStockFaible)
        );
    }

    // =========================================
    // STATISTIQUES DES CLIENTS
    // =========================================

    private void chargerStatistiquesClients() {

        long totalClients = ClientService.getInstance()
                .getAllClients()
                .stream()
                .filter(Client::isActif)
                .count();

        totalClientsLabel.setText(
                String.valueOf(totalClients)
        );
    }

    // =========================================
    // CHIFFRE D'AFFAIRES
    // =========================================

    private void chargerChiffreAffaires() {

        BigDecimal chiffreAffaires =
                VenteService.getInstance()
                        .calculerChiffreAffaires();

        chiffreAffairesLabel.setText(
                FORMAT_MONTANT.format(chiffreAffaires)
                        + " MAD"
        );
    }

    // =========================================
    // CONFIGURATION DU TABLEAU
    // =========================================

    private void configurerTableauActivites() {

        referenceColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getReference()
                )
        );

        clientColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getClient().getNomAffichage()
                )
        );

        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getDateVente() == null
                                ? ""
                                : cell.getValue()
                                        .getDateVente()
                                        .format(FORMAT_DATE)
                )
        );

        montantColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        FORMAT_MONTANT.format(
                                cell.getValue().getMontantTotalTTC()
                        ) + " MAD"
                )
        );

        statutColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getStatutVente().toString()
                )
        );
    }

    // =========================================
    // CHARGER LES ACTIVITÉS RÉCENTES
    // =========================================

    private void chargerActivitesRecentes() {

        List<Vente> ventesRecentes =
                VenteService.getInstance()
                        .getAllVentes()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        Vente::getDateVente,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                        )
                        .limit(5)
                        .toList();

        activiteTable.setItems(
                FXCollections.observableArrayList(
                        ventesRecentes
                )
        );
    }
}
