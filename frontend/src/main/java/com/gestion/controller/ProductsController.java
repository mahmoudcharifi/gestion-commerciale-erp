package com.gestion.controller;

import java.io.IOException;
import java.math.BigDecimal;

import com.gestion.model.Produit;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;







public class ProductsController {

    @FXML
    private TableView<Produit> productsTable;

    @FXML
    private TableColumn<Produit, String> referenceColumn;

    @FXML
    private TableColumn<Produit, String> nameColumn;

    @FXML
    private TableColumn<Produit, String> categoryColumn;

    @FXML
    private TableColumn<Produit, BigDecimal> priceColumn;

    @FXML
    private TableColumn<Produit, Integer> quantityColumn;

    @FXML
    private TableColumn<Produit, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalProductsLabel;

    private static final ObservableList<Produit> products =
        FXCollections.observableArrayList();

    private FilteredList<Produit> filteredProducts;

    private static void initialiserProduitsDemo() {
        if (products.isEmpty()) {
            products.addAll(
                new Produit(
                    "PRD-001",
                    "Ordinateur HP",
                    "Informatique",
                    new BigDecimal("5500.00"),
                    12
                ),
                new Produit(
                    "PRD-002",
                    "Souris Logitech",
                    "Accessoires",
                    new BigDecimal("150.00"),
                    35
                ),
                new Produit(
                    "PRD-003",
                    "Clavier Dell",
                    "Accessoires",
                    new BigDecimal("250.00"),
                    20
                )
            );
        }
    }


    @FXML
    public void initialize() {

       assurerInitialisation();

        // 2. Configuration des colonnes
        referenceColumn.setCellValueFactory(
                new PropertyValueFactory<>("reference")
        );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("nom")
        );

        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("categorie")
        );

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("prixVente")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantite")
        );


        // 4. Recherche
        filteredProducts = new FilteredList<>(
                products,
                produit -> true
        );

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    String search = newValue == null
                            ? ""
                            : newValue.toLowerCase().trim();

                    filteredProducts.setPredicate(produit -> {

                        if (search.isEmpty()) {
                            return true;
                        }

                        return produit.getNom()
                                .toLowerCase()
                                .contains(search)
                                || produit.getReference()
                                .toLowerCase()
                                .contains(search);
                    });
                }
        );

        productsTable.setItems(filteredProducts);

        productsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        // 5. Boutons Modifier et Supprimer dans chaque ligne
        configureActionsColumn();

        // Hauteur des lignes
        productsTable.setFixedCellSize(48);

        // Hauteur dynamique selon le nombre de produits
        updateTableHeight();

        filteredProducts.addListener(
                (ListChangeListener<Produit>) change -> updateTableHeight()
        );

        updateTableHeight();
        updateProductCount();

        System.out.println("Tableau des produits initialisé !");
    }


    // =====================================
    // CONFIGURATION DE LA COLONNE ACTIONS
    // =====================================

    private void configureActionsColumn() {

        actionsColumn.setCellFactory(column -> new TableCell<>() {

            private final Button editButton =
                    new Button("Modifier");

            private final Button deleteButton =
                    new Button("Supprimer");

            private final HBox buttons =
                    new HBox(8, editButton, deleteButton);

            {
                editButton.getStyleClass().addAll(
                        "button",
                        "secondary-button"
                );

                deleteButton.getStyleClass().addAll(
                        "button",
                        "danger-button"
                );

                editButton.setOnAction(event -> {

                    Produit produit = getTableView()
                            .getItems()
                            .get(getIndex());

                    try {
                        openEditProductForm(produit);
                    } catch (IOException e) {
                        showError("Impossible d'ouvrir le formulaire.");
                    }
                });

                deleteButton.setOnAction(event -> {

                    Produit produit = getTableView()
                            .getItems()
                            .get(getIndex());

                    deleteProduct(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }


    // =====================================
    // AJOUTER UN PRODUIT
    // =====================================

    @FXML
    private void openProductForm() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/gestion/views/ProductForm.fxml"
                )
        );

        Parent root = loader.load();

        ProductFormController controller = loader.getController();

        Stage formStage = new Stage();

        controller.setStage(formStage);

        controller.setOnProductCreated(produit -> {
            products.add(produit);
            updateProductCount();
        });

        formStage.setTitle("Ajouter un produit");
        formStage.setScene(new Scene(root));
        formStage.setResizable(false);
        formStage.showAndWait();
    }


    // =====================================
    // MODIFIER UN PRODUIT
    // =====================================

    private void openEditProductForm(Produit selectedProduct)
            throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/gestion/views/ProductForm.fxml"
                )
        );

        Parent root = loader.load();

        ProductFormController controller = loader.getController();

        Stage formStage = new Stage();

        controller.setStage(formStage);
        controller.setProductToEdit(selectedProduct);

        controller.setOnProductCreated(produit -> {
            productsTable.refresh();
        });

        formStage.setTitle("Modifier un produit");
        formStage.setScene(new Scene(root));
        formStage.setResizable(false);
        formStage.showAndWait();
    }


    // =====================================
    // SUPPRIMER UN PRODUIT
    // =====================================

    private void deleteProduct(Produit selectedProduct) {

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le produit ?");
        confirmation.setContentText(
                "Voulez-vous vraiment supprimer : "
                + selectedProduct.getNom() + " ?"
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                products.remove(selectedProduct);

                updateProductCount();

                System.out.println(
                        "Produit supprimé avec succès !"
                );
            }
        });
    }


    // =====================================
    // ACTUALISER LE COMPTEUR
    // =====================================

    private void updateProductCount() {

        totalProductsLabel.setText(
                String.valueOf(products.size())
        );
    }


    // =====================================
    // HAUTEUR DYNAMIQUE DU TABLEAU
    // =====================================

    private void updateTableHeight() {

        double headerHeight = 45;
        double rowHeight = 48;

        double height = headerHeight
                + (filteredProducts.size() * rowHeight)
                + 5;

        height = Math.max(height, 100);

        productsTable.setPrefHeight(height);
        productsTable.setMinHeight(height);
        productsTable.setMaxHeight(height);

    }


    // =====================================
    // AFFICHER UNE ERREUR
    // =====================================

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void openCategoryManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gestion/views/CategoryManagement.fxml")
            );


            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                getClass().getResource("/com/gestion/css/theme-light.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/components.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/layout.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/tables.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("Gestion des catégories");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(850);
            stage.setMinHeight(580);
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCategoryUsed(String categoryName) {

        return products.stream().anyMatch(
            produit -> produit.getCategorie().equalsIgnoreCase(categoryName)
        );
    }


    public static ObservableList<Produit> getProducts() {
        return FXCollections.unmodifiableObservableList(products);
    }

    public static void assurerInitialisation() {
        initialiserProduitsDemo();
    }
}
