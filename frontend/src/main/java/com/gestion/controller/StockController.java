
package com.gestion.controller;

import java.math.BigDecimal;

import com.gestion.model.Produit;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StockController {

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label stockValueLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label outOfStockLabel;

    @FXML
    private TableView<Produit> stockTable;

    @FXML
    private TableColumn<Produit, String> referenceColumn;

    @FXML
    private TableColumn<Produit, String> nameColumn;

    @FXML
    private TableColumn<Produit, String> categoryColumn;

    @FXML
    private TableColumn<Produit, Integer> quantityColumn;

    @FXML
    private TableColumn<Produit, Integer> thresholdColumn;

    @FXML
    private TableColumn<Produit, String> statusColumn;

    private ObservableList<Produit> products;

    @FXML
    public void initialize() {

        configureTable();

        products = ProductsController.getProducts();

        stockTable.setItems(products);

        updateStatistics();

        // Actualiser les statistiques lors d'un ajout
        // ou d'une suppression de produit.
        products.addListener(
            (ListChangeListener<Produit>) change ->
                updateStatistics()
        );

    }

    private void configureTable() {

        referenceColumn.setCellValueFactory(
            new PropertyValueFactory<>("reference")
        );

        nameColumn.setCellValueFactory(
            new PropertyValueFactory<>("nom")
        );

        categoryColumn.setCellValueFactory(
            new PropertyValueFactory<>("categorie")
        );

        quantityColumn.setCellValueFactory(
            new PropertyValueFactory<>("quantite")
        );

        thresholdColumn.setCellValueFactory(
            new PropertyValueFactory<>("seuilMinimal")
        );

        statusColumn.setCellValueFactory(
            new PropertyValueFactory<>("statutStock")
        );

        stockTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }

    private void updateStatistics() {

        int totalProducts = products.size();
        int lowStock = 0;
        int outOfStock = 0;

        BigDecimal stockValue = BigDecimal.ZERO;

        for (Produit produit : products) {

            stockValue = stockValue.add(
                produit.getPrixVente().multiply(
                    BigDecimal.valueOf(produit.getQuantite())
                )
            );

            if (produit.getQuantite() == 0) {
                outOfStock++;

            } else if (
                produit.getQuantite() <= produit.getSeuilMinimal()
            ) {
                lowStock++;
            }
        }

        totalProductsLabel.setText(
            String.valueOf(totalProducts)
        );

        stockValueLabel.setText(
            stockValue.toPlainString() + " MAD"
        );

        lowStockLabel.setText(
            String.valueOf(lowStock)
        );

        outOfStockLabel.setText(
            String.valueOf(outOfStock)
        );

        stockTable.refresh();
    }
}
