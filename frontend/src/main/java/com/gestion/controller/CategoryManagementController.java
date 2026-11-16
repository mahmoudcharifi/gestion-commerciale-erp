package com.gestion.controller;

import java.io.IOException;

import com.gestion.model.Categorie;
import com.gestion.service.CategorieService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;



public class CategoryManagementController {

    @FXML
    private TableView<Categorie> categoriesTable;

    @FXML
    private TableColumn<Categorie, Long> idColumn;

    @FXML
    private TableColumn<Categorie, String> nameColumn;

    @FXML
    private TableColumn<Categorie, String> descriptionColumn;

    @FXML
    private TableColumn<Categorie, Void> actionsColumn;

    private final CategorieService categorieService =
        CategorieService.getInstance();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );

        nameColumn.setCellValueFactory(
            new PropertyValueFactory<>("nom")
        );

        descriptionColumn.setCellValueFactory(
            new PropertyValueFactory<>("description")
        );

        categoriesTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        configureActionsColumn();
        refreshCategories();
    }

    private void configureActionsColumn() {

        actionsColumn.setCellFactory(column -> new TableCell<>() {

            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            private final HBox container =
                    new HBox(8, editButton, deleteButton);

            {
                editButton.getStyleClass().add("secondary-button");
                deleteButton.getStyleClass().add("danger-button");

                container.setAlignment(Pos.CENTER);

                editButton.setOnAction(event -> {
                    Categorie categorie =
                            getTableView().getItems().get(getIndex());

                    editCategory(categorie);
                });

                deleteButton.setOnAction(event -> {
                    Categorie categorie =
                            getTableView().getItems().get(getIndex());

                    deleteCategory(categorie);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : container);
            }
        });
    }

    // Ouvrir le formulaire pour ajouter une catégorie
    @FXML
    private void addCategory() {
        openCategoryForm(null);
    }

    // Ouvrir le formulaire pour modifier une catégorie
    private void editCategory(Categorie categorie) {
        openCategoryForm(categorie);
    }

    // Formulaire commun : ajout et modification
    private void openCategoryForm(Categorie categorie) {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/com/gestion/views/CategoryForm.fxml"
                )
            );

            Parent root = loader.load();

            CategoryFormController controller =
                    loader.getController();

            controller.setCategorieService(categorieService);

            if (categorie == null) {
                controller.configureForAdd();
            } else {
                controller.configureForEdit(categorie);
            }

            Stage stage = new Stage();

            stage.setTitle(
                categorie == null
                    ? "Nouvelle catégorie"
                    : "Modifier une catégorie"
            );

            stage.initModality(Modality.APPLICATION_MODAL);

            Window owner = categoriesTable.getScene().getWindow();
            stage.initOwner(owner);

            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                getClass().getResource(
                    "/com/gestion/css/theme-light.css"
                ).toExternalForm(),

                getClass().getResource(
                    "/com/gestion/css/components.css"
                ).toExternalForm(),

                getClass().getResource(
                    "/com/gestion/css/layout.css"
                ).toExternalForm(),

                getClass().getResource(
                    "/com/gestion/css/tables.css"
                ).toExternalForm()
            );

            stage.setScene(scene);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.showAndWait();

            if (controller.isSaved()) {
                refreshCategories();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir le formulaire.");
        }
    }

    // Suppression (ancienne version, nous la moderniserons ensuite)

    private void deleteCategory(Categorie selectedCategory) {

        boolean confirmed = showDeleteConfirmation(selectedCategory);

        if (!confirmed) {
            return;
        }

        try {
            boolean categorieUtilisee =
                    ProductsController.isCategoryUsed(
                        selectedCategory.getNom()
                    );

            categorieService.deleteCategorie(
                selectedCategory.getId(),
                categorieUtilisee
            );

            refreshCategories();

            showSuccess("Catégorie supprimée avec succès !");

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }


    private void refreshCategories() {
        categoriesTable.getItems().setAll(
            categorieService.getAllCategories()
        );
    }

    private void showSuccess(String message) {
        showCategoryMessage("Succès", message, true);
    }

    private void showError(String message) {
        showCategoryMessage("Opération impossible", message, false);
    }


    private boolean showDeleteConfirmation(Categorie categorie) {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/com/gestion/views/CategoryDeleteConfirmation.fxml"
                )
            );

            Parent root = loader.load();

            CategoryDeleteConfirmationController controller =
                    loader.getController();

            controller.setCategoryName(categorie.getNom());

            Stage stage = new Stage();
            stage.setTitle("Confirmation de suppression");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(categoriesTable.getScene().getWindow());

            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                getClass().getResource("/com/gestion/css/theme-light.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/components.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/layout.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/tables.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            return controller.isConfirmed();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showCategoryMessage(
            String title,
            String message,
            boolean success
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/com/gestion/views/CategoryMessage.fxml"
                )
            );

            Parent root = loader.load();

            CategoryMessageController controller =
                    loader.getController();

            controller.setMessage(title, message, success);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(categoriesTable.getScene().getWindow());

            Scene scene = new Scene(root);

            scene.getStylesheets().addAll(
                getClass().getResource("/com/gestion/css/theme-light.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/components.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/layout.css").toExternalForm(),
                getClass().getResource("/com/gestion/css/tables.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
