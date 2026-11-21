package com.gestion.controller;

import java.io.IOException;

import com.gestion.navigation.NavigationManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button productsNavButton;

    @FXML
    private Button categoriesNavButton;

    @FXML
    private Button dashboardNavButton;

    private NavigationManager navigationManager;

    @FXML
    public void initialize() {

        navigationManager = new NavigationManager(contentArea);

        try {
            navigationManager.navigateTo("Dashboard");
            setActiveButton(dashboardNavButton);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Navigation initialisée !");
    }

    @FXML
    private void showDashboard() throws IOException {

        navigationManager.navigateTo("Dashboard");
        setActiveButton(dashboardNavButton);

        System.out.println("Dashboard ouvert !");
    }

    @FXML
    private void showProducts() throws IOException {

        navigationManager.navigateTo("Products");
        setActiveButton(productsNavButton);

        System.out.println("Page Produits ouverte !");
    }

    @FXML
    private void showCategories() throws IOException {

        navigationManager.navigateTo("CategoryManagement");
        setActiveButton(categoriesNavButton);

        System.out.println("Page Catégories ouverte !");
    }


    private void setActiveButton(Button activeButton) {

        productsNavButton.getStyleClass().remove("nav-button-active");
        categoriesNavButton.getStyleClass().remove("nav-button-active");
        dashboardNavButton.getStyleClass().remove("nav-button-active");
        clientsNavButton.getStyleClass().remove("nav-button-active");
        suppliersNavButton.getStyleClass().remove("nav-button-active");
        stockNavButton.getStyleClass().remove("nav-button-active");
        salesNavButton.getStyleClass().remove("nav-button-active");
        purchasesNavButton.getStyleClass().remove("nav-button-active");

        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }


    @FXML
    private Button stockNavButton;

    @FXML
    private void showStock() throws IOException {

        navigationManager.navigateTo("Stock");
        setActiveButton(stockNavButton);

        System.out.println("Page Stock ouverte !");
    }

    @FXML
    private Button clientsNavButton;
    @FXML
    private void showClients() throws IOException {

        navigationManager.navigateTo("Clients");
        setActiveButton(clientsNavButton);

        System.out.println("Page Clients ouverte !");
    }

    @FXML
    private Button suppliersNavButton;
    @FXML
    private void showSuppliers() throws IOException {
        navigationManager.navigateTo("Suppliers");
        setActiveButton(suppliersNavButton);
    }


    @FXML
    private Button salesNavButton;
    @FXML
    private void showSales() throws IOException {

        navigationManager.navigateTo("Sales");
        setActiveButton(salesNavButton);

        System.out.println("Page Ventes ouverte !");
    }

    @FXML
    private Button purchasesNavButton;

    @FXML
    private void showPurchases() throws IOException {

        navigationManager.navigateTo("Achats");

        setActiveButton(purchasesNavButton);

        System.out.println("Page Achats ouverte !");
    }
}
