
package com.gestion.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.gestion.model.MouvementStock;
import com.gestion.model.Produit;
import com.gestion.service.StockService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @FXML private TableView<MouvementStock> movementTable;

    @FXML private TableColumn<MouvementStock, String> movementDateColumn;
    @FXML private TableColumn<MouvementStock, String> movementReferenceColumn;
    @FXML private TableColumn<MouvementStock, String> movementProductColumn;
    @FXML private TableColumn<MouvementStock, String> movementTypeColumn;
    @FXML private TableColumn<MouvementStock, String> movementQuantityColumn;
    @FXML private TableColumn<MouvementStock, String> movementReasonColumn;

    private final ObservableList<MouvementStock> movementData =
            FXCollections.observableArrayList();

    private static final DateTimeFormatter MOVEMENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        configureTable();
        configureMovementTable();

        products = ProductsController.getProducts();

        stockTable.setItems(products);

        updateStatistics();

        products.addListener(
            (ListChangeListener<Produit>) change ->
                updateStatistics()
        );
    }

    private void configureMovementTable() {

        movementDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getDateMouvement()
                                .format(MOVEMENT_DATE_FORMAT)
                )
        );

        movementReferenceColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getProduit().getReference()
                )
        );

        movementProductColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getProduit().getNom()
                )
        );

        movementTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getType() == com.gestion.model.TypeMouvement.ENTREE
                                ? "Entrée"
                                : "Sortie"
                )
        );

        movementQuantityColumn.setCellValueFactory(cell -> {
            MouvementStock mouvement = cell.getValue();

            String signe = mouvement.getType()
                    == com.gestion.model.TypeMouvement.ENTREE ? "+" : "-";

            return new ReadOnlyStringWrapper(
                    signe + mouvement.getQuantite()
            );
        });

        movementReasonColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getMotif()
                )
        );

        movementTable.setItems(movementData);

        movementTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        refreshMovementHistory();
    }
        private void refreshMovementHistory() {
        movementData.setAll(stockService.getAllMouvements());
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

        statusColumn.setCellFactory(column -> new TableCell<Produit, String>() {

            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);

                if (empty || statut == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(statut);

                badge.setStyle(
                    "-fx-padding: 5px 12px;" +
                    "-fx-background-radius: 20px;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
                );

                switch (statut) {

                    case "Disponible":
                        badge.setStyle(badge.getStyle() +
                            "-fx-background-color: #E5F3EB;" +
                            "-fx-text-fill: #23845B;");
                        break;

                    case "Stock faible":
                        badge.setStyle(badge.getStyle() +
                            "-fx-background-color: #FFF3DF;" +
                            "-fx-text-fill: #D99132;");
                        break;

                    case "Rupture":
                        badge.setStyle(badge.getStyle() +
                            "-fx-background-color: #FCEAEA;" +
                            "-fx-text-fill: #C74747;");
                        break;
                }

                setText(null);
                setGraphic(badge);
            }
        });

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


    @FXML
    private void openEntryForm() {
        ouvrirFormulaireMouvement(true);
    }

    @FXML
    private void openExitForm() {
        ouvrirFormulaireMouvement(false);
    }

    private void ouvrirFormulaireMouvement(boolean entree) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestion/views/StockMovementForm.fxml")
            );

            Parent root = loader.load();

            StockMovementFormController controller = loader.getController();
            controller.configurer(entree);

            Stage stage = new Stage();
            stage.setTitle(entree ? "Entrée de stock" : "Sortie de stock");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(stockTable.getScene().getWindow());
            Scene scene = new Scene(root);

            // Récupérer les styles CSS de la fenêtre principale
            scene.getStylesheets().setAll(
                    stockTable.getScene().getStylesheets()
            );

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isValide()) {
                Produit produit = controller.getProduit();
                int quantite = controller.getQuantite();
                String motif = controller.getMotif();

                if (entree) {
                    stockService.entreeStock(produit, quantite, motif);
                } else {
                    stockService.sortieStock(produit, quantite, motif);
                }

                stockTable.refresh();
                updateStatistics();
                refreshMovementHistory();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        }
    }

    private void afficherErreur(String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.WARNING
                );

        alert.setTitle("Mouvement de stock");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private final StockService stockService = StockService.getInstance();
}
